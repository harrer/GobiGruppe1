package de.lmu.ifi.bio.splicing.genome;
import java.util.HashMap;
import de.lmu.ifi.bio.splicing.interfaces.AbstractGene;

public class Gene extends AbstractGene{
	private HashMap<String, Transcript> transcripts;
	private HashMap<String, Transcript> protIdTranscripts;
	private String geneId, seqName;
	private boolean strand;
	

	
	public Gene(String geneId,
			String seqName, boolean strand) {
        super(geneId, strand, seqName);
    }

	public Transcript getTranscript(String transcriptId){
		if(transcripts.containsKey(transcriptId))
			return transcripts.get(transcriptId);
		else
			return protIdTranscripts.get(transcriptId);
	}
	
	public String getGeneId(){
		return geneId;
	}
	
	public void addTranscript(Transcript t){
		transcripts.put(t.getTranscriptId(), t);
		protIdTranscripts.put(t.getProteinId(), t);
	}
	
	public String getSeqName(){
		return seqName;
	}
	
	public boolean getStrand(){
		return strand;
	}
	
	public HashMap<String, Transcript> getTranscripts(){
		return transcripts;
	}
}