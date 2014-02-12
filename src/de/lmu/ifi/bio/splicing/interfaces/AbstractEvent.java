package de.lmu.ifi.bio.splicing.interfaces;

/**
 * Created by uhligc on 12.02.14.
 */
public class AbstractEvent {
    String i1, i2;
    long start, stop;
    boolean replace;

    public AbstractEvent(String i1, String i2, long start, long stop, boolean replace) {
        this.i1 = i1;
        this.i2 = i2;
        this.start = start;
        this.stop = stop;
        this.replace = replace;
    }

    public String getI1() {
        return i1;
    }

    public String getI2() {
        return i2;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    public boolean isReplace() {
        return replace;
    }
}
