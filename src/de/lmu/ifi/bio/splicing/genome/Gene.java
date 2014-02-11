package de.lmu.ifi.bio.splicing.genome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Gene {
	private String geneId;
	private Integer start;
	private Integer end;
	private String chr;
	private Strand strand;
	private String geneName;
	private List<Transcript> transcripts;
	private HashMap<String,Protein> proteins; 
	
	public void addTranscript(Transcript transcript){
		this.transcripts.add(transcript);
	}
	public String getGeneId() {
		return geneId;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		return end;
	}

	public String getChr() {
		return chr;
	}
	@Override
	public String toString(){
		return geneId;
	}
	
	public void addProtein(Protein protein){
		proteins.put(protein.getProteinId(), protein);
		if (protein.getParentTranscript().getParentGene() == null )
			protein.getParentTranscript().setParentGene(this);
		else if (! protein.getParentTranscript().getParentGene() .equals(this) ){
			throw new RuntimeException("Set protein to different gene not possible");
		}
	}
	
	public Gene( String chr, String geneId, String geneName, Strand strand, Integer start, Integer end) {
		super();
		this.geneName = geneName;
		this.proteins = new HashMap<String,Protein>();
		this.geneId = geneId;
		this.start = start;
		this.end = end;
		this.chr = chr;
		this.strand = strand;
		this.transcripts = new ArrayList<Transcript>();
	}

	public Gene(String geneId) {
		this.geneId = geneId;
	}

	public Strand getStrand() {
		return strand;
	}
	public List<Transcript> getTranscripts() {
		return transcripts;
	}
	public Collection<Protein> getProteins(){
		return proteins.values();
	}
	public Protein getProtein(String protein) {
		return proteins.get(protein);
		
	}
	public String getGeneName() {
		return geneName;
	}
}
