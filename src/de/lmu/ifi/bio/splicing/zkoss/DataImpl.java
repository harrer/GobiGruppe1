package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public class DataImpl implements Data {
    List<Object> searchlist = new ArrayList<>();


    //TODO Query auf Database
    DataImpl() {
        searchlist.add(new Gene("ENSG0000023", "1", true));
        searchlist.add(new Gene("ENSG0320002", "1", true));
        searchlist.add(new Gene("ENSG0033002", "1", true));
        searchlist.add(new Transcript("ENST3999393", "ENSP9999323"));
        searchlist.add(new Transcript("ENST399953", "ENSP9999323"));
        searchlist.add(new Transcript("ENST399923", "ENSP999923"));
        searchlist.add(new Transcript("ENST399923", "ENSP999923"));
    }

    @Override
    public List<String> findAll() {
        List<String> genes = new ArrayList<>();
        for (Object o : searchlist) {
            if (o instanceof Gene) {
                genes.add(((Gene) o).getGeneId());
            }
        }
        return genes;
    }

    @Override
    public List<String> search(String keyword) {
        List<String> tmp = new ArrayList<>();
        for (Object o : searchlist) {
            if (o instanceof Transcript) tmp.addAll(((Transcript) o).search(keyword));
            else tmp.addAll(((Gene) o).search(keyword));
        }
        return tmp;
    }

    @Override
    public List<String> select() {
        return null;
    }
}
