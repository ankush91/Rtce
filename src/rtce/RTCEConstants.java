package rtce;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RTCEConstants {
	private static final Charset RTCECHARSET = StandardCharsets.US_ASCII;

	public static Charset getRtcecharset() {
		return RTCECHARSET;
	}
	
	
}
