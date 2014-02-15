package de.lmu.ifi.bio.splicing.zkoss.entity;

/**
 * Created by uhligc on 13.02.14.
 */
public class PatternEvent {
    private String name, id;
    private int start, stop;

    public PatternEvent(String name, int start, int stop) {
        this.name = name;
        this.start = start;
        this.stop = stop;
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
