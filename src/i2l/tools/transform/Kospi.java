package i2l.tools.transform;

public class Kospi {

	public String date = "";
	
	public float price = 0f;
	
	/**
	 * rdp-5: rdp before 5 days
	 */
	public float rdp5_pre = 0f;
	
	/**
	 * rdp-10: rdp before 10 days
	 */
	public float rdp10_pre = 0f;
	
	/**
	 * rdp-15: rdp before 15 days
	 */
	public float rdp15_pre = 0f;
	
	/**
	 * rdp-20: rdp before 20 days
	 */
	public float rdp20_pre = 0f;
	
	/**
	 * ema100: price - ema100
	 */
	public float ema100 = 0f;
	
	/**
	 * ema3: ema3
	 */
	public float ema3 = 0f;
	
	/**
	 * rdp+5: future price after 5 days
	 */
	public float rdp5_post = 0f;
}
