/*******************************************************************************
 * Copyright (c) 2015-2019 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ******************************************************************************/


import java.io.File;
import java.util.List;

import javax.swing.text.Position.Bias;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.datavec.spark.transform.misc.WritablesToStringFunction;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
//import org.deeplearning4j.examples.download.DownloaderUtility;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.IOException;
import preprocessamento.PreProcessadorCSV;

public class Principal {

	private static DataSet readCSVDataset(
        String arquivo, int batchSize, int labelIndex, int numClasses)
        throws IOException, InterruptedException {

        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new File(arquivo)));
        DataSetIterator iterator = new RecordReaderDataSetIterator(rr, batchSize, labelIndex, numClasses);
        return iterator.next();
    }
	
    public static void main(String[] args) throws Exception {
    	
    	String modo = "";
    	
    	for (String arg : args) {
    		arg = arg.toUpperCase().trim();
    		
    		if(arg.equals("-WEB")) {
    			modo = "WEB";
    		} else if(arg.equals("-CONSOLE")) {
    			modo = "CONSOLE";
    		} else {
    			System.out.println("java Principal");
    		}
    	}
    	
    	
    	String dataLocalPath = System.getProperty("user.dir") + "\\" ;
    	
        int seed = 123;
        double learningRate = 0.01;
        int batchSize = 5;
        int nEpochs = 100;

        int numInputs = 3;
        int numOutputs = 2;
        int numHiddenNodes = 27;
        
        
     
        //new PreProcessadorCSV(dataLocalPath, "treinamento_tb_amostras_final_201908251648.csv").processar();
        //new PreProcessadorCSV(dataLocalPath, "validacao_tb_amostras_final_201908251648.csv").processar();

        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new File(dataLocalPath,"treinamento_teste.csv")));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,3,2);
        
        RecordReader rrTest = new CSVRecordReader();
        rrTest.initialize(new FileSplit(new File(dataLocalPath,"validacao_teste.csv")));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,3,2);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.001))
                .biasInit(-1)
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes/3)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes/3).nOut(numHiddenNodes/9)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes/9).nOut(numOutputs)
                        .build())
                .build();
        
        conf.setBackpropType(BackpropType.Standard);

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

		if(modo.equals("WEB")) {
	        //Initialize the user interface backend
	        UIServer uiServer = UIServer.getInstance();

	        //Configure where the network information (gradients, activations, score vs. time etc) is to be stored
	        //Then add the StatsListener to collect this information from the network, as it trains
	        StatsStorage statsStorage = new FileStatsStorage(new File(System.getProperty("java.io.tmpdir"), "ui-stats.dl4j"));
	        int listenerFrequency = 1;
	        model.setListeners(new StatsListener(statsStorage, listenerFrequency));

	        //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
	        uiServer.attach(statsStorage);
		}
		if(modo.equals("CONSOLE")) {
			model.setListeners(new ScoreIterationListener(5));  //print the score with every iteration
		}
        
        System.out.println("Train model....");
        model.fit( trainIter, nEpochs );

        System.out.println("Evaluate model....");
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            DataSet t = testIter.next();
            INDArray features = t.getFeatures();
            INDArray lables = t.getLabels();
            INDArray predicted = model.output(features,false);

            eval.eval(lables, predicted);

        }
        
        System.out.println(eval.stats());
        
        System.out.println("****************FINALIZADO********************");
    }
}
