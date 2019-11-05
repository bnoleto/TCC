package preprocessamento.calculo_risco_fogo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import preprocessamento.PreProcessadorCSV;

public class RF {
	
	public static ArrayList<ArrayList<String>> calcularRF_analise(ArrayList<ArrayList<String>> dados_met, ArrayList<ArrayList<String>> dados_queimadas) throws ParseException{
		
		ArrayList<ArrayList<String>> tabela = dados_met;
		/*
		Calendar data_inicial = Calendar.getInstance();
		Calendar data_final = Calendar.getInstance();
		
		data_inicial.set(1900, Calendar.JANUARY, 1);
		data_final.set(2018, Calendar.DECEMBER, 31);
		*/

		ArrayList<ArrayList<String>> lista_remover = new ArrayList<ArrayList<String>>();
		
		
		for(int i = 0; i < tabela.size(); i++) {

			try {
				
				double pse = get_pse(i, tabela);
				
				double risco_observado = calcular_risco_observado(pse, tabela, i,Vegetacao.SAVANA_CAATINGA_ABERTA);
				
				System.out.println(((double)i/tabela.size()*100) + "% (" + (i+1) +"/"+tabela.size()+")");
				
				
				tabela.get(i).add(Double.toString(pse));
				tabela.get(i).add(Double.toString(risco_observado));
				tabela.get(i).add(classificar_rf_indexado(risco_observado));
				
				
			} catch (Exception e) {
				lista_remover.add(tabela.get(i));
			}

		}
		
		
		
		
		// irï¿½ remover do CSV final os registros em que nï¿½o foi possï¿½vel classificar o risco
		for(ArrayList<String> registro : lista_remover) {
			tabela.remove(registro);
		}
		/*
		Calendar data_inicial = Calendar.getInstance();
		Calendar data_final = Calendar.getInstance();
		
		data_inicial.set(1900, 1, 1);
		data_final.set(2019, 12, 31);
		*/
		
		//tabela = PreProcessadorCSV.filtrar_por_data_string(data_inicial, data_final, tabela);
		
		ArrayList<ArrayList<String>> remover_tabela = new ArrayList<ArrayList<String>>();
		
		for(ArrayList<String> amostra : tabela) {
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone("America/Brasilia"));
			Date data_hora = formatter.parse(amostra.get(0));
			
			Calendar data = Calendar.getInstance();
			data.setTimeInMillis(data_hora.getTime());
			
			boolean excluir = true;
			
			for(ArrayList<String> amostra_queim : dados_queimadas) {
				
				SimpleDateFormat formatter_q = new SimpleDateFormat("yyyy-MM-dd");
				formatter_q.setTimeZone(TimeZone.getTimeZone("America/Brasilia"));
				Date data_hora_q = formatter.parse(amostra_queim.get(0));
				
				Calendar data_q = Calendar.getInstance();
				data_q.setTimeInMillis(data_hora_q.getTime());
				/*
				System.out.print(data_q.get(Calendar.YEAR)+"-"+(data_q.get(Calendar.MONTH)+1)+"-"+data_q.get(Calendar.DAY_OF_MONTH));
				System.out.print(" / ");
				System.out.println(data.get(Calendar.YEAR)+"-"+(data.get(Calendar.MONTH)+1)+"-"+data.get(Calendar.DAY_OF_MONTH));
				*/
				if(data_q.compareTo(data) == 0) {
					excluir = false;
				}
			}
			
			if (excluir) {
				remover_tabela.add(amostra);
			}
			
			
		}
		
		tabela.removeAll(remover_tabela);
		
		for(int i = 0; i < tabela.size(); i++) {
			
			tabela.get(i).remove(5); // remove risco (double)
			//tabela.get(i).remove(0); // remove data
			
		}
		
		System.out.println(tabela.size());
		
		return tabela;
		
	}
	
	public static void main (String[] args) throws ParseException {
		
		String pasta = System.getProperty("user.dir") + "/src/resources/datasets/";
		String arquivo = "filtrado_amostras_imperatriz_inmet.csv";
		
		ArrayList<ArrayList<String>> tabela = csv_to_ArrayList(pasta+arquivo, 1);
		
		Calendar data_inicial = Calendar.getInstance();
		Calendar data_final = Calendar.getInstance();
		
		data_inicial.set(1900, Calendar.JANUARY, 1);
		data_final.set(2018, Calendar.DECEMBER, 31);
		

		ArrayList<ArrayList<String>> lista_remover = new ArrayList<ArrayList<String>>();
		
		
		for(int i = 0; i < tabela.size(); i++) {

			try {
				
				double pse = get_pse(i, tabela);
				
				double risco_observado = calcular_risco_observado(pse, tabela, i,Vegetacao.SAVANA_CAATINGA_ABERTA);
				
				System.out.println(((double)i/tabela.size()*100) + "% (" + (i+1) +"/"+tabela.size()+")");
				
				
				tabela.get(i).add(Double.toString(pse));
				tabela.get(i).add(Double.toString(risco_observado));
				tabela.get(i).add(classificar_rf_indexado(risco_observado));
				
				
			} catch (Exception e) {
				lista_remover.add(tabela.get(i));
			}

		}
		
		
		
		
		// irï¿½ remover do CSV final os registros em que nï¿½o foi possï¿½vel classificar o risco
		for(ArrayList<String> registro : lista_remover) {
			tabela.remove(registro);
		}
		/*
		Calendar data_inicial = Calendar.getInstance();
		Calendar data_final = Calendar.getInstance();
		
		data_inicial.set(1900, 1, 1);
		data_final.set(2019, 12, 31);
		*/
		
		tabela = PreProcessadorCSV.filtrar_por_data_string(data_inicial, data_final, tabela);
		
		System.out.println(tabela.size());
		
		
		
		salvarCSV("\"data\",\"precipitacao\",\"temperatura\",\"umidade\",\"dias_de_secura\",\"indice_risco\",\"classe_risco\"",
				pasta+"/nao_normalizado/"+arquivo, tabela);
		
		
		for(int i = 0; i < tabela.size(); i++) {
			
			tabela.get(i).remove(5); // remove risco (double)
			tabela.get(i).remove(0); // remove data
			
		}
		
		normalizar(tabela);
		
		show_classes(tabela);
		
		salvarCSV("\"precipitacao\",\"temperatura\",\"umidade\",\"dias_de_secura\",\"classe_risco\"",
				pasta+"/normalizado/"+arquivo, tabela);
		
	}
	
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
	
	public static ArrayList<ArrayList<String>> csv_to_ArrayList (String arquivo, int saltar_linhas){
		
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
		        
		        for(int i = 0; i < coluna.length; i++) {
		        	lst_linha.add(coluna[i]);
		        }
		        tabela.add(lst_linha);
	        }
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return tabela;
	}
	
	private static double get_soma_precipitacao(int qtd_dias, int i_atual, ArrayList<ArrayList<String>> tabela) throws ParseException {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date data = formatter.parse(tabela.get(i_atual).get(0));
		
		
		Calendar data_final = Calendar.getInstance();
		data_final.setTimeInMillis(data.getTime());
		data_final.add(Calendar.DAY_OF_MONTH, -1);
		
		Calendar data_inicial = Calendar.getInstance();
		data_inicial.setTime(data_final.getTime());
		data_inicial.add(Calendar.DAY_OF_MONTH, -qtd_dias);
		
		ArrayList<ArrayList<String>> amostras_validas = PreProcessadorCSV.filtrar_por_data_string(data_inicial, data_final, tabela);
		
		
		
		double soma = 0;
		for(int i = 0; i < amostras_validas.size(); i++) {
			soma += Double.parseDouble(amostras_validas.get(i).get(1));
		}
		
		return soma;
	}
	
	private static double get_pse(int i, ArrayList<ArrayList<String>> tabela) throws ParseException {
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
	
	public static double get_pse(double[] precipitacoes) throws ParseException {
		double prec1 = precipitacoes[0];
		double prec2 = precipitacoes[1];	
		double prec3 = precipitacoes[2];
		double prec4 = precipitacoes[3];
		double prec5 = precipitacoes[4];
		double prec10 = precipitacoes[5];
		double prec15 = precipitacoes[6];
		double prec30 = precipitacoes[7];
		double prec60 = precipitacoes[8];
		double prec90 = precipitacoes[9];
		double prec120 = precipitacoes[10];
		
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
	
	private static double calcular_risco_observado(double pse, ArrayList<ArrayList<String>> tabela, int i,Vegetacao vegeta) throws ArrayIndexOutOfBoundsException, ParseException {
		ArrayList<String> dia_corrente = tabela.get(i);
		
		double rf_basico;
		
		rf_basico = (0.9*(1+Math.sin(((vegetacao(vegeta)*pse)-90)*(3.14/180))))/2;
		
		if(rf_basico > 0.9) {
			rf_basico = 0.9;
		}
		
		double fu = (Double.parseDouble(dia_corrente.get(3))*-0.006)+1.3;
		
		double ft = (Double.parseDouble(dia_corrente.get(2))*0.02)+0.4;
		
		double rf_observado = rf_basico * ft * fu;
		
		if(rf_observado < 0.01) {
			return 0;
		}
		
		
		
		return rf_observado;
	}
	
	public static double calcular_risco_observado(double prec, double temp, double umid, double pse, Vegetacao vegeta) throws ArrayIndexOutOfBoundsException, ParseException {
		
		double rf_basico;
		
		rf_basico = (0.9*(1+Math.sin(((vegetacao(vegeta)*pse)-90)*(3.14/180))))/2;
		
		if(rf_basico > 0.9) {
			rf_basico = 0.9;
		}
		
		double fu = (umid*-0.006)+1.3;
		
		double ft = (temp*0.02)+0.4;
		
		double rf_observado = rf_basico * ft * fu;
		
		if(rf_observado < 0.01) {
			return 0;
		}
		
		
		
		return rf_observado;
	}
	
	public static String classificar_rf(double rf_observado) {
		if(rf_observado <= 0.15) {
			return "MÍNIMO";
		} else if (rf_observado > 0.15 && rf_observado <= 0.40) {
			return "BAIXO";
		} else if (rf_observado > 0.40 && rf_observado <= 0.70) {
			return "MÉDIO";
		} else if (rf_observado > 0.70 && rf_observado <= 0.95) {
			return "ALTO";
		} else {
			return "CRÍTICO";
		}
	}
	
	public static Risco classificar_rf_enum(double rf_observado) {
		if(rf_observado <= 0.15) {
			return Risco.MINIMO;
		} else if (rf_observado > 0.15 && rf_observado <= 0.40) {
			return Risco.BAIXO;
		} else if (rf_observado > 0.40 && rf_observado <= 0.70) {
			return Risco.MEDIO;
		} else if (rf_observado > 0.70 && rf_observado <= 0.95) {
			return Risco.ALTO;
		} else {
			return Risco.CRITICO;
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
	
	public static void salvarCSV(String colunas, String arquivo, ArrayList<ArrayList<String>> tabela) {
		
		try {
		
			File file = new File(arquivo);
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        if(colunas != null) {
	        	bw.write(colunas);	
	        }
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
	
	public static File arrayList_to_CSV(List<ArrayList<String>> tabela) {
		
		try {
		
			File temp = File.createTempFile("tempdata", ".tmp");
			
	        BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
	        
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
	        
	        return temp;
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	public static void show_classes(List<ArrayList<String>> tabela) {
		int classe0 = 0;
		int classe1 = 0;
		int classe2 = 0;
		int classe3 = 0;
		int classe4 = 0;
		
		
		for(ArrayList<String> amostra : tabela) {
			
			
			if(amostra.get(4).equals("0")) {
				classe0++;
			} else if(amostra.get(4).equals("1")) {
				classe1++;
			} else if(amostra.get(4).equals("2")) {
				classe2++;
			} else if(amostra.get(4).equals("3")) {
				classe3++;
			} else if(amostra.get(4).equals("4")) {
				classe4++;
			}
			
		}
		
		System.out.println("=== contagem de classes ===");
		System.out.println("0: " + classe0 + " amostras");
		System.out.println("1: " + classe1 + " amostras");
		System.out.println("2: " + classe2 + " amostras");
		System.out.println("3: " + classe3 + " amostras");
		System.out.println("4: " + classe4 + " amostras");
		
	}
	

	public static void show_classes(ArrayList<ArrayList<String>> tabela) {
		int classe0 = 0;
		int classe1 = 0;
		int classe2 = 0;
		int classe3 = 0;
		int classe4 = 0;
		
		
		for(ArrayList<String> amostra : tabela) {
			
			
			if(amostra.get(4).equals("0")) {
				classe0++;
			} else if(amostra.get(4).equals("1")) {
				classe1++;
			} else if(amostra.get(4).equals("2")) {
				classe2++;
			} else if(amostra.get(4).equals("3")) {
				classe3++;
			} else if(amostra.get(4).equals("4")) {
				classe4++;
			}
			
		}
		
		System.out.println("=== contagem de classes ===");
		System.out.println("0: " + classe0 + " amostras");
		System.out.println("1: " + classe1 + " amostras");
		System.out.println("2: " + classe2 + " amostras");
		System.out.println("3: " + classe3 + " amostras");
		System.out.println("4: " + classe4 + " amostras");
		
	}
	
	public static void salvar_variaveis_normalizacao(ArrayList<ArrayList<String>> tabela, String arquivo) {
		
		ArrayList<ArrayList<String>> tabela_valores = new ArrayList<ArrayList<String>>(); 
		
		double[] menor = new double[tabela.get(0).size()];
		double[] maior = new double[tabela.get(0).size()];
		
		// primeira passagem (descobrirï¿½ os limites da tabela)
		for(int i = 0; i < tabela.size(); i++) {
			if(i == 0) {
				for(int j = 0; j < tabela.get(i).size(); j++) {
					menor[j] = Double.parseDouble(tabela.get(i).get(j));	
					maior[j] = Double.parseDouble(tabela.get(i).get(j));	
				}

			}
			
			for(int j = 0; j < tabela.get(i).size(); j++) {
				if(Double.parseDouble(tabela.get(i).get(j)) < menor[j]) {
					menor[j] = Double.parseDouble(tabela.get(i).get(j));
				}
				if(Double.parseDouble(tabela.get(i).get(j)) > maior[j]) {
					maior[j] = Double.parseDouble(tabela.get(i).get(j));
				}
			}
		}
		
		ArrayList<String> menores = new ArrayList<String>();
		ArrayList<String> maiores = new ArrayList<String>();
		
		for(int i = 0; i < 4; i++) {
			menores.add(String.valueOf(menor[i]));
			maiores.add(String.valueOf(maior[i]));
		}		
		
		tabela_valores.add(menores);
		tabela_valores.add(maiores);
		
		salvarCSV(null, arquivo, tabela_valores);
		
	}
	
	
	private static void normalizar(ArrayList<ArrayList<String>> tabela) {
		
		double[] menor = new double[tabela.size()-1];
		double[] maior = new double[tabela.size()-1];
		
		// primeira passagem (descobrirï¿½ os limites da tabela)
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

		// segunda passagem (normalizaï¿½ï¿½o)
		for(ArrayList<String> item : tabela) {
			
			// normalizar precipitaï¿½ï¿½o
			double prec = (Double.parseDouble(item.get(0))-menor[0])/(maior[0]-menor[0]);
			
			item.set(0, Double.toString(prec));
			
			// normalizar temperatura
			double temp = (Double.parseDouble(item.get(1))-menor[1])/(maior[1]-menor[1]);
			
			item.set(1, Double.toString(temp));
			
			// normalizar umidade
			double umid = (Double.parseDouble(item.get(2))-menor[2])/(maior[2]-menor[2]);
			
			item.set(2, Double.toString(umid));
			
			// normalizar dias de secura
			double pse = (Double.parseDouble(item.get(3))-menor[3])/(maior[3]-menor[3]);
			
			item.set(3, Double.toString(pse));
		}
		
	}
}
