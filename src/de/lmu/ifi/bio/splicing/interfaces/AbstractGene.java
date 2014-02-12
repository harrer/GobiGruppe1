package de.lmu.ifi.bio.splicing.interfaces;

import java.util.HashMap;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class AbstractGene {
    HashMap<String, AbstractTranscript> hashmap_transcriptid; //transcript_id
    HashMap<String, AbstractTranscript> hashmap_proteinid; //protein_id als key
    String geneId, chromosome;
    boolean strand;

    protected AbstractGene(String geneId, boolean strand, String chromosome) {
        this.geneId = geneId;
        this.strand = strand;
        this.chromosome = chromosome;
        hashmap_proteinid = new HashMap<String, AbstractTranscript>();
        hashmap_transcriptid = new HashMap<String, AbstractTranscript>();
    }

    public void addTranscript(AbstractTranscript abstractTranscript) {
        hashmap_transcriptid.put(abstractTranscript.getTranscriptId(), abstractTranscript);
        hashmap_proteinid.put(abstractTranscript.getProteinId(), abstractTranscript);
    }

    public AbstractTranscript getTranscriptByTranscriptId(String transcript_id) {
        return hashmap_transcriptid.get(transcript_id);
    }

    public AbstractTranscript getTranscriptByProteinId(String protein_id) {
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

    public HashMap<String, AbstractTranscript> getHashmap_proteinid() {
        return hashmap_proteinid;
    }

    public HashMap<String, AbstractTranscript> getHashmap_transcriptid() {
        return hashmap_transcriptid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractGene)) return false;

        AbstractGene that = (AbstractGene) o;

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
}
