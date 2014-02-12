package de.lmu.ifi.bio.splicing.interfaces;

import java.util.ArrayList;
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
        this.list = new ArrayList<>();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractTranscript)) return false;

        AbstractTranscript that = (AbstractTranscript) o;

        if (list != null ? !list.equals(that.list) : that.list != null) return false;
        if (!protein_id.equals(that.protein_id)) return false;
        if (!transcript_id.equals(that.transcript_id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = list != null ? list.hashCode() : 0;
        result = 31 * result + transcript_id.hashCode();
        result = 31 * result + protein_id.hashCode();
        return result;
    }
}
