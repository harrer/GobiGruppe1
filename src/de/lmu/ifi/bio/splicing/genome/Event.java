package de.lmu.ifi.bio.splicing.genome;

/**
 * Created by uhligc on 12.02.14.
 */
public class Event {
    //i1 und i2 sind transcript ids (keine protein ids)
    String i1, i2;
    long start, stop;
    byte type; //1 = deletion, 2 = insert, 3 = replace

    public Event(String i1, String i2, long start, long stop, byte type) {
        this.i1 = i1;
        this.i2 = i2;
        this.start = start;
        this.stop = stop;
        this.type = type;
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

    public byte getType() { return type; }
}
