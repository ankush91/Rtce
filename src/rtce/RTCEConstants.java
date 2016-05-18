package rtce;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RTCEConstants {
	
	//The character set to be used for all char and string variables
	private static final Charset RTCECHARSET = StandardCharsets.US_ASCII;
	//The number of characters maximum in a request name
	private static final int requestLength = 8;

	/**
	 * Returns the character set
	 * @return the character set (ASCII)
	 */
	public static Charset getRtcecharset() {
		return RTCECHARSET;
	}

	/**
	 * Returns the length of the request
	 * @return the number of characters in the request (8)
	 */
	public static int getRequestlength() {
		return requestLength;
	}
	
}
