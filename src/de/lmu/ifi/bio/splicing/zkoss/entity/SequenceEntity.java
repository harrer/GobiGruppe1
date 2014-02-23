package de.lmu.ifi.bio.splicing.zkoss.entity;

/**
 * Created by Carsten Uhlig on 20.02.14.
 */
public class SequenceEntity {
    private String aa,varsplic,sec,acc,prosite, transcriptid;

    public SequenceEntity(String aa, String transcriptid, String prosite) {
        this.aa = aa;
        this.transcriptid = transcriptid;
        this.prosite = prosite;
    }

    public String getTranscriptid() {
        return transcriptid;
    }

    public void setTranscriptid(String transcriptid) {
        this.transcriptid = transcriptid;
    }

    public SequenceEntity(String aa, String varsplic) {
        this.aa = aa;
        this.varsplic = varsplic;
    }

    public SequenceEntity() {
    }

    public SequenceEntity(String aa) {
        this.aa = aa;
    }

    public SequenceEntity(String aa, String sec, String acc, String prosite) {
        this.aa = aa;
        this.sec = sec;
        this.acc = acc;
        this.prosite = prosite;
    }

    public String getAa() {

        return aa;
    }

    public void setAa(String aa) {
        this.aa = aa;
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

    public SequenceEntity(String aa, String varsplic, String sec, String acc, String prosite) {

        this.aa = aa;
        this.varsplic = varsplic;
        this.sec = sec;
        this.acc = acc;
        this.prosite = prosite;
    }
}
