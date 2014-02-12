package de.lmu.ifi.bio.splicing.eventdetection;

import de.lmu.ifi.bio.splicing.genome.*;
import java.util.HashSet;
import java.util.Set;

public class EventDetector {
	
	public static Set<Event> getEvents(Transcript t1, Transcript t2, boolean strand){
		EventAnotation ea = new EventAnotation(t1, t2, strand);
		return makeEventSet(ea);
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
}
