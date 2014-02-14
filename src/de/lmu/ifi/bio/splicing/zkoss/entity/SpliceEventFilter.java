package de.lmu.ifi.bio.splicing.zkoss.entity;

/**
 * Created by uhligc on 14.02.14.
 * Filter für ZKoss Frontend.
 * Ist als Objekt für den Datenfilter der detailed View zuständig.
 */
public class SpliceEventFilter {
    private String i1 = "", i2 = "", start = "", stop = "", type = "", sec = "", pattern = "", acc = "";

    public String getI1() {
        return i1;
    }

    public void setI1(String i1) {
        this.i1 = i1;
    }

    public String getI2() {
        return i2;
    }

    public void setI2(String i2) {
        this.i2 = i2;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }
}
