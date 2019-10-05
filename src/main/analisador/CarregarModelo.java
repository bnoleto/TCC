package main.analisador;

import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.swing.JDialog;

import estatisticas.analise_grafico.AnalisarGrafico;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;

public class CarregarModelo extends JDialog {
	
	AnalisarGrafico modelo;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CarregarModelo dialog = new CarregarModelo("");
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public CarregarModelo(String nome_rede) throws ClassNotFoundException, IOException {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 450, 175);
		
		JLabel lblCarregandoModelo = new JLabel("Carregando Modelo \""+nome_rede+"\"...");
		lblCarregandoModelo.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblCarregandoModelo, BorderLayout.CENTER);
		
		setVisible(true);
		
		this.modelo = new AnalisarGrafico(nome_rede);

		setVisible(false);

	}
	
	public AnalisarGrafico get_modelo(){
		return this.modelo;
	}

}
