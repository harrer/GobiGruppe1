package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Event;
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
    List<String> searchlist;
    List<Event> eventlist;
    DBQuery dbq;

    //TODO Query auf Database
    DataImpl() {
        dbq = new DBQuery();
        searchlist = new LinkedList<>();
        eventlist = new ArrayList<>();
    }

    @Override
    public List<String> findAll() {
        return dbq.findAllGenes();
    }

    @Override
    public List<String> search(String keyword) {
        return dbq.search(keyword);
    }

    @Override
    public List<EventDisplay> select(List<String> keylist) {
        //TODO select implement
        //liste von strings die geneids, transcriptids und proteinids enthalten
        //muss jeweils mit get gene/transcript abgefragt werden
        return null;
    }
}
