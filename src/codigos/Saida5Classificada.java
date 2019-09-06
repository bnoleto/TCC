package codigos;
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


import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.apache.commons.math3.util.Precision;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.datavec.RecordReaderMultiDataSetIterator;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.evaluation.curves.PrecisionRecallCurve;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.MultiDataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.MultiDataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class Saida5Classificada {

	
    public static void main(String[] args) throws Exception {
    	
    	String modo = "CONSOLE";
    	
    	for (String arg : args) {
    		arg = arg.toUpperCase().trim();
    		
    		if(arg.equals("-WEB")) {
    			modo = "WEB";
    		} else if(arg.equals("-CONSOLE")) {
    			modo = "CONSOLE";
    		} else {
    			
    		}
    	}
    	
    	
    	String dataLocalPath = System.getProperty("user.dir") + "\\src\\resources\\" ;
    	
        int seed = 123;
        double learningRate = 0.01;
        int batchSize = 1;
        
        // TODO: alterar para 100*batchSize para ver at� onde o score cai (prov�vel boa converg�ncia)
        int nEpochs = 1;

        int numInputs = 3;
        int numOutputs = 5;
        int numHiddenNodes = 30;
        
        double chartStep = (double)nEpochs/1000;
        
        
     
        //new PreProcessadorCSV(dataLocalPath, "treinamento_tb_amostras_final_201908251648.csv").processar();
        //new PreProcessadorCSV(dataLocalPath, "validacao_tb_amostras_final_201908251648.csv").processar();

        RecordReader rr = new CSVRecordReader();
        rr.initialize(new FileSplit(new File(dataLocalPath,"processado_treinamento_tb_amostras_final_201908251648.csv")));
        //MultiDataSetIterator trainIter = new RecordReaderMultiDataSetIterator(rr,batchSize,3,5);
        MultiDataSetIterator trainIter = new RecordReaderMultiDataSetIterator.Builder(batchSize)
        		.addReader("dataset", rr)
        		.addInput("dataset", 0, 2)
        		.addOutputOneHot("dataset", 3, 5)
        		.build();
        
        RecordReader rrTest = new CSVRecordReader();
        rrTest.initialize(new FileSplit(new File(dataLocalPath,"processado_validacao_tb_amostras_final_201908251648.csv")));
        //DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,3,5);
        MultiDataSetIterator testIter = new RecordReaderMultiDataSetIterator.Builder(batchSize)
        		.addReader("dataset", rrTest)
        		.addInput("dataset", 0, 2)
        		.addOutputOneHot("dataset", 3, 5)
        		.build();
        
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(learningRate))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(numHiddenNodes).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs)
                        .build())
                .build();
        
        
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
		
		ArrayList<ArrayList<Double>> dados_treinamento = new ArrayList<ArrayList<Double>>();
		// 0: epoca, 1: score, 2: deltascore
		
		TrainingListener t1 = new TrainingListener() {
			
			@Override
			public void onGradientCalculation(Model model) {
				
				
			}
			
			@Override
			public void onForwardPass(Model model, Map<String, INDArray> activations) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onForwardPass(Model model, List<INDArray> activations) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEpochStart(Model model) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onEpochEnd(Model model) {
				Evaluation eval = new Evaluation(5);
				
				
				
				INDArray[] features = trainIter.next().getFeatures();
				INDArray[] labels = trainIter.next().getLabels();
				
				eval.evalTimeSeries(labels[0], features[0]);
				
							
			}
			
			@Override
			public void onBackwardPass(Model model) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void iterationDone(Model model, int iteration, int epoch) {
				System.out.println("�poca " + epoch + " / Itera��o " + iteration + " / Score: " + model.score());
				
				ArrayList<Double> iteracao_atual = new ArrayList<Double>();
				
				iteracao_atual.add((double)epoch);
				iteracao_atual.add((double)model.score());
				iteracao_atual.add((double)iteration);
				
				dados_treinamento.add(iteracao_atual);
				
			}
		};		
		
		if(modo.equals("CONSOLE")) {
			model.setListeners(t1);  //print the score with every iteration
		}
	
        
		
		
		System.out.println(model.summary());
		
        System.out.println("Realizando treinamento....");
        
        model.fit(trainIter, nEpochs);

        System.out.println("Gerando an�lise do modelo da rede....");
      /*
        Evaluation eval = new Evaluation(5);
        
        while(testIter.hasNext()){
        	MultiDataSet next = testIter.next();
        	eval.eval(next.getLabels(0), next.getFeatures(0));
        }
        
        
        
        System.out.println(eval.stats());
        System.out.println("== Falsos Negativos ==");
        for(int i = 0; i<5; i++) {
        	System.out.println(i+": "+eval.falseNegativeRate(i));	
        }
        System.out.println("== Falsos Positivos ==");
        for(int i = 0; i<5; i++) {
        	System.out.println(i+": "+eval.falsePositiveRate(i));	
        }
        */
        // PLOTAR� O GR�FICO
        
        
        XYSeries series1 = new XYSeries("Score");
//      XYSeries series2 = new XYSeries("Precis�o");
        
        for(int i = 0; i < dados_treinamento.size(); i++) {
        	series1.add(dados_treinamento.get(i).get(2), dados_treinamento.get(i).get(1));
//        	series2.add(dados_treinamento.get(i).get(0), dados_treinamento.get(i).get(2));
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);
//        dataset.addSeries(series2);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Dados do treinamento", 
                "Itera��es", 
                "Score", 
                dataset, 
                PlotOrientation.VERTICAL,
                true, 
                false, 
                false 
        );
         
        chart.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF, 0));

        XYPlot plot = (XYPlot)chart.getPlot();
        
        plot.setRenderer(new SamplingXYLineRenderer());

        plot.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF, 0));
        plot.setOutlinePaint(new Color(0x00, 0x00, 0x00, 0));
        plot.setInsets(new RectangleInsets(0, 1, 0, 0));
        plot.setDomainGridlinePaint(new Color(0x00, 0x00, 0x00, 0x40));
        plot.setRangeGridlinePaint(new Color(0x00, 0x00, 0x00, 0x40));
        
        ChartUtils.saveChartAsPNG(new File("treinamento.png"), chart, 640, 480);
        
        SVGGraphics2D g2 = new SVGGraphics2D(640, 480);
        Rectangle r = new Rectangle(0, 0, 640, 480);
        chart.draw(g2, r);
        
        File f = new File("treinamento.svg");
        SVGUtils.writeToSVG(f, g2.getSVGElement());
        
        System.out.println("****************FINALIZADO********************");
    }
}
