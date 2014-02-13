package de.lmu.ifi.bio.splicing.zkoss;

/**
 * Created by uhligc on 13.02.14.
 */
public class Pattern {
    private String name;
    private int start, stop;

    public Pattern(String name, int start, int stop) {
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
}
