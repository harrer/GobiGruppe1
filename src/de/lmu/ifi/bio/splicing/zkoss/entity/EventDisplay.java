package de.lmu.ifi.bio.splicing.zkoss.entity;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Gene;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by uhligc on 13.02.14.
 */
public class EventDisplay extends Event {
    public void setPattern(List<PatternEvent> pattern) {
        this.pattern = pattern;
        calcpatternids();
    }

    public void setPatternids(String patternids) {
        this.patternids = patternids;
    }

    //    private char acc;
    private List<PatternEvent> pattern;
//    private char sec;
    private String patternids;
    private Gene curGene;

    public Gene getCurGene() {
        return curGene;
    }

    public void setCurGene(Gene curGene) {
        this.curGene = curGene;
    }

    public EventDisplay(String i1, String i2, int start, int stop, char type, char acc, List<PatternEvent> pattern, char sec) {
        super(i1, i2, start, stop, type);
        this.setSecondaryStructure(sec);
        this.setAccess(acc);
        this.pattern = pattern;
    }

    public EventDisplay(String i1, String i2, int start, int stop, char type, char acc, List<PatternEvent> pattern, char sec, Gene gene) {
        super(i1, i2, start, stop, type);
        this.setSecondaryStructure(sec);
        this.setAccess(acc);
        this.pattern = pattern;
        curGene = gene;
    }

    public EventDisplay(String i1, String i2, int start, int stop, char type, char acc, char startSS, char stopSS, char startAcc, char stopAcc) {
        super(i1, i2, start, stop, type, startSS, stopSS, startAcc, stopAcc, acc);
        this.pattern = pattern;
    }

    public String getPatternids() {
        calcpatternids();
        return patternids;
    }

    public List<PatternEvent> getPattern() {
        return pattern;
    }

    public void calcpatternids() {
        Set<String> dada = new HashSet<>();
        for (PatternEvent patternEvent : pattern) {
            if (!dada.contains(patternEvent.getId()))
                dada.add(patternEvent.getId());
        }

        if (dada.size() > 1)
            System.out.println(dada);

        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (String s : dada) {
            sb.append(prefix);
            prefix = "\n";
            sb.append(s);
        }
        patternids = sb.toString();
    }

    @Override
    public String toString() {
        return "EventDisplay{" + super.toString() +
                "acc=" + getAccessibility() +
                ", pattern=" + pattern +
                ", sec=" + getSecondaryStructure() +
                '}';
    }
}
