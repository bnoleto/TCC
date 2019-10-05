package main;

import codigos.Treinamento;
import estatisticas.NoletoGrafico;
import main.analisador.Analisador;
import utils.ModoExecucao;

public class Saida5Classificada {
	
	// iniciar do zero / continuar de onde parou o treinamento / apenas gerar os gráficos do arquivo .stats existentes
	public static ModoExecucao modo = ModoExecucao.COMECAR;
	
    public static void main(String[] args) throws Exception {
   
	    if(modo == ModoExecucao.COMECAR) {
	    	
	    	Treinamento.comecar_treinamento(50000);		// treinará por 5000 épocas
	
	    } else if (modo == ModoExecucao.CONTINUAR) {
	    	
	    	Treinamento.continuar_treinamento("20191004_0135", 4000); // continuará o treinamento do arquivo por 500 épocas
	    	
	    } else if(modo == ModoExecucao.GERAR_GRAFICOS) {
	    	
	    	NoletoGrafico.gerarGraficos("20191001_2342");
	    	
	    } else if(modo == ModoExecucao.ANALISAR_GRAFICO) {
	    	Analisador.main(args);
	    }
	    

        System.out.println("****************FINALIZADO********************");
    }
}
