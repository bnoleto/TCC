package main;

import java.io.IOException;
import codigos.Classificador;

public class TesteCondicoesMet {
		
	public static void main(String[] args) throws IOException, InterruptedException {
		
		System.out.println("== O risco de queimada é " + Classificador.classificarRF("20191005_2034", 5, 34, 30, 30) + " ==");
		
	}
	
	

}
