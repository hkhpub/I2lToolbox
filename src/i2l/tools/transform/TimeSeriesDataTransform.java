package i2l.tools.transform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TimeSeriesDataTransform {

	public static String INPUT_FILE = "D:/data.i2l/kospi.csv";
	public static String SPACE = " ";
	public static String COMMA = ", ";
	public static String OUTPUT_FILE = "D:/workspace.i2l/Dataset/kospi/kospi";
	public static int CSV_FORM = 1;
	public static int LIBSVM_FORM = 2;
	private static int writeForm = LIBSVM_FORM;
//	private static int writeForm = CSV_FORM;
	
	public static void main(String args[]) {
		
		// read file
		List<Kospi> kospiList = new ArrayList<Kospi>();
		BufferedReader br = null;
		File file = new File(INPUT_FILE);
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			Kospi kospi = null;
			while ((line = br.readLine())!=null) {
				String[] tokens = line.split(",");
				if (tokens.length > 1) {
					kospi = new Kospi();
					kospi.date = tokens[0];
					kospi.price = Float.parseFloat(tokens[1]);
					kospiList.add(kospi);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (br != null) { try { br.close(); } catch (Exception ex) {}; }
		}
		
		System.out.println("read kospi total: "+kospiList.size()+" records");
		Collections.reverse(kospiList);
		
		TimeSeriesDataTransform transformer = new TimeSeriesDataTransform();
		List<Kospi> rdp5List = transformer.calculatePriorRDP(kospiList, 5);
		List<Kospi> rdp10List = transformer.calculatePriorRDP(kospiList, 10);
		List<Kospi> rdp15List = transformer.calculatePriorRDP(kospiList, 15);
		List<Kospi> rdp20List = transformer.calculatePriorRDP(kospiList, 20);
		List<Kospi> labelList = transformer.calculatePostLabel(kospiList, 5);
		List<Kospi> emaList = transformer.calculateEMA(kospiList, 100);
		
		// write file
		BufferedWriter bw = null;
		String outFileNm = OUTPUT_FILE;
		if (writeForm == CSV_FORM) {
			outFileNm += ".csv";
		}
		File outfile = new File(outFileNm);
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile)));
			
			if (writeForm == CSV_FORM) {
				// write csv header
				bw.write("ema100, rdp5, rdp10, rdp15, rdp20, label");
				bw.write("\n");
			}
			for (int i=0; i<kospiList.size(); i++) {
				if (i<20)
					continue;
				if (i>=kospiList.size()-5)
					continue;
				if (i<100)
					continue;
				
				if (writeForm == CSV_FORM) {
					bw.write(emaList.get(i).price+COMMA);
					bw.write(rdp5List.get(i).price+COMMA);
					bw.write(rdp10List.get(i).price+COMMA);
					bw.write(rdp15List.get(i).price+COMMA);
					bw.write(rdp20List.get(i).price+COMMA);
					bw.write(labelList.get(i).label);
					bw.write("\n");
					
				} else if (writeForm == LIBSVM_FORM) {
					bw.write(labelList.get(i).price+SPACE);
					bw.write("1:"+emaList.get(i).price+SPACE);
					bw.write("2:"+rdp5List.get(i).price+SPACE);
					bw.write("3:"+rdp10List.get(i).price+SPACE);
					bw.write("4:"+rdp15List.get(i).price+SPACE);
					bw.write("5:"+rdp20List.get(i).price+SPACE);
					bw.write("\n");
					
				} else {
					
				}
			}
			System.out.println("write done!");
			
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) { try { bw.close(); } catch (Exception ex) {}; }
		}
	}
	
	/**
	 * @param rawList
	 * @param fromDays
	 * @param nDays
	 * @return
	 */
	public List<Kospi> calculatePriorRDP(List<Kospi> rawList, int nDays) {
		List<Kospi> rdpList = new ArrayList<Kospi>();
		for (int i=0; i<rawList.size(); i++) {
			if (i<nDays) {
				rdpList.add(new Kospi());
				continue;
			}
			
			Kospi kospi = rawList.get(i-(nDays-5));
			Kospi priorKospi = rawList.get(i-nDays);
			float price = kospi.price;
			float priorPrice = priorKospi.price;
			float rdp = (price-priorPrice)/priorPrice;
			
			Kospi tmp = new Kospi();
			tmp.date = kospi.date;
			tmp.price = rdp;
			rdpList.add(tmp);
		}
		return rdpList;
	}
	
	public List<Kospi> calculateEMA(List<Kospi> rawList, int period) {
		List<Kospi> emaList = new ArrayList<Kospi>();
		float alpha = 0.2f;
		for (int i=0; i<rawList.size(); i++) {
			if (i<period) {
				emaList.add(new Kospi());
				continue;
			}
			
			float ema = 0;
			for (int j=0; j<period; j++) {
				float xt = rawList.get(i-j).price;
				float alpha_exp = (float) Math.pow((1-alpha), j);
				ema += xt * alpha_exp;
			}
			
			ema = ema * alpha;
			
			Kospi tmp = new Kospi();
			tmp.price = ema;
			emaList.add(tmp);
//			System.out.println("ema("+(i+1)+"): "+ema);
		}
		return emaList;
	}
	
	public List<Kospi> calculatePostLabel(List<Kospi> rawList, int nDays) {
		List<Kospi> labelList = new ArrayList<Kospi>();
		int size = rawList.size();
		for (int i=0; i<size; i++) {
			if (i>=size-nDays) {
				labelList.add(new Kospi());
				continue;
			}
			
			Kospi kospi = rawList.get(i);
			Kospi postKospi = rawList.get(i+nDays);
			float price = kospi.price;
			float postPrice = postKospi.price;
			String label = postPrice-price > 0 ? "up" : "down";
			int intLabel = postPrice-price > 0 ? 1 : -1;
			
			Kospi tmp = new Kospi();
			tmp.date = kospi.date;
			tmp.price = intLabel;
			tmp.label = label;
			labelList.add(tmp);
		}
		return labelList;
	}
}
