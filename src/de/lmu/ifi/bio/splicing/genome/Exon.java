package de.lmu.ifi.bio.splicing.genome;

public class Exon {
	private long startPos, endPos;
	private int frame;
	
	
	public Exon(long startPos, long endPos, int frame) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.frame = frame;
	}
	public long getStartPos() {
		return startPos;
	}
	public long getEndPos() {
		return endPos;
	}
	public int getFrame() {
		return frame;
	}
}
