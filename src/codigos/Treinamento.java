package codigos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
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
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import estatisticas.NoletoGrafico;
import utils.Horario;

public class Treinamento {
	
	private static int numSaidas;
	
	public static ArrayList<Double[]> ArrayListNomeado(String nome){
		
		return new ArrayList<Double[]>() {

			private static final long serialVersionUID = 1L;

			
			@Override
        	public String toString(){
                return nome;
            }
        };
	}
	
	public static void comecar_treinamento(int qtd_epocas) throws IOException, InterruptedException {
		
		int seed = 154;
    	double taxa_aprendizado = 0.1;
    	
    	int numEntradas = 4;
    	
    	numSaidas = 5;
        
        int numNosOcultos = 5;
        int maxCamadasOcultas = 4;
    	
    	String nome_rede = Horario.getDiaHora();
    	
    	// dados para serem adicionados ao gráfico ao final
    	ArrayList<ArrayList<Double[]>> dados = new ArrayList<ArrayList<Double[]>>();
    	
    	dados.add(ArrayListNomeado("Dataset de Treinamento"));
    	dados.add(ArrayListNomeado("Dataset de Teste"));

        // parte inicial da rede
        ListBuilder b1 = new NeuralNetConfiguration.Builder()
        		.seed(seed)
                .weightInit(WeightInit.XAVIER)
                //.updater(new Adam(learningRate))
                .updater(new Nesterovs(taxa_aprendizado, 0.01))
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .list()
                .layer(new DenseLayer.Builder().nIn(numEntradas).nOut(numNosOcultos)
                        .activation(Activation.GELU)
                        .build());
        
        // camadas ocultas
        double divisor = 1;
        int n_entrada = numNosOcultos;
        int n_saida = numNosOcultos;
        int qtd_camadasOcultas = 0;
        while((int) n_entrada/divisor > numSaidas && qtd_camadasOcultas < maxCamadasOcultas) {
        	
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
                .nIn(n_saida).nOut(numSaidas)
                .build());

        MultiLayerConfiguration conf = b1.build();
        
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
    	
    	model.init();
    	
    	FileUtils.writeStringToFile(new File(System.getProperty("user.dir")+"\\redes\\"+nome_rede+"\\","config.json"), conf.toJson(), Charset.forName("UTF-8"));
    	
    	treinamento(model, qtd_epocas, nome_rede, dados);

	}
	
	public static void continuar_treinamento(String nome_rede, int qtd_epocas) throws IOException, ClassNotFoundException, InterruptedException {
		
		MultiLayerNetwork model = MultiLayerNetwork.load(new File(System.getProperty("user.dir")+"\\redes\\"+nome_rede+"\\","rede.nn"), true);
    	
		numSaidas = 5;
		
    	FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<Double[]>> dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
        ois.close();
        
        treinamento(model,qtd_epocas,nome_rede,dados);
        
	}
	
	private static void treinamento(MultiLayerNetwork model, int nEpochs, String nome_rede, ArrayList<ArrayList<Double[]>> dados) throws IOException, InterruptedException {
		
		ArrayList<Double[]> stats_treinamento = dados.get(0);
		ArrayList<Double[]> stats_teste = dados.get(1);
		
		String diretorio_dataset = System.getProperty("user.dir") + "\\src\\resources\\" ;
		
		int batchSize = 4492;

		File dir = new File(System.getProperty("user.dir") + "\\redes\\" +nome_rede+"\\");
        dir.mkdirs();
        
        RecordReader rrTest = new CSVRecordReader(1, ',');
        rrTest.initialize(new FileSplit(new File(diretorio_dataset,"normalizado_validacao_tb_amostras_final_201908251648.csv")));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,4,5);
        
        RecordReader rr = new CSVRecordReader(1, ',');
        rr.initialize(new FileSplit(new File(diretorio_dataset,"normalizado_treinamento_tb_amostras_final_201908251648.csv")));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,4,5);
        
        double menor_loss = 0;
        boolean overfitting = false;
        
        System.out.println("Treinando modelo...");
        for( int i=0; i<nEpochs; i++ ) {
        	
            model.fit(trainIter);

            // Estatísicas com o dataset de treinamento
            Evaluation eval = model.evaluate(trainIter);
            System.out.println(eval.stats());
            System.out.println("*** Fim da Época " + i + "/" + nEpochs +" ***" );
            DataSetLossCalculator lc = new DataSetLossCalculator(trainIter, true);
            stats_treinamento.add(new Double[] {eval.accuracy(),eval.precision(),eval.recall(),eval.f1(),lc.calculateScore(model)});
            trainIter.reset();
            
            // Guardará estatísicas com o dataset de teste
            Evaluation evalTeste = model.evaluate(testIter);
            lc = new DataSetLossCalculator(testIter, true);
            stats_teste.add(new Double[] {evalTeste.accuracy(),evalTeste.precision(),evalTeste.recall(),evalTeste.f1(),lc.calculateScore(model)});
            
            if(i == 0) {
            	menor_loss = lc.calculateScore(model);
            } 
            
            if(overfitting == false){
            	if(lc.calculateScore(model) > menor_loss) {
            		
            		File dir2 = new File(System.getProperty("user.dir") + "\\redes\\" +nome_rede+"\\melhor\\");
        	        dir2.mkdirs();
            		
            		model.save(new File(dir2,"rede.nn"), true);
	            	
	            	FileOutputStream fos = new FileOutputStream(dir2+"\\estatisticas.stats");
	    	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	    	        oos.writeObject(dados);
	    	        oos.close();
	    	        
	    	        NoletoGrafico.gerarGraficos(dados, dir2);
	            	
	    	        System.out.println("Menor loss atingido, rede salva!");
	    	        
	    	        overfitting = true;
            		
            	} else {
            		menor_loss = lc.calculateScore(model);
            	}
            }
            
            testIter.reset();

            // salvar a rede a cada 1000 épocas e atualizar gráficos
            if(i % 1000 == 0 && i != 0) {
            	
            	System.out.println("Salvando rede...");
            	
            	model.save(new File(dir,"rede.nn"), true);
            	
            	FileOutputStream fos = new FileOutputStream(dir+"\\estatisticas.stats");
    	        ObjectOutputStream oos = new ObjectOutputStream(fos);
    	        oos.writeObject(dados);
    	        oos.close();
    	        
    	        NoletoGrafico.gerarGraficos(dados, dir);
    	        
    	        System.out.println("Salvo!");
            	
            }
            
        }
        

        model.save(new File(dir,"rede.nn"), true);
        
        System.out.println("Gerando análise do modelo da rede....");
        
        Evaluation eval = new Evaluation(numSaidas);
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
		
	}
}
