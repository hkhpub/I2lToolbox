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


public class Classification_Transform {

	public static String INPUT_FILE = "D:/data.i2l/kospi.csv";
	public static String SPACE = " ";
	public static String COMMA = ", ";
	public static String OUTPUT_FILE = "D:/github.i2l/Dataset/kospi/kospi";
	public static int CSV_FORM = 1;
	public static int LIBSVM_FORM = 2;
//	private static int writeForm = LIBSVM_FORM;
	private static int writeForm = CSV_FORM;
	
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
		
		Classification_Transform transformer = new Classification_Transform();
		List<Kospi> rdp5List = transformer.calculatePriorRDP(kospiList, 5);
		List<Kospi> rdp10List = transformer.calculatePriorRDP(kospiList, 10);
		List<Kospi> rdp15List = transformer.calculatePriorRDP(kospiList, 15);
		List<Kospi> rdp20List = transformer.calculatePriorRDP(kospiList, 20);
		List<Kospi> ema100List = transformer.calculateEMA100(kospiList);
		List<Kospi> rdpPost5List = transformer.calculatePostRDP(kospiList, 5);
		List<String> directionList = transformer.getRDP5Direction(rdpPost5List);
		
		System.out.println(String.format("%d, %d, %d, %d, %d, %d, %d", rdp5List.size(), rdp10List.size(), rdp15List.size(),
				rdp20List.size(), ema100List.size(), rdpPost5List.size(), directionList.size()));
		
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
					bw.write(ema100List.get(i).ema100+COMMA);
					bw.write(rdp5List.get(i).rdp5_pre+COMMA);
					bw.write(rdp10List.get(i).rdp10_pre+COMMA);
					bw.write(rdp15List.get(i).rdp15_pre+COMMA);
					bw.write(rdp20List.get(i).rdp20_pre+COMMA);
					bw.write(directionList.get(i));
					bw.write("\n");
					
				} else if (writeForm == LIBSVM_FORM) {
					bw.write(directionList.get(i)+SPACE);
					bw.write("1:"+ema100List.get(i).ema100+SPACE);
					bw.write("2:"+rdp5List.get(i).rdp5_pre+SPACE);
					bw.write("3:"+rdp10List.get(i).rdp10_pre+SPACE);
					bw.write("4:"+rdp15List.get(i).rdp15_pre+SPACE);
					bw.write("5:"+rdp20List.get(i).rdp20_pre+SPACE);
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
			
			Kospi base = rawList.get(i-(nDays-5));
			Kospi prior = rawList.get(i-nDays);
			float rdp_prior_xdays = (base.price-prior.price)/prior.price * 100;
			
			Kospi kospi = new Kospi();
			kospi.date = base.date;
			
			// assign each, rdp5, rdp10, rdp15, rdp20
			if (nDays == 5) {
				kospi.rdp5_pre = rdp_prior_xdays;
				
			} else if (nDays == 10) {
				kospi.rdp10_pre = rdp_prior_xdays;
				
			} else if (nDays == 15) {
				kospi.rdp15_pre = rdp_prior_xdays;
				
			} else if (nDays == 20) {
				kospi.rdp20_pre = rdp_prior_xdays;
				
			}
			rdpList.add(kospi);
		}
		return rdpList;
	}
	
	/**
	 * EMA100 계산 - P(i)- EMA100(i) 100-days exponential moving average of i-th day
	 * http://en.wikipedia.org/wiki/Moving_average
	 * @param rawList
	 * @param period
	 * @return
	 */
	public List<Kospi> calculateEMA100(List<Kospi> rawList) {
		List<Kospi> ema100List = calculateEMA(rawList, 100);
		for (Kospi kospi : ema100List) {
			kospi.ema100 = kospi.price - kospi.ema100;
		}
		return ema100List;
	}
	
	public List<Kospi> calculateEMA(List<Kospi> rawList, int period) {
		List<Kospi> emaList = new ArrayList<Kospi>();
		float alpha = 0.2f;
		for (int i=0; i<rawList.size(); i++) {
			// ema100 을 계산하기 때문에, 100일전 price가 있는 시점부터 ema100을 구한다.
			if (i<period) {
				emaList.add(new Kospi());
				continue;
			}
			
			float ema100 = 0;
			for (int j=0; j<period; j++) {
				// xt = price(i-j): 100-0, 100-1, ... , 100-99.
				float xt = rawList.get(i-j).price;
				// alpha_exp = (1-alpha)^0, ... (1-alpha)^99
				float alpha_exp = (float) Math.pow((1-alpha), j);
				ema100 += xt * alpha_exp;
			}
			ema100 = ema100 * alpha;
			
			Kospi kospi = new Kospi();
			kospi.price = rawList.get(i).price;
			kospi.ema100 = ema100;
			emaList.add(kospi);
		}
		return emaList;
	}
	
	public List<Kospi> calculatePostRDP(List<Kospi> rawList, int nDays) {
		// calculate ema3
		List<Kospi> ema3List = calculateEMA(rawList, 3);
		List<Kospi> rdpPost5List = new ArrayList<Kospi>();
		int size = ema3List.size();
		
		for (int i=0; i<size; i++) {
			// cut-off last 5 days
			if (i>=size-nDays) {
				rdpPost5List.add(new Kospi());
				continue;
			}
			
			Kospi base = ema3List.get(i);
			Kospi post = ema3List.get(i+nDays);
			float rdp_post_xdays = (post.price - base.price) / base.price * 100;
			
			Kospi kospi = new Kospi();
			kospi.date = base.date;
			kospi.rdp5_post = rdp_post_xdays;
			rdpPost5List.add(kospi);
		}
		return rdpPost5List;
	}
	
	/**
	 * RDP+5 의 up, down 방향을 리턴
	 * @param postRdp5List
	 * @return
	 */
	public List<String> getRDP5Direction(List<Kospi> postRdp5List) {
		List<String> directions = new ArrayList<String>();
		for (int i=0; i<postRdp5List.size(); i++) {
			if (postRdp5List.get(i).rdp5_post >= 0) {
				if (writeForm == LIBSVM_FORM) {
					directions.add("1");
				} else {
					directions.add("up");
				}
			} else {
				if (writeForm == LIBSVM_FORM) {
					directions.add("0");
				} else {
					directions.add("down");
				}
			}
		}
		return directions;
	}
}
