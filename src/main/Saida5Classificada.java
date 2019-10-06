package main;

import codigos.Treinamento;
import estatisticas.NoletoGrafico;
import main.analisador.Analisador;
import utils.ModoExecucao;

public class Saida5Classificada {
	
	// iniciar do zero / continuar de onde parou o treinamento / apenas gerar os gr�ficos do arquivo .stats existentes
	public static ModoExecucao modo = ModoExecucao.GERAR_GRAFICOS;
	public static String nome_arquivo = "20191005_2034-20k";
	
    public static void main(String[] args) throws Exception {
   
	    if(modo == ModoExecucao.COMECAR) {
	    	
	    	Treinamento.comecar_treinamento(50000);		// treinar� por 5000 �pocas
	
	    } else if (modo == ModoExecucao.CONTINUAR) {
	    	
	    	Treinamento.continuar_treinamento(nome_arquivo,28000); // continuar� o treinamento do arquivo por 500 �pocas
	    	
	    } else if(modo == ModoExecucao.GERAR_GRAFICOS) {
	    	
	    	NoletoGrafico.gerarGraficos(nome_arquivo);
	    	
	    } else if(modo == ModoExecucao.ANALISAR_GRAFICO) {
	    	Analisador.main(args);
	    }
	    

        System.out.println("****************FINALIZADO********************");
    }
}
