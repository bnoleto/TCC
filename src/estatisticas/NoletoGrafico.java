package estatisticas;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

public class NoletoGrafico {
	/*
	public static void gerarGrafico(ArrayList<ArrayList<ArrayList<Double[]>>> dados, File dir, Stats stat) throws IOException {
		gerarChart(dados, 0,stat.ordinal(),"Treinamento: "+stat.name().toUpperCase(), "�POCA", stat.name().toUpperCase(), dir+"/"+stat.toString().toLowerCase(),true);
		
		System.out.println("Gr�fico "+ stat.name()+ " gerado!");
	}*/
	
	public static void gerarGraficos(String nome_rede) throws IOException, ClassNotFoundException {
		
		File dir = new File(System.getProperty("user.dir") + "/redes/" +nome_rede+"/");
    	
    	FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/redes/"+nome_rede+"/estatisticas.stats");
        ObjectInputStream ois = new ObjectInputStream(fis);
        ArrayList<ArrayList<ArrayList<Double[]>>> dados = (ArrayList<ArrayList<ArrayList<Double[]>>>) ois.readObject();
        ois.close();
        
        NoletoGrafico.gerarGraficos(dados, dir);
        
        System.out.println("Gráficos gerados!");
		
	}

	public static void gerarGraficos(ArrayList<ArrayList<ArrayList<Double[]>>> dados, File dir2) throws IOException {
		
		File dir = new File (dir2.getPath()+"/graficos/");
		dir.mkdirs();
		
		gerarChart(dados, 0, 0,"Treinamento: ACURACIA", "ÉPOCA", "ACURÁCIA", dir+"/acuracia",true);
		gerarChart(dados, 0, 1,"Treinamento: PRECISAO", "ÉPOCA", "PRECISÃO", dir+"/precisao",true);
		gerarChart(dados, 0, 2,"Treinamento: RECALL", "ÉPOCA", "RECALL", dir+"/recall",true);
		gerarChart(dados, 0, 3,"Treinamento: F1 SCORE", "ÉPOCA", "F1 SCORE", dir+"/f1score",true);
		gerarChart(dados, 0, 4,"Treinamento: ERRO MEDIO", "ÉPOCA", "LOSS", dir+"/loss",false);
		
		// CHARTS DE CADA CLASSE
		for(int i = 1; i <= 5; i++) {
			gerarChart(dados, i, 0,"Treinamento: PRECISAO (classe " + (i-1) +")", "ÉPOCA", "PRECISAO", dir+"/precisao_c"+(i-1),true);
			gerarChart(dados, i, 1,"Treinamento: RECALL (classe " + (i-1) + ")", "ÉPOCA", "RECALL", dir+"/recall_c"+(i-1),true);
			gerarChart(dados, i, 2,"Treinamento: F1 SCORE (classe " + (i-1) + ")", "ÉPOCA", "F1 SCORE", dir+"/f1score_c"+(i-1),true);
		}
		
		
		
	}
	
	private static void gerarChart(ArrayList<ArrayList<ArrayList<Double[]>>> dados, int n, int coluna,String titulo, String labelX, String labelY, String filename, boolean yLimitado) throws IOException {
		
		XYSeries[] series = new XYSeries[dados.size()];
		 
		XYSeriesCollection dataset = new XYSeriesCollection();
		 
		 for(int i = 0; i < dados.size(); i++) {
			 series[i] = new XYSeries(dados.get(i).toString());
			 
			 
			 for(int j = 0; j < dados.get(i).size(); j++) {
				 
				 Number epoca = j+1;
				 
				 series[i].add(epoca, dados.get(i).get(j).get(n)[coluna]);
				 
				 
			 }
			 dataset.addSeries(series[i]);
		 } 
		
		JFreeChart chart = ChartFactory.createXYLineChart(
                null, 
                labelX, 
                labelY, 
                dataset, 
                PlotOrientation.VERTICAL,
                true, 
                false, 
                false 
        );
		
		
         
        chart.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF, 0xFF));
        chart.getLegend().setFrame(new BlockBorder());

        XYPlot plot = (XYPlot)chart.getPlot();
        
        plot.setRenderer(new SamplingXYLineRenderer());
        

        plot.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF, 0));
        plot.setOutlinePaint(new Color(0x00, 0x00, 0x00, 0));
        plot.setInsets(new RectangleInsets(0, 1, 0, 0));
        plot.setDomainGridlinePaint(new Color(0x00, 0x00, 0x00, 0x40));
        
        plot.getDomainAxis().setTickLabelFont(new Font("Monospaced", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Monospaced", Font.PLAIN, 12));
        
        plot.setRangeGridlinePaint(new Color(0x00, 0x00, 0x00, 0x40));
        if(yLimitado) {
        	plot.getRangeAxis().setRange(0, 1);	
        }
        

        ChartUtils.saveChartAsPNG(new File(filename + ".png"), chart, 640, 480);
        
        SVGGraphics2D g2 = new SVGGraphics2D(640, 480);
        Rectangle r = new Rectangle(0, 0, 640, 480);
        chart.draw(g2, r);
        
        File f = new File(filename + ".svg");
        SVGUtils.writeToSVG(f, g2.getSVGElement());
	}

}
