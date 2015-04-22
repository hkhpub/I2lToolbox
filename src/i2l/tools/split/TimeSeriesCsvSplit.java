package i2l.tools.split;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class TimeSeriesCsvSplit extends TimeSeriesFileSplit {
	
	private String extName = ".csv";
	private String m_csvHeaderLine = "";
	
	@Override
	protected String getExtName(){
		return extName;
	}
	
	@Override
	protected String getHeaderLine(){
		return m_csvHeaderLine;
	}
	
	@Override
	public int readDataCount(String filename) {
		FileReader fr = null;
		BufferedReader br = null;
		int nLines = 0;
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
			
			m_csvHeaderLine =br.readLine();
			while (br.readLine()!=null) {
				nLines++;
			}
		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (br!=null)
					br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				if (br!=null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return nLines;
	}
	
	public static void main(String args[]) {
		Configuration config = null;
		Parameters params = new Parameters();
		FileBasedConfigurationBuilder<FileBasedConfiguration> builder = 
				new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.properties()
						.setFileName("conf/split.properties"));
		
		try {
			config = builder.getConfiguration();
		} catch (ConfigurationException cex) {
		}
		
		if (config == null) {
			System.err.println("Exception with configurations!");
		}
		int k = config.getInt("k");
		int train_set = config.getInt("train_set");
		int validation_set = config.getInt("validation_set");
		int test_set = config.getInt("test_set");
		int sets[] = {train_set, validation_set, test_set};
		
		String input = config.getString("input.file.csv");
		String output = config.getString("output.path");
		
		TimeSeriesCsvSplit splitter = new TimeSeriesCsvSplit();
		
		int totalCount = splitter.readDataCount(input);
		splitter.setTotalCount(totalCount);
		splitter.setOneFoldCount(sets[0]+sets[1]+sets[2]);
		splitter.setSubCount(k);
		
		splitter.generateFiles_timeSeries(input, output, k, sets);
		
		System.out.println("split csv done !");
	}
}
