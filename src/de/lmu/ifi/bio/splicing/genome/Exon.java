package de.lmu.ifi.bio.splicing.genome;

public class Exon {
    long start, stop;
    int frame;

    public Exon(long start, long stop, int frame) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exon)) return false;

        Exon that = (Exon) o;

        if (frame != that.frame) return false;
        if (start != that.start) return false;
        if (stop != that.stop) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (stop ^ (stop >>> 32));
        result = 31 * result + frame;
        return result;
    }
}
