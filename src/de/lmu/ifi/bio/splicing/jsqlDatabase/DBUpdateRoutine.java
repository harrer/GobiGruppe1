package de.lmu.ifi.bio.splicing.jsqlDatabase;

import de.lmu.ifi.bio.splicing.eventdetection.EventDetector;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.homology.ModelPDB_onENSP;
import de.lmu.ifi.bio.splicing.io.DSSPParser;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSP;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;
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

    public static void updateEvents() {
        ModelPDB_onENSP modelling = new ModelPDB_onENSP();
        List<String> geneIds = dbq.findAllGenes();
        HashMap<String, DSSPData> dsspData = new HashMap<>();
        HashMap<String, double[]> ssDistribution = new HashMap<>();
        HashMap<String, double[]> accDistribution = new HashMap<>();
        long time = System.currentTimeMillis();
        int i = 0, percent = 0;
        for (String geneId : geneIds) {
            List<Event> events = dbq.getEvents(geneId);
            HashMap<String, List<Model>> mapModels = new HashMap<>();
            for (Event event : events) {
                Model used = null;
                double quality = 0;
                List<Model> models;
                if (mapModels.containsKey(event.getI1())) {
                    models = mapModels.get(event.getI1());
                } else {
                    models = modelling.getModelsForENST(event.getI1());
                    mapModels.put(event.getI1(), models);
                }
                for (Model model : models) {
                    if (model.contains(event.getStart(), event.getStop()) && model.getQuality() > quality) {
                        used = model;
                        quality = model.getQuality();
                    }
                }
                if (used != null) {
                    DSSPData dssp;
                    if (dsspData.containsKey(used.getPdbId())) {
                        dssp = dsspData.get(used.getPdbId());
                    } else {
                        dssp = DSSPParser.getDSSPData(used.getPdbId());
                        dsspData.put(used.getPdbId(), dssp);
                    }
                    if (dssp != null) {
                        try {
                            DSSP.updateEventWithAccAndSS(used, event, dssp);
                            double expSS[] = DSSP.calcSecondaryStructureDistribution(dssp, used.getPdbStart(), used.getPdbStop(),
                                    used.getEnspStop() - used.getEnspStart() + 1);
                            double expAcc[] = DSSP.calcAccessibilityDistribution(dssp, used.getPdbStart(), used.getPdbStop(),
                                    used.getEnspStop() - used.getEnspStart() + 1);
                            ssDistribution.put(event.getI1() + event.getStart() + "|" + event.getStop(), new double[] {event.getType(), expSS[0], expSS[1], expSS[2]});
                            accDistribution.put(event.getI1() + event.getStart() + "|" + event.getStop(), new double[] {event.getType(), expAcc[0], expAcc[1], expAcc[2]});
//                            System.out.printf("Isoform1: %s start: %d stop: %d, Expected: %d %d %d%n", event.getI1(),
//                                    event.getStart(), event.getStop(), expSS[0], expSS[1], expSS[2]);
                        } catch (Exception e) {
                            System.out.println("Exception while using Model " + used.getPdbId() + " for " + event.getI1());
                            e.printStackTrace();
                        }
                    }
                }
            }
            i++;
            if (i % (geneIds.size() / 100) == 0) {
                long da = System.currentTimeMillis();
                percent++;
                System.out.printf("Progress: %d%% after %.1f min, %.1f min left %n", percent, ((da - time) / (float) 60000), ((da - time) / (float) i) * (geneIds.size() - i) / (float) 60000);
            }
//            if(i > 100)
//                break;
        }
        DSSPParser.saveSSDistribution(DSSP.calcSecondaryStructureDistribution(dsspData.values()));
        DSSPParser.saveExpectedSS(ssDistribution.values(), accDistribution.values());
    }

    public static void insertEventSets() {
        List<String> thebiglist = dbq.findAllGenes();
        int size = thebiglist.size();
        int counter = 0;
        long start = System.currentTimeMillis();
        for (String s : thebiglist) {

            try {
                if ((counter % (size / 100)) == 0)
                    System.out.printf("Bereits %.1fProzent und noch %d sec %n", size / (float) counter, (((System.currentTimeMillis() - start) / counter) * (size - counter)) / 1000);
            } catch (ArithmeticException e) {
                System.out.println("da legst di nida");
            }
            Set<Event> eventSet = EventDetector.getEvents(dbq.getGene(s));
            dbu.insertEventSet(eventSet);
            counter++;
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
        if (args.length > 0 && args[0].equalsIgnoreCase("event"))
            insertEventSets();
        else
            updateEvents();
    }
}
