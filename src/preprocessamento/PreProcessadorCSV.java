package preprocessamento;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.writable.Writable;
import org.datavec.spark.transform.SparkTransformExecutor;
import org.datavec.spark.transform.misc.StringToWritablesFunction;
import org.datavec.spark.transform.misc.WritablesToStringFunction;

public class PreProcessadorCSV {

	private String pasta;
	private String arquivo;
	
	public PreProcessadorCSV(String pasta, String arquivo){
		this.pasta = pasta;
		this.arquivo = arquivo;
	}
	
	
	
	public void processar() {
		
		 System.setProperty("hadoop.home.dir", "C:\\winutils\\");
	                
	        String inputPath = pasta + arquivo;
	        String outputPath = pasta + "processado\\" + arquivo; 
	        
	        Schema inputDataSchema = new Schema.Builder()
	        		.addColumnsDouble("data","precipitacao","temperatura_max","umidade")
	        		.addColumnCategorical("ocorreu_queimada", "false", "true")
	        		.addColumnDouble("risco_fogo")
	        		.build();
	        
	        TransformProcess tp = new TransformProcess.Builder(inputDataSchema)
	        		.removeColumns("data","risco_fogo")
	        		.categoricalToInteger("ocorreu_queimada")
	        		.build();
	        
	        
	        int numActions = tp.getActionList().size();
	        
	        
	        for(int i = 0; i< numActions; i++) {
	        	System.out.println("\n\n =====================");
	        	System.out.println(" === Schema após o passo " + i + "(" + tp.getActionList().get(i) + ") ===");
	        	System.out.println(tp.getSchemaAfterStep(i));
	        }
	        
	        SparkConf sparkConf = new SparkConf();
	        sparkConf.setMaster("local[*]");
	        sparkConf.setAppName("TCC RR Transform");
	        
	        JavaSparkContext sc = new JavaSparkContext(sparkConf);
	        
	        JavaRDD<String> lines = sc.textFile(inputPath).mapPartitionsWithIndex((index, iter) -> {
	                if (index == 0 && iter.hasNext()) {
	                    iter.next();
	                }
				     return iter;
	        	}, true);
	        
	        sc.close();

	        
	        // convert to Writable
	        JavaRDD<List<Writable>> registro_queimadas = lines.map(new StringToWritablesFunction(new CSVRecordReader()));
	        // run our transform process
	        JavaRDD<List<Writable>> processed = SparkTransformExecutor.execute(registro_queimadas,tp);
	        // convert Writable back to string for export
	        JavaRDD<String> toSave = processed.map(new WritablesToStringFunction(","));

	        toSave.saveAsTextFile(outputPath);
	}
	
}
