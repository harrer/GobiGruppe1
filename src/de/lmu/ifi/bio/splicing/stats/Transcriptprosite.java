package de.lmu.ifi.bio.splicing.stats;

import de.lmu.ifi.bio.splicing.genome.Transcript;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Carsten Uhlig on 24.02.14.
 */
public class Transcriptprosite {
    private Set<String> prositepatterns = new HashSet<>();
    private String transcriptid;

    public Transcriptprosite(String transcript_id) {
        this.transcriptid = transcript_id;
    }

    public Set<String> getPrositepatterns() {
        return prositepatterns;
    }

    public void setPrositepatterns(Set<String> prositepatterns) {
        this.prositepatterns = prositepatterns;
    }

    public void addPrositePattern(String pattern) {
        if (!prositepatterns.contains(pattern))
            prositepatterns.add(pattern);
    }

    public boolean hasPrositeid(String prositeid) {
        return prositepatterns.contains(prositeid);
    }
}
