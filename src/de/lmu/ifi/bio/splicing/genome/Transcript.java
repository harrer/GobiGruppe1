package de.lmu.ifi.bio.splicing.genome;

import java.util.ArrayList;
import java.util.List;

public class Transcript {

	private Gene parentGene=null;
	private String transcriptType = null;
	private String transcriptName=null;
	private String transcriptId = null;
	private Protein protein = null;
	private List<Exon> exons = null;

	public String getTranscriptType() {
		return transcriptType;
	}

	public String getTranscriptName() {
		return transcriptName;
	}

	public void setParentGene(Gene parentGene) {
		this.parentGene = parentGene;
	}

	public void addExon(Exon exon){
		this.exons.add(exon);
	}
	
	public Gene getParentGene() {
		return parentGene;
	}
	public void setProtein(Protein protein){
		this.protein = protein;
		this.getParentGene().addProtein(protein);
	}
	public String getTranscriptId() {
		return transcriptId;
	}

	public Transcript(Gene parentGene, String transcriptId, String transcriptName, String transcriptType) {
		super();
		this.parentGene = parentGene;
		this.transcriptId = transcriptId;
		this.transcriptName = transcriptName;
		this.exons = new ArrayList<Exon>();
		this.transcriptType = transcriptType;
	}

	public Protein getProtein() {
		return protein;
	}

	public List<Exon> getExons() {
		return exons;
	}
	@Override
	public String toString(){
		return transcriptId;
	}
}
