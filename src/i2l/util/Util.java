package i2l.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Util {
	
	public static int getMinimumIndex(double[] array) {
		int ret = 0;
		double minimum = Float.MAX_VALUE;
		for (int i=0; i<array.length; i++) {
			if (array[i] <= minimum) {
				minimum = array[i];
				ret = i;
			}
		}
		return ret;
	}
	
	public static int getMaximumIndex(double[] array) {
		int ret = 0;
		double maximum = 0f;
		for (int i=0; i<array.length; i++) {
			if (array[i] >= maximum) {
				maximum = array[i];
				ret = i;
			}
		}
		return ret;
	}
	
	public static int getMinimumIndex(List<Double> list) {
		int ret = 0;
		double minimum = Float.MAX_VALUE;
		for (int i=0; i<list.size(); i++) {
			if (list.get(i) <= minimum) {
				minimum = list.get(i);
				ret = i;
			}
		}
		return ret;
	}
	
	public static int getMaximumIndex(List<Double> list) {
		int ret = 0;
		double maximum = 0f;
		for (int i=0; i<list.size(); i++) {
			if (list.get(i) >= maximum) {
				maximum = list.get(i);
				ret = i;
			}
		}
		return ret;
	}
	
	/**
	 * get second minimum index, exclude the parameter index
	 * @param list
	 * @param excludeIndex
	 * @return
	 */
	public static int getMinimumIndex(List<Double> list, int excludeIndex) {
		int ret = 0;
		double minimum = Float.MAX_VALUE;
		for (int i=0; i<list.size(); i++) {
			if (i == excludeIndex)
				continue;
			
			if (list.get(i) <= minimum) {
				minimum = list.get(i);
				ret = i;
			}
		}
		return ret;
	}
	
	/**
	 * get second maximum index, exclude the parameter index
	 * @param list
	 * @param excludeIndex
	 * @return
	 */
	public static int getMaximumIndex(List<Double> list, int excludeIndex) {
		int ret = 0;
		double maximum = 0f;
		for (int i=0; i<list.size(); i++) {
			if (i == excludeIndex)
				continue;
			
			if (list.get(i) >= maximum) {
				maximum = list.get(i);
				ret = i;
			}
		}
		return ret;
	}
	
	public static String getTimeTag() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(System.currentTimeMillis()));
		String year = String.format("%4d", calendar.get(Calendar.YEAR));
		String month = String.format("%02d", calendar.get(Calendar.MONTH)+1);
		
		String date = String.format("%02d", calendar.get(Calendar.DATE));
		String hour = String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d", calendar.get(Calendar.MINUTE));
		String second = String.format("%02d", calendar.get(Calendar.SECOND));
		
		return year+month+date+"-"+hour+minute+second;
	}
	
	public static void info(String s) {
		System.out.print(s);
		System.out.flush();
	}
}
