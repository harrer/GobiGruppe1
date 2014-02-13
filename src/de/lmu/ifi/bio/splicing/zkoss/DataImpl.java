package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public class DataImpl implements Data {
    List<String> searchlist = new LinkedList<>();
    DBQuery dbq = new DBQuery();

    //TODO Query auf Database
    DataImpl() {
//        searchlist.add("ENSG2929293");
//        searchlist.add("ENSG2320392093");
//        searchlist.add("ENST2398283928");
    }

    @Override
    public List<String> findAll() {
        return new DBQuery().findAllGenes();
    }

    @Override
    public List<String> search(String keyword) {
        return dbq.search(keyword);
    }

    @Override
    public List<Object[]> select(List<String> keylist) {
        //TODO select implement
        //liste von strings die geneids, transcriptids und proteinids enthalten
        //muss jeweils mit get gene/transcript abgefragt werden
        return null;
    }
}
