package i2l.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class LogUtil {

	/**
	 * validation set으로 optimal parameter를 선택한 후 test set으로 실험한 accuracy
	 * @param optimal_acc_file
	 * @param optimalAccs
	 */
	public static void logOptimalAccuracy(String optimal_acc_file, double[] optimalAccs, double gamma, int param_k) {
		File file = new File(optimal_acc_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			bw.write("num of clusters: "+param_k+"\n");
			bw.write("gamma: "+gamma+"\n");
			for (int i=0; i<optimalAccs.length; i++) {
				bw.write("[Data set "+(i+1)+"] Accuracy: "+String.format("%.4f", optimalAccs[i])+"\n");
				bw.flush();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
	
	/**
	 * StringBuffer logger에 있는 내용을 파일에 쓴다.
	 * @param optimal_acc_file
	 * @param logger
	 */
	public static void log2File(String optimal_acc_file, StringBuffer logger) {
		File file = new File(optimal_acc_file);
		BufferedWriter bw = null;
		try {
			// append mode = true
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			bw.write(logger.toString());
			bw.flush();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { bw.close(); } catch (Exception ex) {};
		}
	}
}
