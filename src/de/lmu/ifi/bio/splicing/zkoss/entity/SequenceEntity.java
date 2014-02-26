package de.lmu.ifi.bio.splicing.zkoss.entity;

/**
 * Created by Carsten Uhlig on 20.02.14.
 */
public class SequenceEntity {
    private String aa1;
    private String aa2;
    private String varsplic;
    private String sec;
    private String acc;
    private String prosite;
    private String transcriptid;

    public String getAa1raw() {
        return aa1raw;
    }

    public void setAa1raw(String aa1raw) {
        this.aa1raw = aa1raw;
    }

    private String aa1raw;

    public SequenceEntity(String aa1, String transcriptid, String prosite) {
        this.aa1 = aa1;
        this.transcriptid = transcriptid;
        this.prosite = prosite;
    }

    public String getTranscriptid() {
        return transcriptid;
    }

    public SequenceEntity(String aa1, String aa2, String varsplic, String sec, String acc, String prosite, String transcriptid) {
        this.aa1 = aa1;
        this.aa2 = aa2;
        this.varsplic = varsplic;
        this.sec = sec;
        this.acc = acc;
        this.prosite = prosite;
        this.transcriptid = transcriptid;
    }

    public String getAa2() {

        return aa2;
    }

    public void setAa2(String aa2) {
        this.aa2 = aa2;
    }

    public void setTranscriptid(String transcriptid) {
        this.transcriptid = transcriptid;
    }

    public SequenceEntity(String aa1, String varsplic) {
        this.aa1 = aa1;
        this.varsplic = varsplic;
    }

    public SequenceEntity() {
    }

    public SequenceEntity(String aa1) {
        this.aa1 = aa1;
    }

    public SequenceEntity(String aa1, String sec, String acc, String prosite) {
        this.aa1 = aa1;
        this.sec = sec;
        this.acc = acc;
        this.prosite = prosite;
    }

    public String getAa1() {

        return aa1;
    }

    public void setAa1(String aa1) {
        this.aa1 = aa1;
    }

    public String getVarsplic() {
        return varsplic;
    }

    public void setVarsplic(String varsplic) {
        this.varsplic = varsplic;
    }

    public String getSec() {
        return sec;
    }

    public void setSec(String sec) {
        this.sec = sec;
    }

    public String getAcc() {
        return acc;
    }

    public void setAcc(String acc) {
        this.acc = acc;
    }

    public String getProsite() {
        return prosite;
    }

    public void setProsite(String prosite) {
        this.prosite = prosite;
    }

    public SequenceEntity(String aa1, String varsplic, String sec, String acc, String prosite) {
        this.aa1 = aa1;
        this.varsplic = varsplic;
        this.sec = sec;
        this.acc = acc;
        this.prosite = prosite;
    }
}
