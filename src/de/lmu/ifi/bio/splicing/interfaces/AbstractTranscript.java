package de.lmu.ifi.bio.splicing.interfaces;

import java.util.List;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class AbstractTranscript {
    List<AbstractExon> list;
    String transcript_id;
    String protein_id;

    protected AbstractTranscript(String transcript_id, String protein_id) {
        this.transcript_id = transcript_id;
        this.protein_id = protein_id;
    }

    public void addExon(AbstractExon abstractExon) {
        list.add(abstractExon);
    }

    public List<AbstractExon> getExons() {
        return list;
    }

    public String getTranscriptId() {
        return transcript_id;
    }

    public String getProteinId() {
        return protein_id;
    }
}
