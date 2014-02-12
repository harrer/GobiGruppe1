package de.lmu.ifi.bio.splicing.eventdetection;

import de.lmu.ifi.bio.splicing.genome.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDetector {
	
	public static Set<Event> getEvents(Transcript t1, Transcript t2, boolean strand){
		EventAnotation ea = new EventAnotation(t1, t2, strand);
		return makeEventSet(ea);
	}
	
	public static List<long[]> getEventList(Transcript t1, Transcript t2, boolean strand){
		EventAnotation ea = new EventAnotation(t1, t2, strand);
		return ea.getEventsP();
	}
	
	public static Set<Event> makeEventSet(EventAnotation ea){
		Set<Event> events = new HashSet<Event>();
		long ins = 0, dels = 0;
		for(long[] event : ea.getEventsP()){
			if(event[0] == 1){
				events.add(new Event(ea.getT1().getProteinId(), ea.getT2().getProteinId(), event[1] - ins, event[2] - ins, false));
				dels += event[2] - event[1] + 1;
			} else if(event[0] == 2){
				events.add(new Event(ea.getT2().getProteinId(), ea.getT1().getProteinId(), event[1] - dels, event[2] - dels, false));
				ins += event[2] - event[1] + 1;
			} else if(event[0] == 3){
				events.add(new Event(ea.getT1().getProteinId(), ea.getT2().getProteinId(), event[1] - ins, event[2] - ins, true));
			}
		}
		return events;
	}
	
	public static void printEvents(EventAnotation ea) {
		System.out.println("Conserved:");
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

		System.out.println("\nConserved:");
		for (long[] e : ea.getEventsP()) {
			if (e[0] == 0)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nDeletions:");
		for (long[] e : ea.getEventsP()) {
			if (e[0] == 1)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nInserts");
		for (long[] e : ea.getEventsP()) {
			if (e[0] == 2)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println("\nReplaces");
		for (long[] e : ea.getEventsP()) {
			if (e[0] == 3)
				System.out.print("(" + e[1] + ", " + e[2] + ") ");
		}
		System.out.println();
	}
	
	public void printVarSplice(EventAnotation ea) {
		StringBuilder sbp = new StringBuilder();
		for (long[] a : ea.getEventsP()) {
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
		// System.out.println(sbp.toString().replaceAll("(.)", "$1  "));
		System.out.println(sbp);
	}

}
