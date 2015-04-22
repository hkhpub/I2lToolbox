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

public class DataSplit {

	int totalCount = 0;
	int subCount = 0;
	
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
	
	/**
	 * 일반 k-fold separating
	 * @param input
	 * @param output
	 * @param k
	 */
	public void generateFiles_kfold(String input, String output, int k) {
		String line = "";
		FileReader fr = null;
		BufferedReader br = null;
		FileWriter fw1 = null;
		FileWriter fw2 = null;
		BufferedWriter bw1 = null;
		BufferedWriter bw2 = null;
		
		for (int i=0; i<k; i++) {
			int current = 0;
			int trainFrom = i * subCount;
			int trainTo = (i+1) * subCount;
			String trainFileNm = output+"/train_"+(i+1);
			String testFileNm = output+"/test_"+(i+1);
			
			try {
				fr = new FileReader(input);
				br = new BufferedReader(fr);
				
				fw1 = new FileWriter(trainFileNm);
				bw1 = new BufferedWriter(fw1);
				
				fw2 = new FileWriter(testFileNm);
				bw2 = new BufferedWriter(fw2);
				
				while ((line=br.readLine()) != null) {
					if (current >= trainFrom && current < trainTo) {
						// write to test file
						bw2.write(line+"\n");
					} else {
						// write to train file
						bw1.write(line+"\n");
					}
					current++;
				}
				// flush buffered writer
				bw1.flush();
				bw2.flush();
				
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
		
		String input = config.getString("input.file");
		String output = config.getString("output.path");
		
		DataSplit seperator = new DataSplit();
		 
		seperator.totalCount = seperator.readDataCount(input);
		seperator.subCount = seperator.totalCount / k;
		seperator.generateFiles_kfold(input, output, k);
	}
	
}
