package de.lmu.ifi.bio.splicing.interfaces;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class AbstractExon {
    long start, stop;
    int frame;

    protected AbstractExon(long start, long stop, int frame) {
        this.start = start;
        this.stop = stop;
        this.frame = frame;
    }

    public long getStart() {
        return start;
    }

    public long getStop() {
        return stop;
    }

    public long getFrame() {
        return frame;
    }
}
