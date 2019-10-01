package estatisticas.analise_grafico;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class AnalisarGrafico {
	
	public static void get_menor_valor(String nome_rede, Stats stats, Dataset dataset) throws IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<Double[]>> dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
        ois.close();
		
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
	
	public static void get_maior_valor(String nome_rede, Stats stats, Dataset dataset) throws IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<Double[]>> dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
        ois.close();
		
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
		System.out.println("Época: " + epoca);
		
	}
	
	public static void get_info_epoca(String nome_rede, int epoca) throws IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<Double[]>> dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
        ois.close();
		
		System.out.println("ÉPOCA " + epoca);
		
		System.out.println("== Dataset de TREINAMENTO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(0).get(epoca)[i]);	
		}
		
		System.out.println("== Dataset de VALIDAÇÃO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(1).get(epoca)[i]);	
		}
		
	}
	
	public static double get_stat_epoca(String nome_rede, int epoca, Dataset dataset,Stats stats) throws IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<Double[]>> dados = (ArrayList<ArrayList<Double[]>>) ois.readObject();
        ois.close();
		
		epoca = epoca - 1;
		
		return dados.get(dataset.ordinal()).get(epoca)[stats.ordinal()];
		
	}

}
