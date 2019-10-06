package codigos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import preprocessamento.PreProcessadorCSV;
import preprocessamento.calculo_risco_fogo.RF;
import utils.Horario;

public class Treinamento {
	
	private static int numSaidas = 5;
	private static int numEntradas = 4;
	private static int numNosOcultos = 5;
	private static int maxCamadasOcultas = 4;
	
	public static ArrayList<ArrayList<Double[]>> ArrayListNomeado(String nome){
		
		return new ArrayList<ArrayList<Double[]>>() {

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

    	String nome_rede = Horario.getDiaHora();
    	
    	// dados para serem adicionados ao gr�fico ao final
    	ArrayList<ArrayList<ArrayList<Double[]>>> dados = new ArrayList<ArrayList<ArrayList<Double[]>>>();
    	
    	dados.add(ArrayListNomeado("Dataset de Treinamento"));
    	dados.add(ArrayListNomeado("Dataset de Validação"));

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
        
        // camada de sa�da
        b1.layer(new OutputLayer.Builder(LossFunction.NEGATIVELOGLIKELIHOOD)
                .activation(Activation.SOFTMAX)
                .nIn(n_saida).nOut(numSaidas)
                .build());

        MultiLayerConfiguration conf = b1.build();
        
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
    	
    	model.init();
    	
    	
    	FileUtils.writeStringToFile(new File(System.getProperty("user.dir")+"/redes/"+nome_rede+"/","config.json"), conf.toJson(), Charset.forName("UTF-8"));
    	
    	FileSplit[] dataset = iniciar_dataset(nome_rede);
    	
    	treinamento(model, dataset, qtd_epocas, nome_rede, dados);

	}
	
	private static FileSplit[] iniciar_dataset(String nome_rede) throws IOException {
		
		FileSplit[] dataset = dividir_dataset("normalizado/filtrado_amostras_imperatriz_inmet.csv",nome_rede, 0.75);

		return dataset;
	}
	
	private static FileSplit[] abrir_dataset(String nome_rede) throws IOException, ClassNotFoundException {

		ArrayList<ArrayList<String>> dataset_treinamento = RF.csv_to_ArrayList(System.getProperty("user.dir")+"/redes/"+nome_rede+"/dataset_treinamento", 1);
		ArrayList<ArrayList<String>> dataset_validacao = RF.csv_to_ArrayList(System.getProperty("user.dir")+"/redes/"+nome_rede+"/dataset_validacao", 1);

        FileSplit fs_treinamento = new FileSplit(RF.arrayList_to_CSV(dataset_treinamento));
        FileSplit fs_validacao = new FileSplit(RF.arrayList_to_CSV(dataset_validacao));
		
		return new FileSplit[] {fs_treinamento,fs_validacao};

	}


	public static void continuar_treinamento(String nome_rede, int qtd_epocas) throws IOException, ClassNotFoundException, InterruptedException {
		
		MultiLayerNetwork model = MultiLayerNetwork.load(new File(System.getProperty("user.dir")+"/redes/"+nome_rede+"/","rede.nn"), true);
		
    	FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/redes/"+nome_rede+"/estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<ArrayList<Double[]>>> dados = (ArrayList<ArrayList<ArrayList<Double[]>>>) ois.readObject();
        ois.close();
        
        FileSplit[] dataset = abrir_dataset(nome_rede);
        
        treinamento(model,dataset,qtd_epocas,nome_rede,dados);

	}
	

	private static void treinamento(MultiLayerNetwork model, FileSplit[] dataset, int nEpochs, String nome_rede, ArrayList<ArrayList<ArrayList<Double[]>>> dados) throws IOException, InterruptedException {
		
		ArrayList<ArrayList<Double[]>> stats_treinamento = dados.get(0);
		ArrayList<ArrayList<Double[]>> stats_teste = dados.get(1);
		
		File dir = new File(System.getProperty("user.dir") + "/redes/" +nome_rede+"/");
        dir.mkdirs();

        int batchSize = (int) dataset[0].length();
        
        RecordReader rr = new CSVRecordReader(0, ',');
        rr.initialize(dataset[0]);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,numEntradas,numSaidas);
        
        RecordReader rrTest = new CSVRecordReader(0, ',');
        rrTest.initialize(dataset[1]);
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,numEntradas,numSaidas);

        boolean overfitting = false;
        
        double loss_train_anterior = 0, loss_valid_anterior = 0;
        double media_delta_loss = -99999;
        
        MultiLayerNetwork model_backup;
        
        System.out.println("Treinando modelo...");
        for( int i=1; i<=nEpochs; i++ ) {
        	
        	model_backup = model.clone();
        	
            model.fit(trainIter);

            // Estat�sicas com o dataset de treinamento
            Evaluation eval = model.evaluate(trainIter);
            System.out.println(eval.stats());
            System.out.println("*** Fim da época " + i + "/" + nEpochs +" ***" );
            double treinamento_loss_atual = new DataSetLossCalculator(trainIter, true).calculateScore(model);
            
            
            
            Double[] overall_tr = new Double[] {eval.accuracy(),eval.precision(),eval.recall(),eval.f1(),treinamento_loss_atual};
            Double[][] tr_classes = new Double[][] { 
            	new Double[] {eval.precision(0),eval.recall(0),eval.f1(0)},
            	new Double[] {eval.precision(1),eval.recall(1),eval.f1(1)},
            	new Double[] {eval.precision(2),eval.recall(2),eval.f1(2)},
            	new Double[] {eval.precision(3),eval.recall(3),eval.f1(3)},
            	new Double[] {eval.precision(4),eval.recall(4),eval.f1(4)},
            	};
            
            ArrayList<Double[]>stat_tr = new ArrayList<Double[]>();
            stat_tr.add(overall_tr);
            stat_tr.add(tr_classes[0]);
            stat_tr.add(tr_classes[1]);
            stat_tr.add(tr_classes[2]);
            stat_tr.add(tr_classes[3]);
            stat_tr.add(tr_classes[4]);
            
            stats_treinamento.add(stat_tr);
            trainIter.reset();
            
            // Guardar� estat�sicas com o dataset de teste
            Evaluation evalTeste = model.evaluate(testIter);
            
            double validacao_loss_atual = new DataSetLossCalculator(testIter, true).calculateScore(model);
            
            Double[] overall_val = new Double[] {evalTeste.accuracy(),evalTeste.precision(),evalTeste.recall(),evalTeste.f1(),validacao_loss_atual};
            Double[][] val_classes = new Double[][] { 
            	new Double[] {evalTeste.precision(0),evalTeste.recall(0),evalTeste.f1(0)},
            	new Double[] {evalTeste.precision(1),evalTeste.recall(1),evalTeste.f1(1)},
            	new Double[] {evalTeste.precision(2),evalTeste.recall(2),evalTeste.f1(2)},
            	new Double[] {evalTeste.precision(3),evalTeste.recall(3),evalTeste.f1(3)},
            	new Double[] {evalTeste.precision(4),evalTeste.recall(4),evalTeste.f1(4)},
            	};
            
            ArrayList<Double[]>stat_val = new ArrayList<Double[]>();
            stat_val.add(overall_val);
            stat_val.add(val_classes[0]);
            stat_val.add(val_classes[1]);
            stat_val.add(val_classes[2]);
            stat_val.add(val_classes[3]);
            stat_val.add(val_classes[4]);
            
            stats_teste.add(stat_val);


            if(overfitting == false){
            	
            	
            	//(validacao_loss_atual-loss_valid_anterior)
            	
            	media_delta_loss += (validacao_loss_atual-loss_valid_anterior);
            	
            	if(i % 50 == 0) {

            		if(i > 1 && media_delta_loss/50 > 0) {

                		File dir2 = new File(System.getProperty("user.dir") + "/redes/" +nome_rede+"/melhor/");
            	        dir2.mkdirs();
                		
                		model_backup.save(new File(dir2,"rede.nn"), true);
    	            	
    	            	FileOutputStream fos = new FileOutputStream(dir2+"/estatisticas.stats");
    	    	        ObjectOutputStream oos = new ObjectOutputStream(fos);
    	    	        oos.writeObject(dados);
    	    	        oos.close();
    	    	        
    	    	        NoletoGrafico.gerarGraficos(dados, dir2);
    	            	
    	    	        System.out.println("Menor loss atingido, rede salva!");
    	    	        
    	    	        overfitting = true;
                		
                	} else {
                		model_backup = model.clone();	
                	}
            		
            		media_delta_loss = 0;
            	}
				
            }
            
            testIter.reset();
            
            System.out.println("Delta_LOSS => Treinamento: " + (treinamento_loss_atual-loss_train_anterior) + " / Validação: " + (validacao_loss_atual-loss_valid_anterior));
            
            loss_train_anterior = treinamento_loss_atual;
            loss_valid_anterior = validacao_loss_atual;

            // salvar a rede a cada 1000 �pocas e atualizar gr�ficos
            if(i > 1 && i % 1000 == 0) {
            	
            	System.out.println("Salvando rede...");
            	
            	model.save(new File(dir,"rede.nn"), true);
            	
            	FileOutputStream fos = new FileOutputStream(dir+"/estatisticas.stats");
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
        
        FileOutputStream fos = new FileOutputStream(dir+"/estatisticas.stats");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(dados);
        oos.close();
        
        NoletoGrafico.gerarGraficos(dados, dir);
		
	}

	private static FileSplit[] dividir_dataset(String arquivo, String nome_rede, double razao_treinamento) throws IOException {
		
		String diretorio_dataset = System.getProperty("user.dir") + "/src/resources/datasets/";
		
		ArrayList<ArrayList<String>> dataset = RF.csv_to_ArrayList(diretorio_dataset+arquivo, 1);
		
		int i_final_treinamento = (int) (razao_treinamento*dataset.size());
		int i_final_validacao = dataset.size()-1;
		
		List<ArrayList<String>> treinamento = dataset.subList(0, i_final_treinamento+1);
		List<ArrayList<String>> validacao = dataset.subList(i_final_treinamento+1, i_final_validacao+1);

		
		Collections.shuffle(treinamento);
		
		treinamento = balancear(treinamento);

		ArrayList<ArrayList<String>> treinamento_output = new ArrayList<ArrayList<String>>();
		treinamento_output.addAll(treinamento);
		
		ArrayList<ArrayList<String>> validacao_output = new ArrayList<ArrayList<String>>();
		validacao_output.addAll(validacao);
		
		RF.salvarCSV(null, System.getProperty("user.dir") + "/redes/"+nome_rede+"/dataset_treinamento",treinamento_output);
		RF.salvarCSV(null, System.getProperty("user.dir") + "/redes/"+nome_rede+"/dataset_validacao",validacao_output);

        FileSplit fs_treinamento = new FileSplit(RF.arrayList_to_CSV(treinamento));
        FileSplit fs_validacao = new FileSplit(RF.arrayList_to_CSV(validacao));
		
		return new FileSplit[] {fs_treinamento,fs_validacao};
	}

	private static List<ArrayList<String>> balancear(List<ArrayList<String>> treinamento) {
				
		ArrayList<List<ArrayList<String>>> classes = new ArrayList<List<ArrayList<String>>>();
		
		classes.add(new ArrayList<ArrayList<String>>());
		classes.add(new ArrayList<ArrayList<String>>());
		classes.add(new ArrayList<ArrayList<String>>());
		classes.add(new ArrayList<ArrayList<String>>());
		classes.add(new ArrayList<ArrayList<String>>());
		
		for(ArrayList<String> amostra : treinamento) {
			if(amostra.get(4).equals("0")) {
				classes.get(0).add(amostra);
			} else if(amostra.get(4).equals("1")) {
				classes.get(1).add(amostra);
			} else if(amostra.get(4).equals("2")) {
				classes.get(2).add(amostra);
			} else if(amostra.get(4).equals("3")) {
				classes.get(3).add(amostra);
			} else if(amostra.get(4).equals("4")) {
				classes.get(4).add(amostra);
			}
		}
		
		int min = classes.get(0).size();
		
		List<ArrayList<String>> nova_list = new ArrayList<ArrayList<String>>();
		
		for(int i = 1; i < 5; i++) {
			if(classes.get(i).size() < min) {
				min = classes.get(i).size();
			}
		}
		
		for(int i = 0; i<5; i++) {
			for(int j = 0; j < min; j++) {
				nova_list.add(classes.get(i).get(j));
			}
		}
		
		return nova_list;

	}
}
