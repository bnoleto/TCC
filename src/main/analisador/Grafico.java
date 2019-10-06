package main.analisador;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Window.Type;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Color;

public class Grafico extends JFrame {

	private JPanel contentPane;
	
	private JLabel grafico;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Grafico frame = new Grafico(100,100,"/media/bnoleto/Bruno Noleto/Programas/###NOLETO/workspace/tcc_workspace/TCC/redes/20191001_2342/acuracia.png");
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
	public Grafico(int x, int y,String arquivo) {
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		grafico = new JLabel("");
		grafico.setHorizontalAlignment(SwingConstants.CENTER);
		grafico.setIcon(new ImageIcon(arquivo));

		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(grafico);
		
		setBounds(x, y, 640, 520);
		
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public Grafico(int x, int y) {
		setBackground(Color.WHITE);
		setResizable(false);
		setType(Type.UTILITY);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		
		grafico = new JLabel("Carregando...");
		grafico.setIcon(null);
		grafico.setHorizontalAlignment(SwingConstants.CENTER);
		grafico.setBackground(Color.WHITE);

		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(grafico);
		
		setBounds(x, y, 640, 520);
		
		setVisible(true);
		
	}
	
	public void alterar_grafico(String arquivo) {
		
		grafico.setIcon(new ImageIcon(arquivo));
		
	}

}
