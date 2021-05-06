package it.polito.tdp.extflightdelays.model;

public class Rotta {
	private Airport a1;
	private Airport a2;
	private int n;
	
	public Rotta(Airport a1, Airport a2, int n) {
		this.a1 = a1;
		this.a2 = a2;
		this.n = n;
	}
	public Airport getA1() {
		return a1;
	}
	public void setA1(Airport a1) {
		this.a1 = a1;
	}
	public Airport getA2() {
		return a2;
	}
	public void setA2(Airport a2) {
		this.a2 = a2;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	
}
