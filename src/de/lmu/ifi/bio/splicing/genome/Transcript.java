package de.lmu.ifi.bio.splicing.genome;

import java.util.LinkedList;
import java.util.List;

public class Transcript {
	private String transcriptId, proteinId;
	private List<Exon> cds;

	public Transcript(String transcriptId, String proteinId) {
		this.transcriptId = transcriptId;
		this.proteinId = proteinId;
		this.cds = new LinkedList<Exon>();

	}

	public void addCds(Exon e) {
		cds.add(e);
	}

	public String getTranscriptId() {
		return transcriptId;
	}

	public String getProteinId() {
		return proteinId;
	}

	public List<Exon> getCds() {
		return cds;
	}

}
