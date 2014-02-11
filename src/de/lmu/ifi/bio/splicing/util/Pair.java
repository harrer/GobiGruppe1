package de.lmu.ifi.bio.splicing.util;


public class Pair<FIRST,SECOND> {
	public FIRST first = null;
	public SECOND second = null;
	public Pair(FIRST first, SECOND second){
		this.first = first;
		this.second = second;
	}
	public String toString(){
		return String.format("(%s,%s)",first.toString(),second.toString());
	}
}
