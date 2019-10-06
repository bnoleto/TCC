package utils;

import java.util.Calendar;

public class Horario {
	
	public static String getDiaHora() {
		Calendar agora = Calendar.getInstance();
		
		String dia = String.format("%02d", agora.get(Calendar.DAY_OF_MONTH));
		String mes = String.format("%02d", agora.get(Calendar.MONTH)+1);
		String ano = String.format("%04d", agora.get(Calendar.YEAR));
		
		String hora = String.format("%02d", agora.get(Calendar.HOUR_OF_DAY));
		String minuto = String.format("%02d", agora.get(Calendar.MINUTE));
		
		return ano+mes+dia+"_"+hora+minuto;
	}

}
