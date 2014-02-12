package de.lmu.ifi.bio.splicing.genome;

import de.lmu.ifi.bio.splicing.interfaces.AbstractEvent;

/**
 * Created by uhligc on 12.02.14.
 */
public class Event extends AbstractEvent {
    public Event(String i1, String i2, long start, long stop, boolean replace) {
        super(i1, i2, start, stop, replace);
    }
}
