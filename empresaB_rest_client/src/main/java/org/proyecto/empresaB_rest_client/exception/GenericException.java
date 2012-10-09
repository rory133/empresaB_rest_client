package org.proyecto.empresaB_rest_client.exception;

public class GenericException extends RuntimeException{
	 
	private String customMsg;
 
	
 
	public GenericException(String customMsg) {
		this.customMsg = customMsg;
	}

	public String getCustomMsg() {
		return customMsg;
	}

	public void setCustomMsg(String customMsg) {
		this.customMsg = customMsg;
	}
 
}
