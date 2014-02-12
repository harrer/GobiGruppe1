package de.lmu.ifi.bio.splicing.interfaces;

import de.lmu.ifi.bio.splicing.genome.Transcript;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class AbstractEvent {
    //i1 und i2 sind transcript ids (keine protein ids)
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

    public abstract Transcript getTranscriptI1();
    public abstract Transcript getTranscriptI2();
}
