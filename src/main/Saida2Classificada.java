package main;

import codigos.Treinamento_bin;
import estatisticas.NoletoGrafico;
import estatisticas.analise_grafico.AnalisarGrafico;
import estatisticas.analise_grafico.Dataset;
import estatisticas.analise_grafico.Stats;
import utils.ModoExecucao;

public class Saida2Classificada {
	
	// iniciar do zero / continuar de onde parou o treinamento / apenas gerar os gr�ficos do arquivo .stats existentes
	public static ModoExecucao modo = ModoExecucao.COMECAR;
	
    public static void main(String[] args) throws Exception {
   
	    if(modo == ModoExecucao.COMECAR) {
	    	
	    	Treinamento_bin.comecar_treinamento(10000);		// treinar� por 10000 �pocas
	
	    } else if (modo == ModoExecucao.CONTINUAR) {
	    	
	    	Treinamento_bin.continuar_treinamento("01102019_0128", 500); // continuar� o treinamento do arquivo por 500 �pocas
	    	
	    } else if(modo == ModoExecucao.GERAR_GRAFICOS) {
	    	
	    	NoletoGrafico.gerarGraficos("20191001_0231");
	    	
	    } else if(modo == ModoExecucao.ANALISAR_GRAFICO) {
	    	new AnalisarGrafico("20191001_0231").get_menor_valor(Stats.LOSS, Dataset.VALIDACAO);
	    }
	    

        System.out.println("****************FINALIZADO********************");
    }
}
