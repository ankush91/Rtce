package rtce;

import java.nio.charset.StandardCharsets;

public class RTCEMessage {

	private RTCEMessageType request;
	private String username;
	private String password;
	private String encryptOpts[];
	private String genericOpts[];

	public RTCEMessageType getRequest() {
		return request;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setRequest(RTCEMessageType request) {
		this.request = request;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String[] getEncryptOpts() {
		return encryptOpts;
	}

	public void setEncryptOpts(String[] encryptOpts) {
		this.encryptOpts = encryptOpts;
	}

	public String[] getGenericOpts() {
		return genericOpts;
	}

	public void setGenericOpts(String[] genericOpts) {
		this.genericOpts = genericOpts;
	}
	
	public void setRequest(byte[] requestChars){
		String requestName = new String(requestChars, RTCEConstants.getRtcecharset());
		request = RTCEMessageType.valueOf(requestName);
	}
	
	public byte[] getRequestChars(){
		byte[] requestName = request.toString().getBytes(RTCEConstants.getRtcecharset());
		return requestName;
	}
}
