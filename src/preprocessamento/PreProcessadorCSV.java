package preprocessamento;

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
import java.util.TimeZone;

public class PreProcessadorCSV {
	
	private static Calendar cal_inicial = Calendar.getInstance();
	private static Calendar cal_final = Calendar.getInstance();
	
	private static Object parseDouble (String valor){
		
		Object nulo = null;
		
		try {
			return new Double(Double.parseDouble(valor));
		} catch (NumberFormatException e) {
			return nulo;
		}
	}
	
	public static void main(String[] args) throws ParseException {
		
		cal_inicial.set(1900, 1, 1);
		cal_final.set(2020, 12, 31);
		
		String pasta = System.getProperty("user.dir") + "\\src\\resources\\datasets\\";
		String arquivo = "amostras_imperatriz_inmet.csv";
		
		ArrayList<ArrayList<String>> amostras = csv_to_ArrayList(pasta+arquivo, 1);
		
		System.out.println(amostras.get(amostras.size()-1).toString());
		
		ArrayList<ArrayList<Object>> amostras_conv = new ArrayList<ArrayList<Object>>();
		
		for(ArrayList<String> amostra : amostras) {
			
			ArrayList<Object> amostra_conv = new ArrayList<Object>();
			
			int estacao = Integer.parseInt(amostra.get(0));
			
			amostra_conv.add(estacao);
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HHmm");
			formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date data_hora = formatter.parse(amostra.get(1)+" "+amostra.get(2));
			
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(data_hora.getTime());
			
			amostra_conv.add(cal);
			
			/*
			Double precipitacao = new Double(-999), temp_maxima = new Double(-999), temp_minima = new Double(-999),
					insolacao = new Double(-999), evaporacao_piche = new Double(-999), temp_comp_media = new Double(-999),
					umidade = new Double(-999), veloc_vento_media = new Double(-999);*/
			
			Double precipitacao = (Double) parseDouble(amostra.get(3));
			amostra_conv.add(precipitacao);
			
			Double temp_maxima = (Double) parseDouble(amostra.get(4));
			amostra_conv.add(temp_maxima);
			
			Double temp_minima = (Double) parseDouble(amostra.get(5));
			amostra_conv.add(temp_minima);
			
			Double insolacao = (Double) parseDouble(amostra.get(6));
			amostra_conv.add(insolacao);
			
			Double evaporacao = (Double) parseDouble(amostra.get(7));
			amostra_conv.add(evaporacao);
			
			Double temp_comp_media = (Double) parseDouble(amostra.get(8));
			amostra_conv.add(temp_comp_media);
			
			Double umidade = (Double) parseDouble(amostra.get(9));
			amostra_conv.add(umidade);
			
			Double veloc_vento_media = (Double) parseDouble(amostra.get(10));
			amostra_conv.add(veloc_vento_media);
			
			amostras_conv.add(amostra_conv);
		}
		
//		Calendar c1 = (Calendar)amostras_conv.get(12348).get(1);
		
//		System.out.println(c1.getTime().toString());
		
		System.out.println(amostras_conv.size());
		
		
		
		amostras_conv = remover_amostras_invalidas(amostras_conv);
		System.out.println(amostras_conv.size());
		
//		amostras_conv = filtrar_por_data(cal_inicial,cal_final,amostras_conv);
		
		amostras_conv = agrupar_por_data(amostras_conv);
		
		
		
		salvarCSV(pasta+"filtrado_"+arquivo, converter_para_strings(amostras_conv));
		
	}
	
	private static ArrayList<ArrayList<Object>> agrupar_por_data(ArrayList<ArrayList<Object>> amostras) {

		ArrayList<ArrayList<Object>> nova_lista = new ArrayList<ArrayList<Object>>();
		
		for(int i = 0; i < amostras.size(); i+=2) {
			
			ArrayList<Object> primeira_amostra = amostras.get(i);
			ArrayList<Object> segunda_amostra = amostras.get(i+1);
			
			ArrayList<Object> nova_amostra = new ArrayList<Object>();
			
			
			nova_amostra.add(primeira_amostra.get(1));	// data
			nova_amostra.add(primeira_amostra.get(2));	// precipitacao
			nova_amostra.add(segunda_amostra.get(3));	// temp max
			nova_amostra.add(segunda_amostra.get(8));	// umidade
			
			nova_lista.add(nova_amostra);
		}
		
		return nova_lista;
	}

	public static ArrayList<ArrayList<Object>> filtrar_por_data(Calendar data_inicial, Calendar data_final, ArrayList<ArrayList<Object>> amostras) {
		
		

		ArrayList<ArrayList<Object>> nova_lista = new ArrayList<ArrayList<Object>>();
		
		for(ArrayList<Object> amostra : amostras) {
			if(((Calendar)amostra.get(1)).compareTo(data_inicial) >= 0 && ((Calendar)amostra.get(1)).compareTo(data_final) <= 0) {
				nova_lista.add(amostra);
			}
		}
		
		return nova_lista;
	}

	private static String get_date_string(Calendar cal) {
		
		String dia = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
		if(dia.length() == 1) {
			dia = "0" + dia;
		}
		String mes = String.valueOf(cal.get(Calendar.MONTH)+1);
		if(mes.length() == 1) {
			mes = "0" + mes;
		}
		
		String ano = String.valueOf(cal.get(Calendar.YEAR));
		/*
		String hora = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		if(hora.length() == 1) {
			hora = "0" + hora;
		}
		
		String minuto = String.valueOf(cal.get(Calendar.MINUTE));
		if(minuto.length() == 1) {
			minuto = "0" + minuto;
		}*/
		
		return ano + "-" + mes + "-" + dia;
	}
	
	private static ArrayList<ArrayList<String>> converter_para_strings(ArrayList<ArrayList<Object>> tabela){
		
		ArrayList<ArrayList<String>> nova_tabela = new ArrayList<ArrayList<String>>();
		
		
		
		for(ArrayList<Object> amostra : tabela) {
			
			ArrayList<String> novo_item = new ArrayList<String>();
			
			novo_item.add(get_date_string(((Calendar)amostra.get(0))));
			novo_item.add(String.valueOf(amostra.get(1)));
			novo_item.add(String.valueOf(amostra.get(2)));
			novo_item.add(String.valueOf(amostra.get(3)));
			
			nova_tabela.add(novo_item);
		}
		
		return nova_tabela;
	}

	private static ArrayList<ArrayList<Object>> remover_amostras_invalidas(ArrayList<ArrayList<Object>> amostras) {
		ArrayList<ArrayList<Object>> nova_lista = new ArrayList<ArrayList<Object>>();
		for(int i = 0; i < amostras.size()-1; i++) {
			
			ArrayList<Object> amostra = amostras.get(i);
			ArrayList<Object> prox_amostra = amostras.get(i+1);
			
			try {
				
			
				// condição para ter 2 amostras para o mesmo dia
				if(((Calendar)amostra.get(1)).get(Calendar.DAY_OF_YEAR) == ((Calendar)prox_amostra.get(1)).get(Calendar.DAY_OF_YEAR) &&
						((Calendar)amostra.get(1)).get(Calendar.YEAR) == ((Calendar)prox_amostra.get(1)).get(Calendar.YEAR)	) {
				
					// tentará pegar apenas as amostras das variáveis que serão importantes
					
						if(amostra.get(2) == null || prox_amostra.get(3) == null || prox_amostra.get(8) == null) {
							throw new NullPointerException();
						} 				
						
						nova_lista.add(amostra);
						nova_lista.add(prox_amostra);
						
						i++;
				}
			} catch (NullPointerException e) {

			}
		}
		
		return nova_lista;
		
	}
	
private static void salvarCSV(String arquivo, ArrayList<ArrayList<String>> tabela) {
		
		try {
		
			File file = new File(arquivo);
	        FileWriter fw = new FileWriter(file);
	        BufferedWriter bw = new BufferedWriter(fw);
	        
	        bw.write("\"Data\",\"Precipitacao\",\"TempMaxima\",\"Umidade Relativa Media\"");
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
		        
		        try {
		        	for (int i = 0; coluna[i] != null; i++) {
			        	lst_linha.add(coluna[i]);
			        }	
		        } catch(ArrayIndexOutOfBoundsException e) {
		        	
		        }

		        tabela.add(lst_linha);
	        }
		
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		return tabela;
	}

	public static ArrayList<ArrayList<String>> filtrar_por_data_string(Calendar data_inicial, Calendar data_final,
			ArrayList<ArrayList<String>> amostras) throws ParseException {
		

		ArrayList<ArrayList<String>> nova_lista = new ArrayList<ArrayList<String>>();
		
		for(ArrayList<String> amostra : amostras) {
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			formatter.setTimeZone(TimeZone.getTimeZone("America/Brasilia"));
			Date data_hora = formatter.parse(amostra.get(0));
			
			Calendar data = Calendar.getInstance();
			data.setTimeInMillis(data_hora.getTime());
			
			if(data.compareTo(data_inicial) >= 0 && data.compareTo(data_final) <= 0) {
				nova_lista.add(amostra);
			}
		}
		
		return nova_lista;
	}
	
}
