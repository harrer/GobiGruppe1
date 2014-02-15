package de.lmu.ifi.bio.splicing.zkoss.entity;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.SecondaryStructure;

/**
 * Created by uhligc on 13.02.14.
 */
public class EventDisplay extends Event{
    private double acc;
    private PatternEvent patternEvent;
    private SecondaryStructure sec;

    public EventDisplay(String i1, String i2, long start, long stop, char type, double acc, PatternEvent patternEvent, SecondaryStructure sec) {
        super(i1, i2, start, stop, type);
        this.acc = acc;
        this.patternEvent = patternEvent;
        this.sec = sec;
    }

    public double getAcc() {
        return acc;
    }

    public PatternEvent getPatternEvent() {
        return patternEvent;
    }

    public SecondaryStructure getSec() {
        return sec;
    }
}
