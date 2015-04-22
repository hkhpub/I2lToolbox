package i2l.tools.split;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class TimeSeriesFileSplit {

	private int totalCount = 0;
	private int subCount = 0;
	private int oneFoldCount = 0;
	
	protected String getExtName(){
		return "";
	}
	
	protected String getHeaderLine(){
		return null;
	}
	
	protected void setTotalCount(int totalCount){
		this.totalCount = totalCount;
	}
	
	protected void setOneFoldCount(int oneFoldCount){
		this.oneFoldCount = oneFoldCount;
	}
	
	protected void setSubCount(int k){
		if (k==1){
			subCount = 0;
		} else {
			subCount = oneFoldCount - (oneFoldCount * k - totalCount) / (k-1);
		}
	}
	
	public int readDataCount(String filename) {
		FileReader fr = null;
		BufferedReader br = null;
		int nLines = 0;
		try {
			fr = new FileReader(filename);
			br = new BufferedReader(fr);
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
	
	public void generateFiles_timeSeries(String input, String output, int k, int sets[]) {
		String line = "";
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw1 = null;
		FileWriter fw2 = null;
		FileWriter fw3 = null;
		BufferedWriter bw1 = null;
		BufferedWriter bw2 = null;
		BufferedWriter bw3 = null;
		
		String ext = getExtName();
		
		for (int i=0; i<k; i++) {
			int current = 0;
			int from = i * subCount;
			String trainNm = output+"/train_"+(i+1)+ext;
			String validationNm = output+"/validation_"+(i+1)+ext;
			String testNm = output+"/test_"+(i+1)+ext;
			
			try {
				fr = new FileReader(input);
				br = new BufferedReader(fr);
				
				fw1 = new FileWriter(trainNm);
				bw1 = new BufferedWriter(fw1);
				
				fw2 = new FileWriter(validationNm);
				bw2 = new BufferedWriter(fw2);
				
				fw3 = new FileWriter(testNm);
				bw3 = new BufferedWriter(fw3);
				
				// write header line
				if (getHeaderLine()!=null){
					// skip first line
					br.readLine();
					bw1.write(getHeaderLine()+"\n");
					bw2.write(getHeaderLine()+"\n");
					bw3.write(getHeaderLine()+"\n");
				}
				
				while ((line=br.readLine())!=null) {
					if (current >= from && current < from + sets[0]) {
						// write to train file
						bw1.write(line+"\n");
						
					} else if (current >= from && current < from + sets[0] + sets[1]) {
						// write to validation file
						bw2.write(line+"\n");
						
					} else if (current >= from && current < from + sets[0] + sets[1] + sets[2]) {
						// write to test file
						bw3.write(line+"\n");
						
					} else {
					}
					current++;
				}
				// flush buffered writer
				bw1.flush();
				bw2.flush();
				bw3.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					fr.close();
					br.close();
					fw1.close();
					bw1.close();
					fw2.close();
					bw2.close();
					fw3.close();
					bw3.close();
				} catch (Exception e) {}
			}
		}
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
		
		String input = config.getString("input.file");
		String output = config.getString("output.path");
		
		TimeSeriesFileSplit splitter = new TimeSeriesFileSplit();
		
		int totalCount = splitter.readDataCount(input);
		splitter.setTotalCount(totalCount);
		splitter.setOneFoldCount(sets[0]+sets[1]+sets[2]);
		splitter.setSubCount(k);
		
		splitter.generateFiles_timeSeries(input, output, k, sets);
		
		System.out.println("done !");
		
		System.out.println("split file done !");
	}
}
