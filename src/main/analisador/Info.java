package main.analisador;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Info extends JDialog {

	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Info dialog = new Info();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public Info() {
		setBounds(100, 100, 487, 405);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] {30, 200, 30, 200};
		gbl_contentPanel.rowHeights = new int[]{14, 14, 14, 14, 14, 14, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblTreinamento = new JLabel("TREINAMENTO");
			lblTreinamento.setFont(new Font("Tahoma", Font.PLAIN, 11));
			lblTreinamento.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_lblTreinamento = new GridBagConstraints();
			gbc_lblTreinamento.anchor = GridBagConstraints.NORTH;
			gbc_lblTreinamento.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblTreinamento.insets = new Insets(0, 0, 5, 5);
			gbc_lblTreinamento.gridx = 1;
			gbc_lblTreinamento.gridy = 0;
			contentPanel.add(lblTreinamento, gbc_lblTreinamento);
		}
		{
			JLabel lblDataset = new JLabel("DATASET");
			lblDataset.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_lblDataset = new GridBagConstraints();
			gbc_lblDataset.anchor = GridBagConstraints.NORTH;
			gbc_lblDataset.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblDataset.insets = new Insets(0, 0, 5, 0);
			gbc_lblDataset.gridx = 3;
			gbc_lblDataset.gridy = 0;
			contentPanel.add(lblDataset, gbc_lblDataset);
		}
		{
			JLabel lblQtdEntradas = new JLabel("Qtd. Entradas: <valor>");
			GridBagConstraints gbc_lblQtdEntradas = new GridBagConstraints();
			gbc_lblQtdEntradas.anchor = GridBagConstraints.NORTH;
			gbc_lblQtdEntradas.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblQtdEntradas.insets = new Insets(0, 0, 5, 5);
			gbc_lblQtdEntradas.gridx = 1;
			gbc_lblQtdEntradas.gridy = 1;
			contentPanel.add(lblQtdEntradas, gbc_lblQtdEntradas);
		}
		{
			JLabel lblBatchsize = new JLabel("Qtd. Amostras Total: <valor>");
			GridBagConstraints gbc_lblBatchsize = new GridBagConstraints();
			gbc_lblBatchsize.anchor = GridBagConstraints.NORTH;
			gbc_lblBatchsize.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblBatchsize.insets = new Insets(0, 0, 5, 0);
			gbc_lblBatchsize.gridx = 3;
			gbc_lblBatchsize.gridy = 1;
			contentPanel.add(lblBatchsize, gbc_lblBatchsize);
		}
		{
			JLabel lblQtdSadas = new JLabel("Qtd. Sa\u00EDdas: <valor>");
			GridBagConstraints gbc_lblQtdSadas = new GridBagConstraints();
			gbc_lblQtdSadas.anchor = GridBagConstraints.NORTH;
			gbc_lblQtdSadas.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblQtdSadas.insets = new Insets(0, 0, 5, 5);
			gbc_lblQtdSadas.gridx = 1;
			gbc_lblQtdSadas.gridy = 2;
			contentPanel.add(lblQtdSadas, gbc_lblQtdSadas);
		}
		{
			JLabel lblQtdAmostrasTreinamento = new JLabel("Qtd. Amostras Treinam.: <valor>");
			GridBagConstraints gbc_lblQtdAmostrasTreinamento = new GridBagConstraints();
			gbc_lblQtdAmostrasTreinamento.anchor = GridBagConstraints.NORTH;
			gbc_lblQtdAmostrasTreinamento.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblQtdAmostrasTreinamento.insets = new Insets(0, 0, 5, 0);
			gbc_lblQtdAmostrasTreinamento.gridx = 3;
			gbc_lblQtdAmostrasTreinamento.gridy = 2;
			contentPanel.add(lblQtdAmostrasTreinamento, gbc_lblQtdAmostrasTreinamento);
		}
		{
			JLabel lblQtdCamadasOcultas = new JLabel("Qtd. Camadas Ocultas: <valor>");
			GridBagConstraints gbc_lblQtdCamadasOcultas = new GridBagConstraints();
			gbc_lblQtdCamadasOcultas.anchor = GridBagConstraints.NORTH;
			gbc_lblQtdCamadasOcultas.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblQtdCamadasOcultas.insets = new Insets(0, 0, 5, 5);
			gbc_lblQtdCamadasOcultas.gridx = 1;
			gbc_lblQtdCamadasOcultas.gridy = 3;
			contentPanel.add(lblQtdCamadasOcultas, gbc_lblQtdCamadasOcultas);
		}
		{
			JLabel lblQtdAmostrasValidao = new JLabel("Qtd. Amostras Valida\u00E7\u00E3o: <valor>");
			GridBagConstraints gbc_lblQtdAmostrasValidao = new GridBagConstraints();
			gbc_lblQtdAmostrasValidao.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblQtdAmostrasValidao.insets = new Insets(0, 0, 5, 0);
			gbc_lblQtdAmostrasValidao.gridx = 3;
			gbc_lblQtdAmostrasValidao.gridy = 3;
			contentPanel.add(lblQtdAmostrasValidao, gbc_lblQtdAmostrasValidao);
		}
		{
			JLabel lblNsEmCada = new JLabel("Qtd. N\u00F3s Camadas Ocultas: <valor>");
			GridBagConstraints gbc_lblNsEmCada = new GridBagConstraints();
			gbc_lblNsEmCada.anchor = GridBagConstraints.NORTHWEST;
			gbc_lblNsEmCada.insets = new Insets(0, 0, 5, 5);
			gbc_lblNsEmCada.gridx = 1;
			gbc_lblNsEmCada.gridy = 4;
			contentPanel.add(lblNsEmCada, gbc_lblNsEmCada);
		}
		{
			JLabel lblRazo = new JLabel("Raz\u00E3o treinam./total: <valor>");
			GridBagConstraints gbc_lblRazo = new GridBagConstraints();
			gbc_lblRazo.anchor = GridBagConstraints.NORTH;
			gbc_lblRazo.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblRazo.insets = new Insets(0, 0, 5, 0);
			gbc_lblRazo.gridx = 3;
			gbc_lblRazo.gridy = 4;
			contentPanel.add(lblRazo, gbc_lblRazo);
		}
		{
			JLabel lblTaxaDeAprendizado = new JLabel("Taxa de Aprendizado: <valor>");
			GridBagConstraints gbc_lblTaxaDeAprendizado = new GridBagConstraints();
			gbc_lblTaxaDeAprendizado.anchor = GridBagConstraints.NORTH;
			gbc_lblTaxaDeAprendizado.fill = GridBagConstraints.HORIZONTAL;
			gbc_lblTaxaDeAprendizado.insets = new Insets(0, 0, 0, 5);
			gbc_lblTaxaDeAprendizado.gridx = 1;
			gbc_lblTaxaDeAprendizado.gridy = 5;
			contentPanel.add(lblTaxaDeAprendizado, gbc_lblTaxaDeAprendizado);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("FECHAR");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		{
			JPanel titulo = new JPanel();
			titulo.setBorder(new EmptyBorder(0, 0, 0, 0));
			getContentPane().add(titulo, BorderLayout.NORTH);
			{
				JLabel label = new JLabel("<Nome do Modelo>");
				label.setFont(new Font("Tahoma", Font.BOLD, 11));
				titulo.add(label);
			}
		}
	}

}
