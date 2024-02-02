package com.alexanderfoerster.commons;

public class ReadExcelError extends Exception {
	private String fehlerMeldung = "";
	
	public ReadExcelError(String meldung) {
		this.fehlerMeldung = meldung;
	}
	
	public String getFehlerMeldung() {
		return fehlerMeldung;
	}
}