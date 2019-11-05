package codigos;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import preprocessamento.PreProcessadorCSV;
import preprocessamento.calculo_risco_fogo.RF;
import preprocessamento.calculo_risco_fogo.Risco;

public class Classificador {
	
	private MultiLayerNetwork model;
	private ArrayList<ArrayList<String>> variaveis_normalizacao;
	
	public static void main(String[] args) throws IOException, ParseException {
		
		String pasta = System.getProperty("user.dir") + "/src/resources/datasets/";
		String arquivo = "com_queimadas/filtrado_amostras_inpe.csv";
		
		ArrayList<ArrayList<String>> tabela = PreProcessadorCSV.csv_to_ArrayList(pasta+arquivo, 1);
		
		for(ArrayList<String> amostra : tabela) {
			amostra.remove(5);

			double prec = Double.parseDouble(amostra.get(1));
			double temp = Double.parseDouble(amostra.get(2));
			double umid = Double.parseDouble(amostra.get(3));
			double pse = Double.parseDouble(amostra.get(4));
			
			amostra.add(String.valueOf(Classificador.classificarRF("20191005_2034", prec, temp, umid, pse).ordinal()));
		}
		
		String arquivo_queimadas = "bruto/tb_amostras_final_201908251648.csv";
		
		ArrayList<ArrayList<String>> amostras_queimadas = PreProcessadorCSV.csv_to_ArrayList(pasta+arquivo_queimadas, 1);
		
		tabela = PreProcessadorCSV.mesclar_coluna_queimadas(tabela, amostras_queimadas);
		
		/*
		for(ArrayList<String> amostra : tabela) {
			
			System.out.print("[");
			for(int i = 0 ; i < amostra.size(); i++) {
				System.out.print("\"" + amostra.get(i) + "\",");	
			}
			System.out.println("]");
			
		}*/
		
		int qtd_fogo_existente[] = {0,0,0,0,0};
		int qtd_fogo_nao_existente[] = {0,0,0,0,0};
		
		for(ArrayList<String> amostra : tabela) {
			
			int classe_risco = Integer.parseInt(amostra.get(5));
			boolean houve_fogo = Boolean.parseBoolean(amostra.get(6));
			
			if(houve_fogo) {
				qtd_fogo_existente[classe_risco]++;
			} else {
				qtd_fogo_nao_existente[classe_risco]++;
			}
		}
		
		System.out.println("Classe : Com Fogo : Sem Fogo : Total");
		for(int i = 0; i<5; i++) {
			
			
			System.out.printf("%d : %d : %d : %d\n", i, qtd_fogo_existente[i], (qtd_fogo_nao_existente[i]), qtd_fogo_existente[i] + qtd_fogo_nao_existente[i]);
			
		}
		

		
		
		
		/*
		String colunas = "\"Data\",\"Precipitacao\",\"TempMaxima\",\"Umidade Relativa Media\",\"Dias de Secura\",\"Risco de Fogo Obtido pela Rede\"";
		
		RF.salvarCSV(colunas, pasta+"com_queimadas/filtrado_amostras_bnoleto.csv", tabela);*/
		
	}
	
	public Classificador(String nome_rede) throws IOException{
		
		model = MultiLayerNetwork.load(new File(System.getProperty("user.dir")+"/redes/"+nome_rede+"/","rede.nn"), false);
		
		variaveis_normalizacao = RF.csv_to_ArrayList(System.getProperty("user.dir")+"/redes/"+nome_rede+"/variaveis_normalizacao", 0);
		
	}
	
	private static double normalizar(double menor_valor, double maior_valor, double valor_atual) {
		
		return ((valor_atual)-menor_valor)/(maior_valor-menor_valor);
		
	}
	
	private static int get_maior_i(MultiLayerNetwork model, ArrayList<ArrayList<String>> variaveis_normalizacao, double prec, double temp, double umid, double pse) {
		
		double[] menor = {
				Double.parseDouble(variaveis_normalizacao.get(0).get(0)),
				Double.parseDouble(variaveis_normalizacao.get(0).get(1)),
				Double.parseDouble(variaveis_normalizacao.get(0).get(2)),
				Double.parseDouble(variaveis_normalizacao.get(0).get(3)),
		};
		
		double[] maior = {
				Double.parseDouble(variaveis_normalizacao.get(1).get(0)),
				Double.parseDouble(variaveis_normalizacao.get(1).get(1)),
				Double.parseDouble(variaveis_normalizacao.get(1).get(2)),
				Double.parseDouble(variaveis_normalizacao.get(1).get(3)),
		};
		
		System.out.println("== (Dados inseridos) ==");
		System.out.println("Temperatura: " + temp + "°C");
		System.out.println("Precipitação: " + prec + "mm");
		System.out.println("Umidade: " + umid + "%");
		System.out.println("Dias de Secura: " + pse);
		
		
		double precipitacao = normalizar(menor[0],maior[0],prec);
		double temperatura = normalizar(menor[1],maior[1],temp);
		double umidade = normalizar(menor[2],maior[2],umid);
		double dias_de_secura = normalizar(menor[3],maior[3],pse);
		
		INDArray entradas = Nd4j.create(new double[]{precipitacao,temperatura,umidade,dias_de_secura}, new int[]{1, 4});
		
		INDArray saida = model.output(entradas);
		
		double maior_num = saida.getDouble(0);
		int maior_i = 0;
		
		for(int i = 0 ; i< 5; i++) {
			if(saida.getDouble(i) > maior_num) {
				maior_num = saida.getDouble(i);
				maior_i = i;
			}
		}
		
		return maior_i;
	}
	
	public Risco classificarRF(double prec, double temp, double umid, double pse) {
		
		return Risco.values()[get_maior_i(this.model, this.variaveis_normalizacao, prec, temp, umid, pse)];
	}
	
	public static Risco classificarRF(String nome_rede, double prec, double temp, double umid, double pse) throws IOException {
		
		MultiLayerNetwork model = MultiLayerNetwork.load(new File(System.getProperty("user.dir")+"/redes/"+nome_rede+"/","rede.nn"), false);
		
		ArrayList<ArrayList<String>> variaveis_normalizacao = RF.csv_to_ArrayList(System.getProperty("user.dir")+"/redes/"+nome_rede+"/variaveis_normalizacao", 0);
		
		return Risco.values()[get_maior_i(model, variaveis_normalizacao, prec, temp, umid, pse)];
		
	}

}
