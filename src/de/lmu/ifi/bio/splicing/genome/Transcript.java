package de.lmu.ifi.bio.splicing.genome;

import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.bio.splicing.interfaces.AbstractTranscript;

public class Transcript extends AbstractTranscript {
    private String transcriptId, proteinId;
    private List<Exon> cds;

    public Transcript(String transcriptId, String proteinId) {
        super(transcriptId, proteinId);
    }

    public void addCds(Exon e) {
        cds.add(e);
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public String getProteinId() {
        return proteinId;
    }

    public List<Exon> getCds() {
        return cds;
    }

}
