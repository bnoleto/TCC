package main.analisador;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import estatisticas.analise_grafico.AnalisarGrafico;
import estatisticas.analise_grafico.Dataset;
import estatisticas.analise_grafico.Stats;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import javax.swing.event.ChangeEvent;
import javax.swing.Box;

public class Analisador {
	
	private HashMap<String, Component> componentMap_panelDados = new HashMap<String,Component>();
	
	private JFrame frmAnliseDosGrficos;
	private JLabel epoca_menor_loss_1;
	private JTextField epoca_field;
	private AnalisarGrafico arquivo_atual;
	private HashMap<String, AnalisarGrafico> arquivos = new HashMap<String,AnalisarGrafico>();
	
	public static String[] findFoldersInDirectory(String directoryPath) {
	    File directory = new File(directoryPath);
		
	    FileFilter directoryFileFilter = new FileFilter() {
	        public boolean accept(File file) {

	            return file.isDirectory();
	        }
	    };
			
	    File[] directoryListAsFile = directory.listFiles(directoryFileFilter);
	    List<String> foldersInDirectory = new ArrayList<String>(directoryListAsFile.length);
	    for (File directoryAsFile : directoryListAsFile) {
	    	
	    	File estatisticas = new File(directoryAsFile, "estatisticas.stats");
	    	
	    	if(estatisticas.exists()) {
	    		foldersInDirectory.add(directoryAsFile.getName());	
	    	}
	    }
	    
	    String[] resultado = new String[foldersInDirectory.size()];
	    
	    for(int i = 0; i<resultado.length; i++) {
	    	resultado[i] = foldersInDirectory.get(i);
	    }

	    return resultado;
	}
	
	private void atualizarDados(int epoca) throws ClassNotFoundException, IOException {
		
		
		
		JLabel treinamento_acuracia = (JLabel) getComponentByName("treinamento_acuracia");
		JLabel treinamento_dados = (JLabel) getComponentByName("treinamento_dados");
		JLabel treinamento_precisao = (JLabel) getComponentByName("treinamento_precisao");
		JLabel treinamento_f1score = (JLabel) getComponentByName("treinamento_f1score");
		JLabel treinamento_recall = (JLabel) getComponentByName("treinamento_recall");
		JLabel treinamento_loss = (JLabel) getComponentByName("treinamento_loss");
		
		JLabel validacao_acuracia = (JLabel) getComponentByName("validacao_acuracia");
		JLabel validacao_dados = (JLabel) getComponentByName("validacao_dados");
		JLabel validacao_precisao = (JLabel) getComponentByName("validacao_precisao");
		JLabel validacao_f1score = (JLabel) getComponentByName("validacao_f1score");
		JLabel validacao_recall = (JLabel) getComponentByName("validacao_recall");
		JLabel validacao_loss = (JLabel) getComponentByName("validacao_loss");
		
		// acuracia - dados na epoca - precisao - f1score - recall - loss
		
		// DADOS DE TREINAMENTO
		treinamento_acuracia.setText("Acurácia: " + arquivo_atual.get_stat_epoca(epoca, Dataset.TREINAMENTO, Stats.ACURACIA));
		treinamento_precisao.setText("Precisão: " + arquivo_atual.get_stat_epoca(epoca, Dataset.TREINAMENTO, Stats.PRECISAO));
		treinamento_f1score.setText("F1 Score: " + arquivo_atual.get_stat_epoca(epoca, Dataset.TREINAMENTO, Stats.F1SCORE));
		treinamento_recall.setText("Recall: " + arquivo_atual.get_stat_epoca(epoca, Dataset.TREINAMENTO, Stats.RECALL));
		treinamento_loss.setText("LOSS: " + arquivo_atual.get_stat_epoca(epoca, Dataset.TREINAMENTO, Stats.LOSS));
		treinamento_dados.setText("(Dados na época " + epoca + ")");
		
		// DADOS DE VALIDACAO
		validacao_acuracia.setText("Acurácia: " + arquivo_atual.get_stat_epoca(epoca, Dataset.VALIDACAO, Stats.ACURACIA));
		validacao_precisao.setText("Precisão: " + arquivo_atual.get_stat_epoca(epoca, Dataset.VALIDACAO, Stats.PRECISAO));
		validacao_f1score.setText("F1 Score: " + arquivo_atual.get_stat_epoca(epoca, Dataset.VALIDACAO, Stats.F1SCORE));
		validacao_recall.setText("Recall: " + arquivo_atual.get_stat_epoca(epoca, Dataset.VALIDACAO, Stats.RECALL));
		validacao_loss.setText("LOSS: " + arquivo_atual.get_stat_epoca(epoca, Dataset.VALIDACAO, Stats.LOSS));
		validacao_dados.setText("(Dados na época " + epoca + ")");
		
	}

	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Analisador window = new Analisador();
					window.frmAnliseDosGrficos.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void createComponentMap(JPanel inicial) {
        Component[] components = inicial.getComponents();
        
        for (int i=0; i < components.length; i++) {
            componentMap_panelDados.put(components[i].getName(), components[i]);
        }
	}
	
	private void addToComponentMap(String nome, Component component) {
		componentMap_panelDados.put(nome, component);
	}
	
	private void addToArquivosMap(String nome, AnalisarGrafico arquivo) {
		arquivos.put(nome, arquivo);
	}

	public Component getComponentByName(String name) {
        if (componentMap_panelDados.containsKey(name)) {
                return (Component) componentMap_panelDados.get(name);
        }
        else return null;
	}
	
	public AnalisarGrafico getArquivoByName(String name) {
        if (arquivos.containsKey(name)) {
                return (AnalisarGrafico) arquivos.get(name);
        }
        else return null;
	}

	/**
	 * Create the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public Analisador() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	private void initialize() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		
		ButtonGroup bt = new ButtonGroup();
		
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		frmAnliseDosGrficos = new JFrame();
		frmAnliseDosGrficos.setTitle("An\u00E1lise dos Gr\u00E1ficos");
		frmAnliseDosGrficos.setResizable(false);
		
		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		frmAnliseDosGrficos.setBounds(300, 200, 400, 558);
		frmAnliseDosGrficos.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		final Grafico grafico = new Grafico(frmAnliseDosGrficos.getX()+frmAnliseDosGrficos.getWidth(),frmAnliseDosGrficos.getY());
		
		JPanel panel_treinamento = new JPanel();
		JPanel panel_validacao = new JPanel();
		
		JPanel panel_dados = new JPanel();
		frmAnliseDosGrficos.getContentPane().add(panel_dados, BorderLayout.CENTER);
		
				
				JComboBox<String> combo_selecao_rede = new JComboBox<String>();
				combo_selecao_rede.setName("combo_selecao_rede");
				
				JRadioButton rdbtnAcurcia = new JRadioButton("Acurácia");
				rdbtnAcurcia.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/acuracia.png");
						
					}
				});
				bt.add(rdbtnAcurcia);
				rdbtnAcurcia.setName("rdbtnAcurcia");
				
				JRadioButton rdbtnPreciso = new JRadioButton("Precisão");
				rdbtnPreciso.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/precisao.png");

						//grafico.setIcon(new ImageIcon(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/precisao.png"));
					}
				});
				bt.add(rdbtnPreciso);
				rdbtnPreciso.setName("rdbtnPreciso");
				
				JRadioButton rdbtnLoss = new JRadioButton("Loss");
				rdbtnLoss.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/loss.png");
					}
				});
				rdbtnLoss.setSelected(true);
				bt.add(rdbtnLoss);
				rdbtnLoss.setName("rdbtnLoss");
				
				JRadioButton rdbtnFScore = new JRadioButton("F1 Score");
				rdbtnFScore.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/f1score.png");
					}
				});
				bt.add(rdbtnFScore);
				rdbtnFScore.setName("rdbtnFScore");
				
				JRadioButton rdbtnRecall = new JRadioButton("Recall");
				rdbtnRecall.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/recall.png");
					}
				});
				bt.add(rdbtnRecall);
				rdbtnRecall.setName("rdbtnRecall");
				
				
				
				combo_selecao_rede.setModel(new DefaultComboBoxModel<String>(findFoldersInDirectory(System.getProperty("user.dir")+"/redes/")));
				
				arquivo_atual = new AnalisarGrafico(combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex()));
				addToArquivosMap(combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex()), arquivo_atual);
				
				frmAnliseDosGrficos.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				
				JLabel label_epoca_menor_loss_1 = new JLabel("\u00C9poca com menor LOSS na valida\u00E7\u00E3o: ");
				label_epoca_menor_loss_1.setName("label_epoca_menor_loss");
				
				epoca_menor_loss_1 = new JLabel(String.valueOf(arquivo_atual.get_epoca_menor_loss_validacao()));
				epoca_menor_loss_1.setFont(new Font("Tahoma", Font.BOLD, 11));
				epoca_menor_loss_1.setName("epoca_menor_loss_1");
				
				JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
				tabbedPane.setBackground(UIManager.getColor("Desktop.background"));
				tabbedPane.setName("tabbedPane");
				
				JButton btn_atualizar = new JButton("");
				btn_atualizar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int selecionado = combo_selecao_rede.getSelectedIndex();
						
						combo_selecao_rede.setModel(new DefaultComboBoxModel<String>(findFoldersInDirectory(System.getProperty("user.dir")+"/redes/")));
						
						combo_selecao_rede.setSelectedIndex(selecionado);
					}
				});
				btn_atualizar.setIcon(new ImageIcon(Analisador.class.getResource("/resources/atualizar.png")));
				btn_atualizar.setName("btn_atualizar");
				
				JSlider slider = new JSlider();
				slider.setBorder(null);
				slider.setName("slider");
				slider.setMinimum(0);
				slider.setMaximum(arquivo_atual.get_qtd_epocas()-1);
				slider.setValue(arquivo_atual.get_epoca_menor_loss_validacao());
				
				JLabel lblSelecionarpoca = new JLabel("Selecionar \u00E9poca:");
				lblSelecionarpoca.setName("lblSelecionarpoca");
				
				epoca_field = new JTextField();
				epoca_field.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							slider.setValue(Integer.parseInt(epoca_field.getText()));
						} catch (NumberFormatException ex) {
						}
						
					}
				});
				epoca_field.setName("epoca_field");
				epoca_field.setColumns(10);
				epoca_field.setText(String.valueOf(slider.getValue()));
				
				combo_selecao_rede.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						String opcao = null;
						
						if(rdbtnAcurcia.isSelected()) {
							opcao = "acuracia";
						} else if(rdbtnPreciso.isSelected()){
							opcao = "precisao";
						} else if(rdbtnLoss.isSelected()){
							opcao = "loss";
						} else if(rdbtnFScore.isSelected()){
							opcao = "f1score";
						} else if(rdbtnRecall.isSelected()){
							opcao = "recall";
						}
						
						try {
							
							frmAnliseDosGrficos.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							
							arquivo_atual = getArquivoByName(combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex()));
							
							if (arquivo_atual == null) {
								arquivo_atual = new AnalisarGrafico(combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex()));
								addToArquivosMap(combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex()), arquivo_atual);
							}
							
							frmAnliseDosGrficos.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
							
							int menor_loss_epoca = arquivo_atual.get_epoca_menor_loss_validacao();
							
							JLabel label_epoca_menor_loss = (JLabel) getComponentByName("epoca_menor_loss_1");
							label_epoca_menor_loss.setText(String.valueOf(menor_loss_epoca));
							
							atualizarDados(menor_loss_epoca);
							slider.setValue(menor_loss_epoca);
							slider.setMaximum(arquivo_atual.get_qtd_epocas()-1);
						} catch (ClassNotFoundException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						
						grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/"+opcao +".png");
					}
				});
				
				slider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent arg0) {
						epoca_field.setText(String.valueOf(slider.getValue()));
						try {
							atualizarDados(slider.getValue());
						} catch (ClassNotFoundException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				
				JLabel lblModelo = new JLabel("Modelo:");
				lblModelo.setName("lblModelo");
				
				JButton btnInformaesDaRede = new JButton("Informa\u00E7\u00F5es do Modelo");
				btnInformaesDaRede.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JDialog info = new Info();
						info.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						info.setVisible(true);
						
					}
				});
				
				
				grafico.alterar_grafico(System.getProperty("user.dir")+"/redes/"+combo_selecao_rede.getItemAt(combo_selecao_rede.getSelectedIndex())+"/graficos/loss.png");
				
				GroupLayout gl_panel_dados = new GroupLayout(panel_dados);
				gl_panel_dados.setHorizontalGroup(
					gl_panel_dados.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_dados.createSequentialGroup()
							.addGroup(gl_panel_dados.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_dados.createSequentialGroup()
									.addComponent(slider, GroupLayout.PREFERRED_SIZE, 226, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(epoca_field, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
								.addGroup(gl_panel_dados.createSequentialGroup()
									.addComponent(label_epoca_menor_loss_1, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE)
									.addGap(6)
									.addComponent(epoca_menor_loss_1, GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE))
								.addGroup(gl_panel_dados.createSequentialGroup()
									.addComponent(lblSelecionarpoca)
									.addPreferredGap(ComponentPlacement.RELATED, 233, Short.MAX_VALUE))
								.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
								.addComponent(btnInformaesDaRede, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
								.addGroup(gl_panel_dados.createSequentialGroup()
									.addGroup(gl_panel_dados.createParallelGroup(Alignment.LEADING)
										.addComponent(rdbtnRecall)
										.addComponent(rdbtnLoss))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel_dados.createParallelGroup(Alignment.LEADING)
										.addComponent(rdbtnPreciso)
										.addGroup(gl_panel_dados.createSequentialGroup()
											.addComponent(rdbtnFScore)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(rdbtnAcurcia))))
								.addGroup(gl_panel_dados.createSequentialGroup()
									.addComponent(lblModelo)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(combo_selecao_rede, 0, 235, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btn_atualizar)))
							.addContainerGap())
				);
				gl_panel_dados.setVerticalGroup(
					gl_panel_dados.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_dados.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panel_dados.createParallelGroup(Alignment.BASELINE)
								.addComponent(epoca_menor_loss_1)
								.addComponent(label_epoca_menor_loss_1))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addGroup(gl_panel_dados.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel_dados.createSequentialGroup()
									.addComponent(lblSelecionarpoca)
									.addGap(9)
									.addComponent(slider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addComponent(epoca_field, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(18)
							.addComponent(btnInformaesDaRede)
							.addGap(18)
							.addGroup(gl_panel_dados.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnLoss)
								.addComponent(rdbtnPreciso))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel_dados.createParallelGroup(Alignment.BASELINE)
								.addComponent(rdbtnRecall)
								.addComponent(rdbtnFScore)
								.addComponent(rdbtnAcurcia))
							.addGap(12)
							.addGroup(gl_panel_dados.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(btn_atualizar, 0, 0, Short.MAX_VALUE)
								.addGroup(gl_panel_dados.createParallelGroup(Alignment.BASELINE)
									.addComponent(combo_selecao_rede, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblModelo, GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)))
							.addContainerGap(77, Short.MAX_VALUE))
				);
				
				
				panel_treinamento.setBackground(UIManager.getColor("List.background"));
				tabbedPane.addTab("Treinamento", null, panel_treinamento, null);
				
				JLabel treinamento_epoca = new JLabel("(Dados na \u00E9poca <valor>)");
				treinamento_epoca.setHorizontalAlignment(SwingConstants.RIGHT);
				addToComponentMap("treinamento_dados", treinamento_epoca);
				
						JLabel treinamento_acuracia = new JLabel("Acur\u00E1cia:");
						addToComponentMap("treinamento_acuracia", treinamento_acuracia);
						
						JLabel treinamento_precisao = new JLabel("Precis\u00E3o:");
						addToComponentMap("treinamento_precisao", treinamento_precisao);
						
						JLabel treinamento_f1score = new JLabel("F1 Score:");
						addToComponentMap("treinamento_f1score", treinamento_f1score);
						
						JLabel treinamento_recall = new JLabel("Recall:");
						addToComponentMap("treinamento_recall", treinamento_recall);
						
						JLabel treinamento_loss = new JLabel("LOSS:");
						addToComponentMap("treinamento_loss", treinamento_loss);
						treinamento_loss.setFont(new Font("Tahoma", Font.BOLD, 11));
						GroupLayout gl_panel_treinamento = new GroupLayout(panel_treinamento);
						gl_panel_treinamento.setHorizontalGroup(
							gl_panel_treinamento.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_panel_treinamento.createSequentialGroup()
									.addContainerGap()
									.addGroup(gl_panel_treinamento.createParallelGroup(Alignment.LEADING)
										.addComponent(treinamento_epoca, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
										.addComponent(treinamento_acuracia, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
										.addComponent(treinamento_precisao, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
										.addComponent(treinamento_f1score, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
										.addComponent(treinamento_recall, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
										.addComponent(treinamento_loss, GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
									.addContainerGap())
						);
						gl_panel_treinamento.setVerticalGroup(
							gl_panel_treinamento.createParallelGroup(Alignment.TRAILING)
								.addGroup(Alignment.LEADING, gl_panel_treinamento.createSequentialGroup()
									.addContainerGap()
									.addComponent(treinamento_acuracia)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(treinamento_precisao)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(treinamento_f1score)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(treinamento_recall)
									.addGap(18)
									.addComponent(treinamento_loss)
									.addPreferredGap(ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
									.addComponent(treinamento_epoca)
									.addContainerGap())
						);
						panel_treinamento.setLayout(gl_panel_treinamento);
						
						
						panel_validacao.setBackground(UIManager.getColor("List.background"));
						tabbedPane.addTab("Valida\u00E7\u00E3o", null, panel_validacao, null);
						
						JLabel validacao_acuracia = new JLabel("Acur\u00E1cia:");
						addToComponentMap("validacao_acuracia", validacao_acuracia);
						
						JLabel validacao_epoca = new JLabel("(Dados na \u00E9poca <valor>)");
						validacao_epoca.setHorizontalAlignment(SwingConstants.RIGHT);
						addToComponentMap("validacao_dados", validacao_epoca);
						
						JLabel validacao_precisao = new JLabel("Precis\u00E3o:");
						addToComponentMap("validacao_precisao", validacao_precisao);
						
						JLabel validacao_f1score = new JLabel("F1 Score:");
						addToComponentMap("validacao_f1score", validacao_f1score);
						
						JLabel validacao_recall = new JLabel("Recall:");
						addToComponentMap("validacao_recall", validacao_recall);
						
						JLabel validacao_loss = new JLabel("LOSS:");
						addToComponentMap("validacao_loss", validacao_loss);
						validacao_loss.setFont(new Font("Tahoma", Font.BOLD, 11));
						GroupLayout gl_panel_validacao = new GroupLayout(panel_validacao);
						gl_panel_validacao.setHorizontalGroup(
							gl_panel_validacao.createParallelGroup(Alignment.TRAILING)
								.addGap(0, 281, Short.MAX_VALUE)
								.addGroup(gl_panel_validacao.createSequentialGroup()
									.addContainerGap()
									.addGroup(gl_panel_validacao.createParallelGroup(Alignment.TRAILING)
										.addComponent(validacao_acuracia, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
										.addComponent(validacao_epoca, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
										.addComponent(validacao_precisao, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
										.addComponent(validacao_f1score, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
										.addComponent(validacao_recall, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
										.addComponent(validacao_loss, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
									.addContainerGap())
						);
						gl_panel_validacao.setVerticalGroup(
							gl_panel_validacao.createParallelGroup(Alignment.TRAILING)
								.addGap(0, 190, Short.MAX_VALUE)
								.addGroup(gl_panel_validacao.createSequentialGroup()
									.addContainerGap()
									.addComponent(validacao_acuracia)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(validacao_precisao)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(validacao_f1score)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(validacao_recall)
									.addGap(18)
									.addComponent(validacao_loss)
									.addPreferredGap(ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
									.addComponent(validacao_epoca)
									.addContainerGap())
						);
						panel_validacao.setLayout(gl_panel_validacao);
						panel_dados.setLayout(gl_panel_dados);
						
						Component horizontalStrut = Box.createHorizontalStrut(20);
						frmAnliseDosGrficos.getContentPane().add(horizontalStrut, BorderLayout.WEST);
						
						Component horizontalStrut_1 = Box.createHorizontalStrut(20);
						frmAnliseDosGrficos.getContentPane().add(horizontalStrut_1, BorderLayout.EAST);

		createComponentMap((JPanel)frmAnliseDosGrficos.getContentPane().getComponent(0));
		
		atualizarDados(arquivo_atual.get_epoca_menor_loss_validacao());
		
		Double[] classe0 = arquivo_atual.get_dados().get(1).get(99999).get(1);
		Double[] classe1 = arquivo_atual.get_dados().get(1).get(99999).get(2);
		Double[] classe2 = arquivo_atual.get_dados().get(1).get(99999).get(3);
		Double[] classe3 = arquivo_atual.get_dados().get(1).get(99999).get(4);
		Double[] classe4 = arquivo_atual.get_dados().get(1).get(99999).get(5);
		
		System.out.println(classe0[0]);
		System.out.println(classe1[0]);
		System.out.println(classe2[0]);
		System.out.println(classe3[0]);
		System.out.println(classe4[0]);


	}
}
