package estatisticas.analise_grafico;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import main.analisador.CarregarModelo;

public class AnalisarGrafico {
	
	private ArrayList<ArrayList<ArrayList<Double[]>>> dados;
	
	public AnalisarGrafico(String nome_rede) throws IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "\\redes\\"+nome_rede+"\\estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        dados = (ArrayList<ArrayList<ArrayList<Double[]>>>) ois.readObject();
        ois.close();
	}
	
	public void get_menor_valor(Stats stats, Dataset dataset) throws IOException, ClassNotFoundException {

		double menor_valor = dados.get(dataset.ordinal()).get(0).get(0)[stats.ordinal()];
		int epoca = 0;
		
		for(int i = 1; i < dados.get(dataset.ordinal()).size(); i++) {
			
			if(dados.get(dataset.ordinal()).get(i).get(0)[stats.ordinal()] <= menor_valor) {
				menor_valor = dados.get(dataset.ordinal()).get(i).get(0)[stats.ordinal()];
				epoca = i;
			}
		}
		
		System.out.println("== Dataset " + dataset.toString() + " ==");
		System.out.println("Menor " + stats.toString() + ": " + menor_valor);
		System.out.println("Época: " + (epoca+1));
		
	}
	
	public int get_epoca_menor_loss_validacao() throws IOException, ClassNotFoundException {
		
		Dataset dataset = Dataset.VALIDACAO;
		Stats stats = Stats.LOSS;
		
		double menor_valor = dados.get(dataset.ordinal()).get(0).get(0)[stats.ordinal()];
		int epoca = 0;
		
		for(int i = 1; i < dados.get(dataset.ordinal()).size(); i++) {
			
			if(dados.get(dataset.ordinal()).get(i).get(0)[stats.ordinal()] <= menor_valor) {
				menor_valor = dados.get(dataset.ordinal()).get(i).get(0)[stats.ordinal()];
				epoca = i;
			}
		}
		
		return epoca;
	}
	
	public int get_qtd_epocas() {
		
		return dados.get(0).size();
		
	}
	
	public void get_maior_valor(Stats stats, Dataset dataset) throws IOException, ClassNotFoundException {
		
		double maior_valor = dados.get(dataset.ordinal()).get(0).get(0)[stats.ordinal()];
		int epoca = 0;
		
		for(int i = 1; i < dados.get(dataset.ordinal()).size(); i++) {
			
			if(dados.get(dataset.ordinal()).get(i).get(0)[stats.ordinal()] >= maior_valor) {
				maior_valor = dados.get(dataset.ordinal()).get(i).get(0)[stats.ordinal()];
				epoca = i;
			}
		}
		
		System.out.println("== Dataset " + dataset.toString() + " ==");
		System.out.println("Maior " + stats.toString() + ": " + maior_valor);
		System.out.println("Época: " + epoca);
		
	}
	
	public void get_info_epoca(int epoca) throws IOException, ClassNotFoundException {
		
		System.out.println("ÉPOCA " + epoca);
		
		System.out.println("== Dataset de TREINAMENTO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(0).get(0).get(epoca)[i]);	
		}
		
		System.out.println("== Dataset de VALIDAÇÃO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(1).get(0).get(epoca)[i]);	
		}
		
	}
	
	public double get_stat_epoca(int epoca, Dataset dataset,Stats stats) throws IOException, ClassNotFoundException {

		return dados.get(dataset.ordinal()).get(epoca).get(0)[stats.ordinal()];
		
	}

	public void set_rede(String nome_rede) throws IOException, ClassNotFoundException {
		
		dados = new CarregarModelo(nome_rede).get_modelo().dados;
		
	}

}
