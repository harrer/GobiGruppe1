package de.lmu.ifi.bio.splicing.interfaces;

import java.util.List;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class Transcript {
    List<Exon> list;
    String transcript_id;
    String protein_id;

    Transcript(String transcript_id, String protein_id) {
        this.transcript_id = transcript_id;
        this.protein_id = protein_id;
    }

    public void addExon(Exon exon) {
        list.add(exon);
    }

    public List<Exon> getExons() {
        return list;
    }

    public String getTranscriptId() {
        return transcript_id;
    }

    public String getProteinId() {
        return protein_id;
    }
}
