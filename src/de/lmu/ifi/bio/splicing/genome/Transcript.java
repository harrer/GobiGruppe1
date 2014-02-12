package de.lmu.ifi.bio.splicing.genome;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.bio.splicing.interfaces.AbstractExon;
import de.lmu.ifi.bio.splicing.interfaces.AbstractTranscript;

public class Transcript extends AbstractTranscript {
	List<Exon> list;
    String transcript_id;
    String protein_id;

    public Transcript(String transcript_id, String protein_id) {
        this.transcript_id = transcript_id;
        this.protein_id = protein_id;
        this.list = new ArrayList<>();
    }

    public void addExon(Exon abstractExon) {
        list.add(abstractExon);
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

    public List<Exon> getCds() {
        return list;
    }

}
