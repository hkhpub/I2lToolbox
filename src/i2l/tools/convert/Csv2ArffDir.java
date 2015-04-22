package i2l.tools.convert;

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class Csv2ArffDir {
	
	/**
	 * takes 2 arguments: - CSV input file - ARFF output file
	 */
	public static void main(String[] args) throws Exception {
		Configuration config = null;
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = 
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName("conf/convert.properties"));
		
		try {
			config = builder.getConfiguration();
		} catch (ConfigurationException cex) {
		}
		if (config == null) {
			System.err.println("Exception with configurations!");
		}
		
		// load CSV
		CSVLoader loader = new CSVLoader();
		ArffSaver saver = new ArffSaver();
		
		String inputPath = config.getString("input.path.csv");
		String outputPath = config.getString("output.path.arff");
		File[] files = new File(inputPath).listFiles();
		
		for (File file : files) {
			if (file.isFile()) {
				// read csv
				loader.setSource(file);
				Instances data = loader.getDataSet();
				
				// save arff
				String outputFileNm = outputPath+"/"+removeExt(file.getName())+".arff";
				saver.setInstances(data);
				saver.setFile(new File(outputFileNm));
				saver.writeBatch();
			}
		}
		
		System.out.println("Convert finish...");
	}
	
	private static String removeExt(String fileName){
		return fileName.substring(0, fileName.indexOf("."));
	}
}
