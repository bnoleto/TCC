package main.classificadorGui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import preprocessamento.calculo_risco_fogo.RF;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;

public class CalcularPSE extends JFrame {
/*
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
	*/
	private JPanel contentPane;
	private JTextField fld_prec1;
	private JTextField fld_prec2;
	private JTextField fld_prec4;
	private JTextField fld_prec3;
	private JTextField fld_prec10;
	private JTextField fld_prec5;
	private JTextField fld_prec15;
	private JTextField fld_prec30;
	private JTextField fld_prec60;
	private JTextField fld_prec90;
	private JTextField fld_prec120;

	/**
	 * Launch the application.
	 *//*
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CalcularPSE frame = new CalcularPSE();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	private double parseDouble(String valor) {
		try {
			return Double.parseDouble(valor);
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	/**
	 * Create the frame.
	 */
	public CalcularPSE(JTextField field_resultado) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(CalcularPSE.class.getResource("/resources/calculadora.png")));
		setTitle("Calcular PSE");
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 348, 209);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lbl_prec1 = new JLabel("Prec1");
		lbl_prec1.setBounds(10, 11, 46, 14);
		contentPane.add(lbl_prec1);
		
		fld_prec1 = new JTextField();
		lbl_prec1.setLabelFor(fld_prec1);
		fld_prec1.setBounds(66, 8, 86, 20);
		contentPane.add(fld_prec1);
		fld_prec1.setColumns(10);
		
		JLabel lbl_prec2 = new JLabel("Prec2");
		lbl_prec2.setBounds(10, 39, 46, 14);
		contentPane.add(lbl_prec2);
		
		fld_prec2 = new JTextField();
		lbl_prec2.setLabelFor(fld_prec2);
		fld_prec2.setColumns(10);
		fld_prec2.setBounds(66, 36, 86, 20);
		contentPane.add(fld_prec2);
		
		fld_prec4 = new JTextField();
		fld_prec4.setColumns(10);
		fld_prec4.setBounds(66, 92, 86, 20);
		contentPane.add(fld_prec4);
		
		JLabel lbl_prec4 = new JLabel("Prec4");
		lbl_prec4.setBounds(10, 95, 46, 14);
		contentPane.add(lbl_prec4);
		
		JLabel lbl_prec3 = new JLabel("Prec3");
		lbl_prec3.setBounds(10, 67, 46, 14);
		contentPane.add(lbl_prec3);
		
		fld_prec3 = new JTextField();
		fld_prec3.setColumns(10);
		fld_prec3.setBounds(66, 64, 86, 20);
		contentPane.add(fld_prec3);
		
		fld_prec10 = new JTextField();
		fld_prec10.setColumns(10);
		fld_prec10.setBounds(66, 148, 86, 20);
		contentPane.add(fld_prec10);
		
		JLabel lbl_prec10 = new JLabel("Prec10");
		lbl_prec10.setBounds(10, 151, 46, 14);
		contentPane.add(lbl_prec10);
		
		JLabel lbl_prec5 = new JLabel("Prec5");
		lbl_prec5.setBounds(10, 123, 46, 14);
		contentPane.add(lbl_prec5);
		
		fld_prec5 = new JTextField();
		fld_prec5.setColumns(10);
		fld_prec5.setBounds(66, 120, 86, 20);
		contentPane.add(fld_prec5);
		
		JLabel lbl_prec15 = new JLabel("Prec15");
		lbl_prec15.setBounds(181, 11, 46, 14);
		contentPane.add(lbl_prec15);
		
		fld_prec15 = new JTextField();
		fld_prec15.setColumns(10);
		fld_prec15.setBounds(237, 8, 86, 20);
		contentPane.add(fld_prec15);
		
		JLabel lbl_prec30 = new JLabel("Prec30");
		lbl_prec30.setBounds(181, 39, 46, 14);
		contentPane.add(lbl_prec30);
		
		fld_prec30 = new JTextField();
		fld_prec30.setColumns(10);
		fld_prec30.setBounds(237, 36, 86, 20);
		contentPane.add(fld_prec30);
		
		JLabel lbl_prec60 = new JLabel("Prec60");
		lbl_prec60.setBounds(181, 67, 46, 14);
		contentPane.add(lbl_prec60);
		
		fld_prec60 = new JTextField();
		fld_prec60.setColumns(10);
		fld_prec60.setBounds(237, 64, 86, 20);
		contentPane.add(fld_prec60);
		
		JLabel lbl_prec90 = new JLabel("Prec90");
		lbl_prec90.setBounds(181, 95, 46, 14);
		contentPane.add(lbl_prec90);
		
		fld_prec90 = new JTextField();
		fld_prec90.setColumns(10);
		fld_prec90.setBounds(237, 92, 86, 20);
		contentPane.add(fld_prec90);
		
		JLabel lbl_prec120 = new JLabel("Prec120");
		lbl_prec120.setBounds(181, 123, 46, 14);
		contentPane.add(lbl_prec120);
		
		fld_prec120 = new JTextField();
		fld_prec120.setColumns(10);
		fld_prec120.setBounds(237, 120, 86, 20);
		contentPane.add(fld_prec120);
		
		JButton btnCalcular = new JButton("Calcular");
		btnCalcular.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				double[] precipitacoes = {
						parseDouble(fld_prec1.getText()),
						parseDouble(fld_prec2.getText()),
						parseDouble(fld_prec3.getText()),
						parseDouble(fld_prec4.getText()),
						parseDouble(fld_prec5.getText()),
						parseDouble(fld_prec10.getText()),
						parseDouble(fld_prec15.getText()),
						parseDouble(fld_prec30.getText()),
						parseDouble(fld_prec60.getText()),
						parseDouble(fld_prec90.getText()),
						parseDouble(fld_prec120.getText()),
						};
				
				try {
					field_resultado.setText(String.valueOf(RF.get_pse(precipitacoes)));
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				dispose();
			}
		});
		btnCalcular.setBounds(181, 147, 142, 23);
		contentPane.add(btnCalcular);
	}
}
