package de.lmu.ifi.bio.splicing.eventdetection;

import de.lmu.ifi.bio.splicing.genome.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EventDetector {

    public static Set<Event> getEvents(Gene g){
        Set<Event> events = new HashSet<>();
        HashMap<String, Transcript> hashmap = g.getHashmap_transcriptid();
        Transcript[] t = new Transcript[hashmap.size()];
        int counter = 0;
        for (Transcript transcript : hashmap.values()) {
            t[counter] = transcript;
            counter++;
        }
        for (int i = 0; i < t.length; i++) {
            for (int j = i + 1; j < t.length; j++) {
                 events.addAll(makeEventSet(new EventAnnotation(t[i], t[j], g.getStrand())));
            }
        }
        return events;
    }
	
	public static Set<Event> makeEventSet(EventAnnotation ea){
		Set<Event> events = new HashSet<>();
		for(long[] event : ea.getEventsP1()){
			if(event[0] == 1){
                events.add(new Event(ea.getT1().getTranscriptId(), ea.getT2().getTranscriptId(), (int) event[1], (int) event[2], 'D'));
            } else if(event[0] == 2){
                events.add(new Event(ea.getT1().getTranscriptId(), ea.getT2().getTranscriptId(), (int) event[1], (int) event[2], 'I'));
			} else if(event[0] == 3){
				events.add(new Event(ea.getT1().getTranscriptId(), ea.getT2().getTranscriptId(), (int) event[1], (int) event[2], 'R'));
			}
		}
        for(long[] event : ea.getEventsP2()){
            if(event[0] == 1){
                events.add(new Event(ea.getT2().getTranscriptId(), ea.getT1().getTranscriptId(), (int) event[1], (int) event[2], 'D'));
            } else if(event[0] == 2){
                events.add(new Event(ea.getT2().getTranscriptId(), ea.getT1().getTranscriptId(), (int) event[1], (int) event[2], 'I'));
            } else if(event[0] == 3){
                events.add(new Event(ea.getT2().getTranscriptId(), ea.getT1().getTranscriptId(), (int) event[1], (int) event[2], 'R'));
            }
        }
		return events;
	}
	
	public static void printEvents(EventAnnotation ea) {
		System.out.println("\nConserved:");
		for (long[] e : ea.getEventsN()) {
			if (e[0] == 0)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
		System.out.println("\nDeletions:");
		for (long[] e : ea.getEventsN()) {
			if (e[0] == 1)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nInserts");
		for (long[] e : ea.getEventsN()) {
			if (e[0] == 2)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}

        System.out.println("\nPlong\nConserved:");
        for (long[] e : ea.getEventsPLong()) {
            if (e[0] == 0)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
        System.out.println("\nDeletions:");
        for (long[] e : ea.getEventsPLong()) {
            if (e[0] == 1)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
        System.out.println("\nInserts");
        for (long[] e : ea.getEventsPLong()) {
            if (e[0] == 2)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
        System.out.println("\nReplaces");
        for (long[] e : ea.getEventsPLong()) {
            if (e[0] == 3)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }

        System.out.println("\nP1\nConserved:");
		for (long[] e : ea.getEventsP1()) {
			if (e[0] == 0)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nDeletions:");
		for (long[] e : ea.getEventsP1()) {
			if (e[0] == 1)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nInserts");
		for (long[] e : ea.getEventsP1()) {
			if (e[0] == 2)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nReplaces");
		for (long[] e : ea.getEventsP1()) {
			if (e[0] == 3)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
        System.out.println("\nP2\nConserved:");
        for (long[] e : ea.getEventsP2()) {
            if (e[0] == 0)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
        System.out.println("\nDeletions:");
        for (long[] e : ea.getEventsP2()) {
            if (e[0] == 1)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
        System.out.println("\nInserts");
        for (long[] e : ea.getEventsP2()) {
            if (e[0] == 2)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
        System.out.println("\nReplaces");
        for (long[] e : ea.getEventsP2()) {
            if (e[0] == 3)
                System.out.print("(" + e[1] + ", " + e[2] + ") ");
        }
		System.out.println();
	}
	
	public void printVarSplice(EventAnnotation ea) {
		StringBuilder sbp = new StringBuilder();
		for (long[] a : ea.getEventsP1()) {
			if (a[0] == 0) {
				for (long i = a[1]; i <= a[2]; i++) {
					sbp.append("*");
				}
			} else if (a[0] == 1) {
				for (long i = a[1]; i <= a[2]; i++) {
					sbp.append("D");
				}
			} else if (a[0] == 2) {
				for (long i = a[1]; i <= a[2]; i++) {
					sbp.append("I");
				}
			} else if (a[0] == 3) {
				for (long i = a[1]; i <= a[2]; i++) {
					sbp.append("R");
				}
			}
		}
		System.out.println(sbp);
	}
}
