package main.classificadorGui;

import java.awt.EventQueue;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import utils.ConexaoDB;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import codigos.Classificador;
import main.analisador.Analisador;
import preprocessamento.calculo_risco_fogo.RF;
import preprocessamento.calculo_risco_fogo.Risco;
import preprocessamento.calculo_risco_fogo.Vegetacao;

import java.awt.Color;
import javax.swing.ImageIcon;

public class ClassificadorGUI extends JFrame {

	private JPanel contentPane;
	private static Map<String,ArrayList<String>> tabela;
	private JTextField fld_ano;
	private JTextField fld_tempmax;
	private JTextField fld_umidmin;
	private JTextField fld_diassecura;
	private JTextField fld_precipitacao;

	/**
	 * Launch the application.
	 * @throws SQLException 
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		ConexaoDB c1 = new ConexaoDB("192.168.0.103:1515/dadosmet", "view", "view");
		
		tabela = c1.getTabela();
		
		c1.encerrar_conexao();
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		/*
		for(ArrayList<String> linha : tabela) {
			String str_linha = "";
			for(String coluna : linha) {
				
				str_linha += coluna + ", ";
				
			}
			
			System.out.println(str_linha);
		}*/
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClassificadorGUI frame = new ClassificadorGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClassificadorGUI() {
		setResizable(false);
		setTitle("Classificador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Preencher a partir do banco de dados", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel.setBounds(10, 110, 604, 77);
		contentPane.add(panel);
		panel.setLayout(null);
		
		ButtonGroup radios = new ButtonGroup();
		
		JComboBox<String> cmb_mes = new JComboBox<String>();
		cmb_mes.setBounds(225, 39, 100, 20);
		panel.add(cmb_mes);
		cmb_mes.setModel(new DefaultComboBoxModel<String>(new String[] {"Janeiro", "Fevereiro", "Mar\u00E7o", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"}));
		
		JComboBox<String> cmb_dia = new JComboBox<String>();
		cmb_dia.setBounds(164, 39, 50, 20);
		panel.add(cmb_dia);
		cmb_dia.setModel(new DefaultComboBoxModel<String>(new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"}));
		
		fld_ano = new JTextField();
		fld_ano.setBounds(337, 39, 55, 20);
		panel.add(fld_ano);
		fld_ano.setColumns(10);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.RED);
		label.setBounds(462, 42, 132, 14);
		panel.add(label);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String data = "";
				
				data+=fld_ano.getText() + "-";
				
				int mes = cmb_mes.getSelectedIndex()+1;
				data+=((mes <= 9) ? "0" + String.valueOf(mes) : String.valueOf(mes)) + "-";
				
				int dia = cmb_dia.getSelectedIndex()+1;
				data+=((mes <= 9) ? "0" + String.valueOf(dia) : String.valueOf(dia));
				
				try {
					ArrayList<String> obtido = tabela.get(data);	
					
					fld_precipitacao.setText(obtido.get(1));
					fld_tempmax.setText(obtido.get(2));
					fld_umidmin.setText(obtido.get(3));
					fld_diassecura.setText(obtido.get(4));
					
					boolean houve_incendio = Boolean.parseBoolean(obtido.get(5));
					
					if(houve_incendio) {
						label.setForeground(Color.RED);
						label.setText("(houve incêndio)");
						
					} else {
						label.setForeground(Color.BLUE);
						label.setText("(sem incêndio)");
					}
					
				} catch (NullPointerException e1){
					label.setForeground(Color.BLACK);
					label.setText("(data sem registro no BD)");
					
				}
				

			}
		});
		btnOk.setBounds(402, 39, 50, 23);
		panel.add(btnOk);
		
		JLabel lblMs = new JLabel("M\u00EAs");
		lblMs.setBounds(225, 18, 46, 14);
		panel.add(lblMs);
		
		JLabel lblAno = new JLabel("Ano");
		lblAno.setBounds(337, 18, 46, 14);
		panel.add(lblAno);
		
		JLabel lblDia = new JLabel("Dia");
		lblDia.setBounds(164, 18, 46, 14);
		panel.add(lblDia);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Preencher manualmente", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(10, 198, 604, 131);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		fld_tempmax = new JTextField();
		fld_tempmax.setBounds(157, 48, 120, 20);
		panel_1.add(fld_tempmax);
		fld_tempmax.setColumns(10);
		
		JLabel lbl_tempmax = new JLabel("Temperatura M\u00E1xima");
		lbl_tempmax.setBounds(157, 23, 120, 14);
		panel_1.add(lbl_tempmax);
		lbl_tempmax.setHorizontalAlignment(SwingConstants.CENTER);
		
		fld_umidmin = new JTextField();
		fld_umidmin.setBounds(287, 48, 120, 20);
		panel_1.add(fld_umidmin);
		fld_umidmin.setColumns(10);
		
		JLabel lbl_umidmin = new JLabel("Umidade M\u00EDnima");
		lbl_umidmin.setBounds(287, 23, 120, 14);
		panel_1.add(lbl_umidmin);
		lbl_umidmin.setHorizontalAlignment(SwingConstants.CENTER);
		
		fld_diassecura = new JTextField();
		fld_diassecura.setBounds(417, 48, 120, 20);
		panel_1.add(fld_diassecura);
		fld_diassecura.setColumns(10);
		
		JLabel lbl_diassecura = new JLabel("Dias de Secura");
		lbl_diassecura.setBounds(417, 23, 162, 14);
		panel_1.add(lbl_diassecura);
		lbl_diassecura.setHorizontalAlignment(SwingConstants.CENTER);
		
		fld_precipitacao = new JTextField();
		fld_precipitacao.setBounds(27, 48, 120, 20);
		panel_1.add(fld_precipitacao);
		fld_precipitacao.setColumns(10);
		
		JLabel lbl_precipitacao = new JLabel("Precipita\u00E7\u00E3o");
		lbl_precipitacao.setBounds(27, 23, 120, 14);
		panel_1.add(lbl_precipitacao);
		lbl_precipitacao.setHorizontalAlignment(SwingConstants.CENTER);
		
		JButton btn_calcularpse = new JButton("");
		btn_calcularpse.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/calculadora.png")));
		btn_calcularpse.setBounds(547, 47, 32, 23);
		panel_1.add(btn_calcularpse);
		
		
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Selecionar modelo para realizar o c\u00E1lculo", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(10, 11, 604, 88);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JComboBox<String> cmb_selectrede = new JComboBox<String>();
		cmb_selectrede.setBounds(154, 48, 140, 20);
		cmb_selectrede.setModel(new DefaultComboBoxModel<String>(Analisador.findFoldersInDirectory(System.getProperty("user.dir")+"/redes/")));
		panel_2.add(cmb_selectrede);
		
		JRadioButton rdbtnInpe = new JRadioButton("INPE");
		rdbtnInpe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmb_selectrede.setEnabled(false);
			}
		});
		rdbtnInpe.setBounds(23, 21, 109, 23);
		panel_2.add(rdbtnInpe);
		
		
		
		JRadioButton rdbtnRedeNeuralArtificial = new JRadioButton("Rede Neural Artificial");
		rdbtnRedeNeuralArtificial.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				cmb_selectrede.setEnabled(true);
			}
		});
		rdbtnRedeNeuralArtificial.setBounds(23, 47, 125, 23);
		panel_2.add(rdbtnRedeNeuralArtificial);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Resultado", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 340, 604, 90);
		contentPane.add(panel_3);
		panel_3.setLayout(null);
		
		JLabel lbl_resultado = new JLabel("Risco INDEFINIDO");
		lbl_resultado.setHorizontalAlignment(SwingConstants.CENTER);
		lbl_resultado.setFont(new Font("Tahoma", Font.BOLD, 20));
		lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_indefinido.png")));
		lbl_resultado.setBounds(10, 32, 584, 32);
		panel_3.add(lbl_resultado);
		
		
		JButton btn_calcularrf = new JButton("Calcular Risco de Fogo");
		btn_calcularrf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_indefinido.png")));
				lbl_resultado.setText("Calculando...");
				
				Risco rf_obtido = null;
				
				if(rdbtnInpe.isSelected()) {
					try {
						rf_obtido = RF.classificar_rf_enum(RF.calcular_risco_observado(
								Double.parseDouble(fld_precipitacao.getText()),
								Double.parseDouble(fld_tempmax.getText()),
								Double.parseDouble(fld_umidmin.getText()),
								Double.parseDouble(fld_diassecura.getText()),
								Vegetacao.SAVANA_CAATINGA_ABERTA
								));
					} catch (ArrayIndexOutOfBoundsException | NumberFormatException | ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					try {
						rf_obtido = Classificador.classificarRF(
								(String) cmb_selectrede.getSelectedItem(),
								Double.parseDouble(fld_precipitacao.getText()),
								Double.parseDouble(fld_tempmax.getText()),
								Double.parseDouble(fld_umidmin.getText()),
								Double.parseDouble(fld_diassecura.getText())
								);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				if(rf_obtido == Risco.MINIMO) {
					lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_minimo.png")));
					lbl_resultado.setText("Risco MÍNIMO");
				} else if(rf_obtido == Risco.BAIXO) {
					lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_baixo.png")));
					lbl_resultado.setText("Risco BAIXO");
				} else if(rf_obtido == Risco.MEDIO) {
					lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_medio.png")));
					lbl_resultado.setText("Risco MÉDIO");
				} else if(rf_obtido == Risco.ALTO) {
					lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_alto.png")));
					lbl_resultado.setText("Risco ALTO");
				} else if(rf_obtido == Risco.CRITICO) {
					lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_critico.png")));
					lbl_resultado.setText("Risco CRÍTICO");
				} else {
					lbl_resultado.setIcon(new ImageIcon(ClassificadorGUI.class.getResource("/resources/rf_indefinido.png")));
					lbl_resultado.setText("Risco INDEFINIDO");
				}
			}
		});
		btn_calcularrf.setBounds(27, 79, 552, 36);
		panel_1.add(btn_calcularrf);
		btn_calcularrf.setFont(new Font("Tahoma", Font.BOLD, 11));

		btn_calcularpse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				CalcularPSE tela_calc = new CalcularPSE(fld_diassecura);
				
				tela_calc.setVisible(true);
			}
		});
		
		
		radios.add(rdbtnInpe);
		radios.add(rdbtnRedeNeuralArtificial);
		rdbtnRedeNeuralArtificial.setSelected(true);

	}
}
