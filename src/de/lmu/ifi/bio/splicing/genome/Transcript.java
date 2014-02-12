package de.lmu.ifi.bio.splicing.genome;

import java.util.ArrayList;
import java.util.List;

public class Transcript {
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

}
