package preprocessamento.calculo_risco_fogo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class RF {
	
	private static double vegetacao(Vegetacao vegeta) {
		if(vegeta == Vegetacao.OMBROFILA_DENSA_ALAGADOS) {
			return 1.5;
		}
		if(vegeta == Vegetacao.FLORESTAS_DECIDUAS_E_SAZONAIS) {
			return 1.72;
		}
		if(vegeta == Vegetacao.FLORESTAS_CONTATO_CAMPINARANA) {
			return 2.0;
		}
		if(vegeta == Vegetacao.SAVANA_ARBOREA_CAATINGA_FECHADA) {
			return 2.4;
		}
		if(vegeta == Vegetacao.SAVANA_CAATINGA_ABERTA) {
			return 3.0;
		}
		if(vegeta == Vegetacao.AGRICULTURA_E_DIVERSOS) {
			return 4.0;
		}
		if(vegeta == Vegetacao.PASTAGENS_GRAMINEAS) {
			return 6.0;
		}
		return -1;
	}
	
	private static ArrayList<ArrayList<String>> csv_to_ArrayList (String arquivo, int saltar_linhas, int limite_coluna){
		
		String linha = "";
		
		ArrayList<ArrayList<String>> tabela = new ArrayList<ArrayList<String>>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {

			for(int i = 0; i < saltar_linhas; i++) {
				br.readLine();
			}
			
	        while ((linha = br.readLine()) != null) {
	        	
	        	ArrayList<String> lst_linha = new ArrayList<String>();
	        	
	            // use comma as separator
		        String[] coluna = linha.split(",");
		        
		        for(int i = 0; i < limite_coluna; i++) {
		        	lst_linha.add(coluna[i]);
		        }
		        tabela.add(lst_linha);
	        }
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return tabela;
	}
	
	private static double get_soma_precipitacao(int qtd_dias, int i_atual, ArrayList<ArrayList<String>> tabela) {
		double soma = 0;
		for(int dia = 1; dia <= qtd_dias; dia++) {
			soma += Double.parseDouble(tabela.get(i_atual-dia).get(1));
		}
		
		return soma;
	}
	
	private static double get_pse(int i, ArrayList<ArrayList<String>> tabela) {
		double prec1 = get_soma_precipitacao(1, i, tabela);		
		double prec2 = get_soma_precipitacao(2, i, tabela);	
		double prec3 = get_soma_precipitacao(3, i, tabela);
		double prec4 = get_soma_precipitacao(4, i, tabela);
		double prec5 = get_soma_precipitacao(5, i, tabela);
		double prec10 = get_soma_precipitacao(10, i, tabela);
		double prec15 = get_soma_precipitacao(15, i, tabela);
		double prec30 = get_soma_precipitacao(30, i, tabela);
		double prec60 = get_soma_precipitacao(60, i, tabela);
		double prec90 = get_soma_precipitacao(90, i, tabela);
		double prec120 = get_soma_precipitacao(120, i, tabela);
		
		double fp1 = Math.exp(-0.14*prec1);
		double fp2 = Math.exp(-0.07*(prec2-prec1));
		double fp3 = Math.exp(-0.04*(prec3-prec2));
		double fp4 = Math.exp(-0.03*(prec4-prec3));
		double fp5 = Math.exp(-0.02*(prec5-prec4));
		double fp6a10 = Math.exp(-0.01*(prec10-prec5));
		double fp11a15 = Math.exp(-0.008*(prec15-prec10));
		double fp16a30 = Math.exp(-0.004*(prec30-prec15));
		double fp31a60 = Math.exp(-0.002*(prec60-prec30));
		double fp61a90 = Math.exp(-0.001*(prec90-prec60));
		double fp91a120 = Math.exp(-0.0007*(prec120-prec90));
		
		return 105*fp1*fp2*fp3*fp4*fp5*fp6a10*fp11a15*fp16a30*fp31a60*fp61a90*fp91a120;
	}
	
	private static double calcular_risco_observado(int i, ArrayList<ArrayList<String>> tabela, Vegetacao vegeta) throws ArrayIndexOutOfBoundsException {
		ArrayList<String> dia_corrente = tabela.get(i);
		
		double pse = get_pse(i, tabela);
		
		double rf_basico;
		
		if(pse > 0.9) {
			rf_basico = 0.9;
		} else {
			rf_basico = (0.9*(1+Math.sin(((vegetacao(vegeta)*pse)-90)*(3.14/180))))/2;
		}
		
		double fu = (Double.parseDouble(dia_corrente.get(3))*-0.006)+1.3;
		
		double ft = (Double.parseDouble(dia_corrente.get(2))*0.02)+0.4;
		
		double rf_observado = rf_basico * ft * fu;
		
		if(rf_observado < 0.01) {
			return 0;
		}
		
		return rf_observado;
	}
	
	private static String classificar_rf(double rf_observado) {
		if(rf_observado <= 0.15) {
			return "MINIMO";
		} else if (rf_observado > 0.15 && rf_observado <= 0.40) {
			return "BAIXO";
		} else if (rf_observado > 0.40 && rf_observado <= 0.70) {
			return "MEDIO";
		} else if (rf_observado > 0.70 && rf_observado <= 0.95) {
			return "ALTO";
		} else {
			return "CRITICO";
		}
	}
	
	private static String classificar_rf_indexado(double rf_observado) {
		if(rf_observado <= 0.15) {
			return "0";
		} else if (rf_observado > 0.15 && rf_observado <= 0.40) {
			return "1";
		} else if (rf_observado > 0.40 && rf_observado <= 0.70) {
			return "2";
		} else if (rf_observado > 0.70 && rf_observado <= 0.95) {
			return "3";
		} else {
			return "4";
		}
	}
	
	private static void salvarCSV(String arquivo, ArrayList<ArrayList<String>> tabela) {
		
		try {
		
			File file = new File(arquivo);
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        //bw.write("\"data\",\"precipitacao\",\"temperatura\",\"umidade\",\"houve_incendio\",\"indice_risco\",\"classe_risco\"");
	        bw.write("\"precipitacao\",\"temperatura\",\"umidade\",\"dias_de_secura\",\"classe_risco\"");
	        bw.newLine();
	        for(int i=0;i<tabela.size();i++){
	        	for(int j=0; j<tabela.get(i).size(); j++) {
	        		bw.write(tabela.get(i).get(j));
	        		if(j != tabela.get(i).size()-1) {
	        			bw.write(",");
	        		}
	        	}
	            bw.newLine();
	        }
	        bw.close();
	        fw.close();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main (String[] args) {
		
		String pasta = System.getProperty("user.dir") + "\\src\\resources\\";
		String arquivo = "treinamento_tb_amostras_final_201908251648.csv";
		
		ArrayList<ArrayList<String>> tabela = csv_to_ArrayList(pasta+arquivo, 1, 5);
		
		ArrayList<ArrayList<String>> lista_remover = new ArrayList<ArrayList<String>>();
		
		for(int i = 0; i < tabela.size(); i++) {

			try {
				double risco_observado = calcular_risco_observado(i, tabela, Vegetacao.SAVANA_CAATINGA_ABERTA);
				
				//tabela.get(i).add(Double.toString(risco_observado));
				tabela.get(i).add(Double.toString(get_pse(i, tabela)));
				tabela.get(i).add(classificar_rf_indexado(risco_observado));
				
			} catch (Exception e) {
				lista_remover.add(tabela.get(i));
			}

		}
		
		for(int i = 0; i < tabela.size(); i++) {
			tabela.get(i).remove(0); // remove data
			tabela.get(i).remove(3); // remove boolean de existencia de incendio
		}
		
		// irá remover do CSV final os registros em que não foi possível classificar o risco
		for(ArrayList<String> registro : lista_remover) {
			tabela.remove(registro);
		}
		
		normalizar(tabela);
		
		salvarCSV(pasta+"normalizado_"+arquivo, tabela);
		
	}

	private static void normalizar(ArrayList<ArrayList<String>> tabela) {
		
		double[] menor = new double[tabela.size()-1];
		double[] maior = new double[tabela.size()-1];
		
		// primeira passagem (descobrirá os limites da tabela)
		for(int i = 0; i < tabela.size(); i++) {
			if(i == 0) {
				for(int j = 0; j < tabela.get(i).size()-1; j++) {
					menor[j] = Double.parseDouble(tabela.get(i).get(j));	
					maior[j] = Double.parseDouble(tabela.get(i).get(j));	
				}

			}
			
			for(int j = 0; j < tabela.get(i).size()-1; j++) {
				if(Double.parseDouble(tabela.get(i).get(j)) < menor[j]) {
					menor[j] = Double.parseDouble(tabela.get(i).get(j));
				}
				if(Double.parseDouble(tabela.get(i).get(j)) > maior[j]) {
					maior[j] = Double.parseDouble(tabela.get(i).get(j));
				}
			}
		}

		// segunda passagem (normalização)
		for(ArrayList<String> item : tabela) {
			
			// normalizar precipitação: 0mm a 100mm
			double prec = (Double.parseDouble(item.get(0))-menor[0])/(maior[0]-menor[0]);
			
			item.set(0, Double.toString(prec));
			
			// normalizar temperatura: 0°C a 50°C
			double temp = (Double.parseDouble(item.get(1))-menor[1])/(maior[1]-menor[1]);
			
			item.set(1, Double.toString(temp));
			
			// normalizar umidade: 0% a 100%
			double umid = (Double.parseDouble(item.get(2))-menor[2])/(maior[2]-menor[2]);
			
			item.set(2, Double.toString(umid));
			
			// normalizar umidade: 0% a 100%
			double pse = (Double.parseDouble(item.get(3))-menor[3])/(maior[3]-menor[3]);
			
			item.set(3, Double.toString(pse));
		}
		
	}
}
