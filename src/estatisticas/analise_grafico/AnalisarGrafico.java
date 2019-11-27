package estatisticas.analise_grafico;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

class AbrirArquivo implements Runnable{
	
	private ObjectInputStream ois;
	private int bytes_restantes;
	private ArrayList<ArrayList<ArrayList<Double[]>>> dados;

	public AbrirArquivo(ObjectInputStream ois) {
		this.ois = ois;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		
		try {

			dados = (ArrayList<ArrayList<ArrayList<Double[]>>>) ois.readObject();
			

		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public ArrayList<ArrayList<ArrayList<Double[]>>> get_dados(){
		return this.dados;
	}
	
	
	
	public int get_bytes_restantes(){
		return this.bytes_restantes;
		
	}
	
}

public class AnalisarGrafico {
	
	private ArrayList<ArrayList<ArrayList<Double[]>>> dados;
	
	public static void show_barra_carregamento(Thread t1, AbrirArquivo abridor, int total, int intervalo, int tamanho_barra) {
		
		
		while(t1.isAlive()) {
			
			int bytes_carregados = total - abridor.get_bytes_restantes();
			
			double porcentagem = (double)bytes_carregados/total;
			
			int quadrados = (int) (porcentagem*tamanho_barra);
			double porcentagem100 = porcentagem*100;
			
			String linha = "";
			
			linha+="[";
			for(int i = 0; i<tamanho_barra; i++) {
				if(i<quadrados) {
					linha+="#";
					
				}else {
					linha+=" ";
				}
				
			}
			linha+="] ";
			
			linha+= String.format("%.2f", porcentagem100) + "%";
			System.out.println(linha);
			try {
				Thread.sleep(intervalo);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			}
		}
		
		System.out.println("Carregamento finalizado!");
	}
	
	public ArrayList<ArrayList<ArrayList<Double[]>>> get_dados (){
		return this.dados;
	}
	
	public AnalisarGrafico(String nome_rede) throws IOException, ClassNotFoundException {
		
		FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/redes/"+nome_rede+"/estatisticas.stats");
		
		int tamanho_arquivo = fis.available(); 
		
		BufferedInputStream bis = new BufferedInputStream(fis);
		
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(fis));
        
        AbrirArquivo abrir = new AbrirArquivo(ois);
        
        Thread t1 = new Thread(abrir);
        
        t1.start();
        
        while(t1.isAlive()) {
        	
        	//show_barra_carregamento(t1, abrir, tamanho_arquivo, 100, 50);
        	System.out.println("Carregando " + nome_rede + "....");
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.out.println("Carregamento finalizado!");
        
        
        dados = abrir.get_dados();
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
		System.out.println("�poca: " + (epoca+1));
		
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
		System.out.println("�poca: " + epoca);
		
	}
	
	public void get_info_epoca(int epoca) throws IOException, ClassNotFoundException {
		
		System.out.println("�POCA " + epoca);
		
		System.out.println("== Dataset de TREINAMENTO ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(0).get(0).get(epoca)[i]);	
		}
		
		System.out.println("== Dataset de VALIDA��O ==");
		
		for(int i = 0; i < Stats.values().length; i++) {
			System.out.println(Stats.values()[i].toString() + ": " + dados.get(1).get(0).get(epoca)[i]);	
		}
		
	}
	
	public double get_stat_epoca(int epoca, Dataset dataset,Stats stats) throws IOException, ClassNotFoundException {

		return dados.get(dataset.ordinal()).get(epoca).get(0)[stats.ordinal()];
		
	}

}
