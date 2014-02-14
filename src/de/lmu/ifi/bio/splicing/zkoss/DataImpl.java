package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.SecondaryStructure;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Carsten on 13.02.14.
 */
public class DataImpl implements Data {
    List<String> searchlist;
    List<EventDisplay> eventlist;
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
        List<String> geneslist = new LinkedList<>();
        List<String> transcriptslist = new LinkedList<>();
        List<String> proteinslist = new LinkedList<>();

        for (String s : keylist) {
            if (s.startsWith("ENSG"))
                geneslist.add(s);
            else if (s.startsWith("ENST"))
                transcriptslist.add(s);
            else if (s.startsWith("ENSP"))
                proteinslist.add(s);
        }

        List<Gene> genes = new LinkedList<>();
        for (String gene : geneslist) {
            genes.add(dbq.getGene(gene));
        }

        //iterate over protein list and transcript list

        for (Gene gene : genes) {
            eventlist.addAll(getEventsPerGene(gene));
        }

        //TODO select implement
        //liste von strings die geneids, transcriptids und proteinids enthalten
        //muss jeweils mit get gene/transcript abgefragt werden
        return eventlist;
    }


    private List<EventDisplay> getEventsPerGene(Gene agene) {
        List<EventDisplay> tmp = new LinkedList<>();
        for (Map.Entry<String, Transcript> stringTranscriptEntry : agene.getHashmap_transcriptid().entrySet()) {
            String transcript1 = stringTranscriptEntry.getKey();
            for (Map.Entry<String, Transcript> transcriptEntry : agene.getHashmap_transcriptid().entrySet()) {
                String transcript2 = transcriptEntry.getKey();
                Event tmpevent = dbq.getEvent(transcript1, transcript2);
                tmp.add(new EventDisplay(tmpevent.getI1(), tmpevent.getI2(), tmpevent.getStart(), tmpevent.getStop(), tmpevent.getType(), 0.0, new Pattern("P00000", 1, 2), SecondaryStructure.HELIX));
            }
        }
        return tmp;
    }

}
