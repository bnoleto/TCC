package estatisticas;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
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
	
	private static XYSeriesCollection dataset;
	
	public NoletoGrafico(ArrayList<ArrayList<Double[]>> dados, int coluna){

		 XYSeries[] series = new XYSeries[dados.size()];
		 
		 dataset = new XYSeriesCollection();
		 
		 for(int i = 0; i < dados.size(); i++) {
			 series[i] = new XYSeries(dados.get(i).toString());
			 
			 
			 for(int j = 0; j < dados.get(i).size(); j++) {
				 
				 Number epoca = j+1;
				 series[i].add(epoca, dados.get(i).get(j)[coluna]);
				 
			 }
			 dataset.addSeries(series[i]);
		 } 
	}
	
	public void gerarChart(String titulo, String labelX, String labelY, String filename, boolean yLimitado) throws IOException {
		
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
		
		
         
        chart.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF, 0));
        chart.getLegend().setFrame(new BlockBorder());

        XYPlot plot = (XYPlot)chart.getPlot();
        
        plot.setRenderer(new SamplingXYLineRenderer());

        plot.setBackgroundPaint(new Color(0xFF, 0xFF, 0xFF, 0));
        plot.setOutlinePaint(new Color(0x00, 0x00, 0x00, 0));
        plot.setInsets(new RectangleInsets(0, 1, 0, 0));
        plot.setDomainGridlinePaint(new Color(0x00, 0x00, 0x00, 0x40));
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
