package de.lmu.ifi.bio.splicing.interfaces;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class Exon {
    long start, stop;
    int frame;

    Exon(long start, long stop, int frame) {
        this.start = start;
        this.stop = stop;
        this.frame = frame;
    }

    abstract public long getStart();
    abstract public long getStop();
    abstract public long getFrame();
}
