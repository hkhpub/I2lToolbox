package i2l.tools.generate;

import i2l.util.FileUtil;

public class CsvWriter {

	public static void main(String args[]) {

		String path = "D:/workspace.opencv/data/at";
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<40; i++) {
			for (int j=0; j<10; j++) {
				sb.append(String.format("%s/s%d/%d.pgm;%d", path, (i+1), (j+1), i)+"\n");
				
			}
		}
		
		FileUtil.WriteFile("at.txt", sb.toString());
	}
}
