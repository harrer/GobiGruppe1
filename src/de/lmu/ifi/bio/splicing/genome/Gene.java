package de.lmu.ifi.bio.splicing.genome;

import de.lmu.ifi.bio.splicing.interfaces.Search;

import java.util.HashMap;
import java.util.List;

public class Gene implements Search{
    HashMap<String, Transcript> hashmap_transcriptid; //transcript_id
    HashMap<String, Transcript> hashmap_proteinid; //protein_id als key
    String geneId, chromosome;
    boolean strand;

    public Gene(String geneId, String chromosome, boolean strand) {
        this.geneId = geneId;
        this.strand = strand;
        this.chromosome = chromosome;
        hashmap_proteinid = new HashMap<String, Transcript>();
        hashmap_transcriptid = new HashMap<String, Transcript>();
    }


    public void addTranscript(Transcript Transcript) {
        hashmap_transcriptid.put(Transcript.getTranscriptId(), Transcript);
        hashmap_proteinid.put(Transcript.getProteinId(), Transcript);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gene)) return false;

        Gene that = (Gene) o;

        if (strand != that.strand) return false;
        if (!chromosome.equals(that.chromosome)) return false;
        if (!geneId.equals(that.geneId)) return false;
        if (!hashmap_proteinid.equals(that.hashmap_proteinid)) return false;
        if (!hashmap_transcriptid.equals(that.hashmap_transcriptid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = hashmap_transcriptid.hashCode();
        result = 31 * result + hashmap_proteinid.hashCode();
        result = 31 * result + geneId.hashCode();
        result = 31 * result + chromosome.hashCode();
        result = 31 * result + (strand ? 1 : 0);
        return result;
    }

    @Override
    public List<Object> search(String keyword) {
        //TODO implement search through Gene
        return null;
    }
}