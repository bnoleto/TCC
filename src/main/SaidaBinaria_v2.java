package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import estatisticas.NoletoGrafico;
import utils.Horario;
import utils.ModoExecucao;

public class SaidaBinaria_v2 {

	public static ArrayList<Double[]> ArrayListNomeado(String nome){
		
		return new ArrayList<Double[]>() {

			private static final long serialVersionUID = 1L;

			
			@Override
        	public String toString(){
                return nome;
            }
        };
		
	}
	
    public static void main(String[] args) throws Exception {
    	
    	// iniciar do zero / continuar de onde parou o treinamento
    	ModoExecucao modo = ModoExecucao.CONTINUAR;
    	
    	String nome_rede = "08092019_2127";
    	
    	File dir = null;

    	String dataLocalPath = System.getProperty("user.dir") + "\\src\\resources\\" ;

        int batchSize = 128;
        
        //20000 épocas por enquanto é o melhor
        int nEpochs = 4000;

        int numOutputs = 2;

        MultiLayerNetwork model = null;

        ArrayList<Double[]> stats_treinamento = null;
        ArrayList<Double[]> stats_teste = null;
        
        // dados para serem adicionados ao gráfico ao final
        ArrayList<ArrayList<Double[]>> dados = null;
        
        RecordReader rrTest = new CSVRecordReader(0, ',');
        rrTest.initialize(new FileSplit(new File(dataLocalPath,"validacao_teste_binarizado.csv")));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,3,2);
        
        RecordReader rr = new CSVRecordReader(0, ',');
        rr.initialize(new FileSplit(new File(dataLocalPath,"treinamento_teste_binarizado.csv")));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,3,2);

        if(modo == ModoExecucao.COMECAR) {

        	int seed = 154;
        	double learningRate = 0.001;
        	
        	int numInputs = 3;
            
            int numHiddenNodes = 36;
            int maxCamadasOcultas = 10;
        	
        	nome_rede = Horario.getDiaHora();
        	
        	// dados para serem adicionados ao gráfico ao final
        	dados = new ArrayList<ArrayList<Double[]>>();
            
        	stats_treinamento = ArrayListNomeado("Dataset de Treinamento");
            stats_teste = ArrayListNomeado("Dataset de Teste");

        	dados.add(stats_treinamento);
            dados.add(stats_teste);
            
            // parte inicial da rede
            ListBuilder b1 = new NeuralNetConfiguration.Builder()
            		.seed(seed)
                    .weightInit(WeightInit.XAVIER)
                    .updater(new Adam(learningRate))
                    .list()
                    .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                            .activation(Activation.RELU)
                            .build());
            
            // camadas ocultas
            double divisor = 1.2;
            int n_entrada = numHiddenNodes;
            int n_saida = numHiddenNodes;
            int qtd_camadasOcultas = 0;
            while((int) n_entrada/divisor > numOutputs && qtd_camadasOcultas < maxCamadasOcultas) {
            	
            	n_saida = (int) (n_entrada/divisor);
            	
            	b1.layer(new DenseLayer.Builder().nIn(n_entrada).nOut(n_saida)
                        .activation(Activation.RELU)
                        .build());
            	
            	qtd_camadasOcultas++;
            	
            	n_entrada = (int) (n_entrada/divisor);
            	
            }
            
            // camada de saída
            b1.layer(new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                    .activation(Activation.SOFTMAX)
                    .nIn(n_saida).nOut(numOutputs)
                    .build());

            MultiLayerConfiguration conf = b1.build();
            
        	model = new MultiLayerNetwork(conf);
        	
        	model.init();
        	
        	FileUtils.writeStringToFile(new File(System.getProperty("user.dir")+"\\redes\\"+nome_rede+"\\","config.json"), conf.toJson(), Charset.forName("UTF-8"));
        	

        } else if (modo == ModoExecucao.CONTINUAR) {
        	
        	model = MultiLayerNetwork.load(new File(System.getProperty("user.dir")+"\\redes\\"+nome_rede+"\\","rede.nn"), true);
        	
        	FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
            ObjectInputStream ois = new ObjectInputStream(fis);
            dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
            ois.close();
            
            stats_treinamento = dados.get(0);
            stats_teste = dados.get(1);

        }

        dir = new File(System.getProperty("user.dir") + "\\redes\\" +nome_rede+"\\");
        dir.mkdirs();
        
        System.out.println("Train model....");
        //model.setListeners(new ScoreIterationListener(10)); //Print score every 10 iterations
        for( int i=0; i<nEpochs; i++ ) {
            model.fit(trainIter);

         // Estatísicas com o dataset de treinamento
            Evaluation eval = model.evaluate(trainIter);
            System.out.println(eval.stats());
            System.out.println("*** Fim da Época " + (i+1) + "/" + nEpochs +" ***" );
            DataSetLossCalculator lc = new DataSetLossCalculator(trainIter, true);
            stats_treinamento.add(new Double[] {eval.accuracy(),eval.precision(),eval.recall(),eval.f1(),lc.calculateScore(model)});
            trainIter.reset();
            
            
            
            // Guardará estatísicas com o dataset de teste
            Evaluation evalTeste = model.evaluate(testIter);
            lc = new DataSetLossCalculator(testIter, true);
            stats_teste.add(new Double[] {evalTeste.accuracy(),evalTeste.precision(),evalTeste.recall(),evalTeste.f1(),lc.calculateScore(model)});
            testIter.reset();
            
        }
        
        
        
        model.save(new File(dir,"rede.nn"), true);
        
        System.out.println("Gerando análise do modelo da rede....");
        
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            DataSet t = testIter.next();
            INDArray features = t.getFeatures();
            INDArray labels = t.getLabels();
            INDArray predicted = model.output(features,false);

            eval.eval(labels, predicted);

        }
        
        System.out.println(eval.stats());
        
        FileOutputStream fos = new FileOutputStream(dir+"\\estatisticas.stats");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(dados);
        oos.close();

        NoletoGrafico.gerarGraficos(dados, dir);

        System.out.println("****************FINALIZADO********************");
    }
}
