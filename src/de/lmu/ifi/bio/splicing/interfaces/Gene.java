package de.lmu.ifi.bio.splicing.interfaces;

import java.util.HashMap;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class Gene {
    HashMap<String, Transcript> hashmap_transcriptid; //transcript_id
    HashMap<String, Transcript> hashmap_proteinid; //protein_id als key
    String geneId, chromosome;
    boolean strand;

    Gene(String geneId, boolean strand, String chromosome) {
        this.geneId = geneId;
        this.strand = strand;
        this.chromosome = chromosome;
        hashmap_proteinid = new HashMap<String, Transcript>();
        hashmap_transcriptid = new HashMap<String, Transcript>();
    }

    public void addTranscript(Transcript transcript) {
        hashmap_transcriptid.put(transcript.getTranscriptId(),transcript);
        hashmap_proteinid.put(transcript.getProteinId(),transcript);
    }

    public Transcript getTranscriptByTranscriptId(String transcript_id) {
        return hashmap_transcriptid.get(transcript_id);
    }

    public Transcript getTranscriptByProteinId(String protein_id) {
        return hashmap_proteinid.get(protein_id);
    }
    public String getGeneId() {
        return geneId;
    }

    public boolean getStrand() {
        return strand;
    }

    public String getChromosome() {
        return chromosome;
    }

    public HashMap<String, Transcript> getHashmap_proteinid() {
        return hashmap_proteinid;
    }

    public HashMap<String, Transcript> getHashmap_transcriptid() {
        return hashmap_transcriptid;
    }
}
