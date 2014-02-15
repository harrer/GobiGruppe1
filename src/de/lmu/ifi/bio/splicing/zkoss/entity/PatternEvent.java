package de.lmu.ifi.bio.splicing.zkoss.entity;

/**
 * Created by uhligc on 13.02.14.
 */
public class PatternEvent {
    private String name, id;
    private int start, stop;
    private boolean is_total; //whether pattern is completetly inside in splicevent or not.

    public PatternEvent(String name, int start, int stop) {
        this.name = name;
        this.start = start;
        this.stop = stop;
    }

    public void setIs_total(boolean is_total) {
        this.is_total = is_total;
    }

    public boolean isIs_total() {

        return is_total;
    }

    public String getName() {
        return name;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public String getId() {
        return id;
    }
}
