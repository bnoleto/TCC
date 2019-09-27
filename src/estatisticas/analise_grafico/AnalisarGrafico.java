package estatisticas.analise_grafico;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class AnalisarGrafico {
	
	private static void get_menor_valor(ArrayList<ArrayList<Double[]>> dados, Stats stats, Dataset dataset) {
		
		double menor_valor = dados.get(dataset.ordinal()).get(0)[stats.ordinal()];
		int epoca = 0;
		
		for(int i = 1; i < dados.get(dataset.ordinal()).size(); i++) {
			
			if(dados.get(dataset.ordinal()).get(i)[stats.ordinal()] <= menor_valor) {
				menor_valor = dados.get(dataset.ordinal()).get(i)[stats.ordinal()];
				epoca = i;
			}
		}
		
		System.out.println("== Dataset " + dataset.toString() + " ==");
		System.out.println("Menor " + stats.toString() + ": " + menor_valor);
		System.out.println("Época: " + (epoca+1));
		
	}
	
	private static void get_maior_valor(ArrayList<ArrayList<Double[]>> dados, Stats stats, Dataset dataset) {
		
		double maior_valor = dados.get(dataset.ordinal()).get(0)[stats.ordinal()];
		int epoca = 0;
		
		for(int i = 1; i < dados.get(dataset.ordinal()).size(); i++) {
			
			if(dados.get(dataset.ordinal()).get(i)[stats.ordinal()] >= maior_valor) {
				maior_valor = dados.get(dataset.ordinal()).get(i)[stats.ordinal()];
				epoca = i;
			}
		}
		
		System.out.println("== Dataset " + dataset.toString() + " ==");
		System.out.println("Maior " + stats.toString() + ": " + maior_valor);
		System.out.println("Época: " + (epoca+1));
		
	}
	
	private static void get_info_epoca(ArrayList<ArrayList<Double[]>> dados, int epoca) {
		
		epoca = epoca - 1;
		
		System.out.println("ÉPOCA " + (epoca+1));
		
		System.out.println("== Dataset de TREINAMENTO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(0).get(epoca)[i]);	
		}
		
		System.out.println("== Dataset de VALIDAÇÃO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(1).get(epoca)[i]);	
		}
		
	}
	
	private static double get_stat_epoca(ArrayList<ArrayList<Double[]>> dados, int epoca, Dataset dataset,Stats stats) {
		
		epoca = epoca - 1;
		
		return dados.get(dataset.ordinal()).get(epoca)[stats.ordinal()];
		
	}

	public static void main(String[] args) throws Exception {

		File dir = null;
		String nome_rede = "26092019_2151";
        ArrayList<ArrayList<Double[]>> dados = null;

		dir = new File(System.getProperty("user.dir") + "\\redes\\" +nome_rede+"\\");
    	
    	FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
        ois.close();
        
        get_menor_valor(dados, Stats.LOSS, Dataset.VALIDACAO);
        
        get_info_epoca(dados, 50000);

	}

}
