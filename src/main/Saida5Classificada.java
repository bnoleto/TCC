package main;

import codigos.Treinamento;
import estatisticas.NoletoGrafico;
import estatisticas.analise_grafico.AnalisarGrafico;
import estatisticas.analise_grafico.Dataset;
import estatisticas.analise_grafico.Stats;
import utils.ModoExecucao;

public class Saida5Classificada {
	
	// iniciar do zero / continuar de onde parou o treinamento / apenas gerar os gr�ficos do arquivo .stats existentes
	public static ModoExecucao modo = ModoExecucao.ANALISAR_GRAFICO;
	
    public static void main(String[] args) throws Exception {
   
	    if(modo == ModoExecucao.COMECAR) {
	    	
	    	Treinamento.comecar_treinamento(10000);		// treinar� por 10000 �pocas
	
	    } else if (modo == ModoExecucao.CONTINUAR) {
	    	
	    	Treinamento.continuar_treinamento("01102019_0128", 500); // continuar� o treinamento do arquivo por 500 �pocas
	    	
	    } else if(modo == ModoExecucao.GERAR_GRAFICOS) {
	    	
	    	NoletoGrafico.gerarGraficos("20191001_0231");
	    	
	    } else if(modo == ModoExecucao.ANALISAR_GRAFICO) {
	    	AnalisarGrafico.get_menor_valor("20191001_0231", Stats.LOSS, Dataset.VALIDACAO);
	    }
	    

        System.out.println("****************FINALIZADO********************");
    }
}
