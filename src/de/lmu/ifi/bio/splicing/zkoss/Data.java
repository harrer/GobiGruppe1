package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Gene;

import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public interface Data {
    public List<Gene> findAll();
    public List<Object> search(String keyword);
    public List<Object> select();
}
