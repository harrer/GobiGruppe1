package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.interfaces.*;
import de.lmu.ifi.bio.splicing.genome.*;

import java.util.List;

public interface DatabaseQuery {
    public Event getEvent();

    /**
     * Soll nach Gene Ids, Transcript Ids und Protein Ids suchen können
     *
     * @param keyword
     * @return
     */
    public List<String> search(String keyword);

    public List<String> findAllGenes();

    public List<String> findAllProteins();

    public List<String> findAllTranscripts();

    public Gene getGene(String geneID);

    /**
     * @param proteinID may be null, transcriptID must be valid
     */
    public Transcript getTranscript(String transcriptID, String proteinID);
}
