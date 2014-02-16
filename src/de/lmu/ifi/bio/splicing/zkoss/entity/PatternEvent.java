package de.lmu.ifi.bio.splicing.zkoss.entity;

/**
 * Created by uhligc on 13.02.14.
 */
public class PatternEvent {
    private String id, transcriptid;
    private int start, stop;
    private boolean is_total; //whether pattern is completetly inside in splicevent or not.

    public PatternEvent(String id, String transcriptid, int start, int stop) {
        this.id = id;
        this.transcriptid = transcriptid;
        this.start = start;
        this.stop = stop;
    }

    public String getTranscriptid() {
        return transcriptid;
    }

    public void setIs_total(boolean is_total) {
        this.is_total = is_total;
    }

    public boolean isIs_total() {

        return is_total;
    }

    public String getId() {
        return id;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

}
