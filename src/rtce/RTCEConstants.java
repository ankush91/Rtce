package rtce;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RTCEConstants {
	private static final Charset RTCECHARSET = StandardCharsets.US_ASCII;
	private static final int requestLength = 8;

	public static Charset getRtcecharset() {
		return RTCECHARSET;
	}

	public static int getRequestlength() {
		return requestLength;
	}
	
}
