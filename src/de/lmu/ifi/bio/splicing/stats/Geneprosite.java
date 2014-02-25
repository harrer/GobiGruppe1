package de.lmu.ifi.bio.splicing.stats;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Carsten Uhlig on 24.02.14.
 */
public class Geneprosite {
    private Set<String> prosite_patterns = new HashSet<>();
    private HashMap<String, Transcriptprosite> transcriptprositeHashMap = new HashMap<>();
    private String geneid;

    public Geneprosite(String geneid) {
        this.geneid = geneid;
    }

    public Set<String> getProsite_patterns() {
        return prosite_patterns;
    }

    public void setProsite_patterns(Set<String> prosite_patterns) {
        this.prosite_patterns = prosite_patterns;
    }

    public void addPattern(String pattern) {
        if (!prosite_patterns.contains(pattern))
            prosite_patterns.add(pattern);
    }

    public double getParts(String patternid) {
        return getTranscriptsCount(patternid) / getTotal();
    }

    public int getTotal() {
        return transcriptprositeHashMap.size();
    }

    public int getTranscriptsCount(String patternid) {
        int counter = 0;

        for (Transcriptprosite transcriptprosite : transcriptprositeHashMap.values()) {
            if (transcriptprosite.hasPrositeid(patternid))
                counter++;
        }

        return counter;
    }

    public void addTranscript(String transcriptid) {
        if (!transcriptprositeHashMap.containsKey(transcriptid))
            transcriptprositeHashMap.put(transcriptid, new Transcriptprosite(transcriptid));
    }

    public void addPair(String transcriptid, String patternid) {
        addTranscript(transcriptid);
        transcriptprositeHashMap.get(transcriptid).addPrositePattern(patternid);
        addPattern(patternid);
    }

    public boolean hasPrositeid(String prositeid) {
        return prosite_patterns.contains(prositeid);
    }
}
