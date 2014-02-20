package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.eventdetection.EventDetector;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSP;
import de.lmu.ifi.bio.splicing.structures.mapping.Model;

import java.util.*;

/**
 * Created by uhligc on 14.02.14.
 */
public class DBUpdateRoutine {
    final static DBQuery dbq = new DBQuery();
    final static DBUpdate dbu = new DBUpdate();

    public static void insertEvents() {
        List<String> thebiglist = dbq.findAllGenes();
        for (String s : thebiglist) {
            Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(s));
            for (Event event : eventSet) {
                dbu.insertEvent(event);
            }
        }
    }

    public static void updateEvents(){
        List<String> geneids = dbq.findAllGenes();
        for(String geneid : geneids){
            List<Event> events = dbq.getEvents(geneid);
            HashMap<String, List<Model>> mapModels = new HashMap<>();
            for (Event event : events) {
                Model used = null;
                double quality = 0;
                if(mapModels.containsKey(event.getI1())){
                    List<Model> models = mapModels.get(event.getI1());
                    for (Model model : models) {
                        if(model.contains(event.getStart(), event.getStop()) && model.getQuality() > quality){

                            used = model;
                        }
                    }
                    if(used == null){
//                        used = modelling.getModel(int start, int stop);
                        models.add(used);
                    }
                } else {
                    List<Model> models = new LinkedList<>();
//                    used = modelling.getModel(int start, int stop);
                    models.add(used);
                    mapModels.put(event.getI1(), models);
                }
                DSSP.updateEventWithAccAndSS(used, event);
                dbu.fullUpdateEvent(event);
            }
        }
    }

    public static void insertEventSets() {
        List<String> thebiglist = dbq.findAllGenes();
        for (String s : thebiglist) {
            Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(s));
            dbu.insertEventSet(eventSet);
        }
    }

    public static void insertEventsForKeyword(String keyword) {
        List<String> list = dbq.search(keyword);
        for (String s : list) {
            if (s.startsWith("ENSG")) {
                insertEventsForGene(s);
            }
        }
    }

    public static void insertEventsForGene(String geneid) {
        Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(geneid));
        for (Event event : eventSet) {
            dbu.insertEvent(event);
        }
    }

    public static void main(String[] args) {
        insertEventSets();
    }
}
