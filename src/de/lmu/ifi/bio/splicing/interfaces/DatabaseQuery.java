package de.lmu.ifi.bio.splicing.interfaces;

import de.lmu.ifi.bio.splicing.genome.*;

import java.util.List;

public interface DatabaseQuery {

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

    public Event getEvent(String isoform1, String isoform2);
    
    public Gene getGene(String geneID);

    public Transcript getTranscript(String transcriptID);
}
