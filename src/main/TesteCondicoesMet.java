package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import preprocessamento.calculo_risco_fogo.RF;
import preprocessamento.calculo_risco_fogo.Risco;

public class TesteCondicoesMet {
	
	private static MultiLayerNetwork model;
	private static String nome_rede;
	
	
	public TesteCondicoesMet(String nome_rede) throws IOException {
		
		this.nome_rede = nome_rede;
		model = MultiLayerNetwork.load(new File(System.getProperty("user.dir")+"/redes/"+nome_rede+"/","rede.nn"), false);
	}

	public static Risco classificar_risco(double prec, double temp, double umid, double pse) {

		
		ArrayList<ArrayList<String>> variaveis_normalizacao = RF.csv_to_ArrayList(System.getProperty("user.dir")+"/redes/"+nome_rede+"/variaveis_normalizacao", 0);
		
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
		
		
		double precipitacao = normalizar(menor[0],maior[0],prec);
		double temperatura = normalizar(menor[1],maior[1],temp);
		double umidade = normalizar(menor[2],maior[2],umid);
		double dias_de_secura = normalizar(menor[3],maior[3],pse);
		
		INDArray entradas = Nd4j.create(new double[]{precipitacao,temperatura,umidade,dias_de_secura}, new int[]{1, 4});
		
		System.out.println(entradas);
		
		INDArray saida = model.output(entradas);
		
		double maior_num = saida.getDouble(0);
		int maior_i = 0;
		
		for(int i = 0 ; i< 5; i++) {
			if(saida.getDouble(i) > maior_num) {
				maior_num = saida.getDouble(i);
				maior_i = i;
			}
		}
		
		return Risco.values()[maior_i];
		
	}
	
	private static double normalizar(double menor_valor, double maior_valor, double valor_atual) {
		
		return ((valor_atual)-menor_valor)/(maior_valor-menor_valor);
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		TesteCondicoesMet teste = new TesteCondicoesMet("20191005_2034");
		
		System.out.println("O risco de queimada é " + teste.classificar_risco(0.0,30.2,76.75,82.70585107714942));
		
	}
	
	

}
