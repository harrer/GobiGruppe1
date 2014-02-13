package de.lmu.ifi.bio.splicing.genome;

import de.lmu.ifi.bio.splicing.interfaces.Search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transcript implements Search {
    List<Exon> cds;
    String transcript_id;
    String protein_id;

    public Transcript(String transcript_id, String protein_id) {
        this.transcript_id = transcript_id;
        this.protein_id = protein_id;
        this.cds = new ArrayList<>();
    }

    public void addExon(Exon abstractExon) {
        cds.add(abstractExon);
    }

    public String getTranscriptId() {
        return transcript_id;
    }

    public String getProteinId() {
        return protein_id;
    }

    public List<Exon> getCds() {
        return cds;
    }

    @Override
    public List<String> search(String keyword) {
        if (transcript_id.contains(keyword) || protein_id.contains(keyword))
            return Arrays.asList(transcript_id, protein_id);
        return Arrays.asList();
    }
}
