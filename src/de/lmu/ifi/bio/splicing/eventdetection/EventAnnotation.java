package de.lmu.ifi.bio.splicing.eventdetection;


import de.lmu.ifi.bio.splicing.genome.*;

import java.util.*;

public class EventAnnotation {
    private List<long[]> eventsN, eventsPLong, eventsP1, eventsP2;
    private long i1frame, i2frame;
    private Transcript t1, t2;
    private boolean strand;

    public EventAnnotation(Transcript t1, Transcript t2, boolean strand) {
        this.t1 = t1;
        this.t2 = t2;
        this.strand = strand;
        calculateNucleotideEvents();
        calculateProteinEvents();
        shortenEventsP();
    }

    public void calculateNucleotideEvents() {
        eventsN = new LinkedList<>();
        List<Exon> cds1 = t1.getCds();
        List<Exon> cds2 = t2.getCds();
        i1frame = t1.getCds().get(0).getFrame();
        i2frame = t1.getCds().get(0).getFrame();
        Iterator<Exon> i1 = cds1.iterator();
        Iterator<Exon> i2 = cds2.iterator();
        Exon e1 = i1.next();
        Exon e2 = i2.next();
        long cur = Math.min(e1.getStart(), e2.getStart());
        while (true) {
            if (cur < e1.getStart() && cur >= e2.getStart()) {
                if (e2.getStop() <= e1.getStart()) {
                    eventsN.add(new long[]{2, cur, e2.getStop()});
                    if (i2.hasNext()) {
                        e2 = i2.next();
                        cur = Math.min(e1.getStart(), e2.getStart());
                    } else {
                        e2 = null;
                        cur = e1.getStart();
                        break;
                    }
                } else {
                    eventsN.add(new long[]{2, cur, e1.getStart() - 1});
                    cur = e1.getStart();
                }
            } else if (cur < e2.getStart() && cur >= e1.getStart()) {
                if (e1.getStop() <= e2.getStart()) {
                    eventsN.add(new long[]{1, cur, e1.getStop()});
                    if (i1.hasNext()) {
                        e1 = i1.next();
                        cur = Math.min(e1.getStart(), e2.getStart());
                    } else {
                        cur = e2.getStart();
                        e1 = null;
                        break;
                    }
                } else {
                    eventsN.add(new long[]{1, cur, e2.getStart() - 1});
                    cur = e2.getStart();
                }
            } else if (cur >= e1.getStart() && cur >= e2.getStart()) {
                if (e1.getStop() < e2.getStop()) {
                    eventsN.add(new long[]{0, cur, e1.getStop()});
                    cur = e1.getStop() + 1;
                    if (i1.hasNext()) {
                        e1 = i1.next();
                    } else {
                        e1 = null;
                        break;
                    }
                } else if (e1.getStop() == e2.getStop()) {
                    eventsN.add(new long[]{0, cur, e1.getStop()});
                    if (i1.hasNext() && i2.hasNext()) {
                        e1 = i1.next();
                        e2 = i2.next();
                        cur = Math.min(e1.getStart(), e2.getStart());
                    } else if (i1.hasNext()) {
                        e1 = i1.next();
                        cur = e1.getStart();
                        e2 = null;
                        break;
                    } else if (i2.hasNext()) {
                        e2 = i2.next();
                        cur = e2.getStart();
                        e1 = null;
                        break;
                    } else {
                        e1 = null;
                        e2 = null;
                        break;
                    }
                } else {
                    eventsN.add(new long[]{0, cur, e2.getStop()});
                    cur = e2.getStop() + 1;
                    if (i2.hasNext()) {
                        e2 = i2.next();
                    } else {
                        e2 = null;
                        break;
                    }
                }
            }
        }
        while (e1 != null) {
            eventsN.add(new long[]{1, cur, e1.getStop()});
            if (i1.hasNext()) {
                e1 = i1.next();
                cur = e1.getStart();
            } else {
                e1 = null;
            }
        }
        while (e2 != null) {
            eventsN.add(new long[]{2, cur, e2.getStop()});
            if (i2.hasNext()) {
                e2 = i2.next();
                cur = e2.getStart();
            } else {
                e2 = null;
            }
        }
    }

    public void calculateProteinEvents() {
        List<long[]> eventsNWork;
        if (strand) {
            eventsNWork = eventsN;
        } else {
            eventsNWork = new LinkedList<>();
            long max = eventsN.get(eventsN.size() - 1)[2];
            for (long[] event : eventsN) {
                eventsNWork.add(new long[]{event[0], max - event[2], max - event[1]});
            }
            Collections.reverse(eventsNWork);
        }
        eventsPLong = new LinkedList<>();
        ListIterator<long[]> it = eventsNWork.listIterator();
        long[] a = it.next();
        long frameshift = i1frame - i2frame;
        long cur = 0, len, extra1 = -i1frame, extra2 = -i2frame;
        boolean first = true;
        while (a != null) {
            if (a[0] == 0) { // conserved
                len = 0;
                do {
                    len += a[2] - a[1] + 1;
                    if (it.hasNext())
                        a = it.next();
                    else
                        a = null;
                } while (a != null && a[0] == 0);
                if (frameshift == 0) {
                    if (Math.min(extra1, extra2) != 0) {
                        if (first) {
                            eventsPLong.add(new long[]{0, cur, cur + (len + Math.min(extra1, extra2)) / 3 - 1});
                            first = false;
                        } else {
                            eventsPLong.add(new long[]{3, cur, cur});
                            eventsPLong.add(new long[]{0, cur + 1, cur + (len + Math.min(extra1, extra2)) / 3 - 1});
                        }
                    } else {
                        eventsPLong.add(new long[]{0, cur, cur + (len + Math.min(extra1, extra2)) / 3 - 1});
                    }
                } else {
                    eventsPLong.add(new long[]{3, cur, cur + (len + Math.min(extra1, extra2)) / 3 - 1});
                }
                cur += (len + Math.min(extra1, extra2)) / 3;
                if (extra1 > extra2) {
                    extra1 = extra1 - extra2 + (len + extra2) % 3;
                    extra2 = (len + extra2) % 3;
                } else if (extra1 < extra2) {
                    extra2 = extra2 - extra1 + (len + extra1) % 3;
                    extra1 = (len + extra1) % 3;
                } else {
                    extra1 = (len + extra1) % 3;
                    extra2 = (len + extra2) % 3;
                }
            } else if (a[0] == 1) { // deletion
                len = 0;
                do {
                    len += a[2] - a[1] + 1;
                    if (it.hasNext())
                        a = it.next();
                    else
                        a = null;
                } while (a != null && a[0] == 1);
                eventsPLong.add(new long[]{1, cur, cur + (len + extra1) / 3 - 1});
                cur += (len + extra1) / 3;
                extra1 = (len + extra1) % 3;
                frameshift = (frameshift + len) % 3;
            } else if (a[0] == 2) { // insert
                len = 0;
                do {
                    len += a[2] - a[1] + 1;
                    if (it.hasNext())
                        a = it.next();
                    else
                        a = null;
                } while (a != null && a[0] == 2);
                eventsPLong.add(new long[]{2, cur, cur + (len + extra2) / 3 - 1});
                cur += (len + extra2) / 3;
                extra2 = (len + extra2) % 3;
                frameshift = (frameshift - len) % 3;
            }
        }
    }

    public void shortenEventsP() {
        eventsP1 = new LinkedList<>();
        eventsP2 = new LinkedList<>();
        long cur1 = 0;
        long cur2 = 0;
        long dels = 0;
        long ins = 0;
        long reps1 = 0;
        long reps2 = 0;
        for (long[] a : eventsPLong) {
            if (a[0] == 0) {
                if (reps1 != 0) {
                    eventsP1.add(new long[]{3, cur1, cur1 + reps1 + dels - 1});
                    eventsP2.add(new long[]{3, cur2, cur2 + reps2 + ins - 1});
                    cur1 += reps1 + dels;
                    cur2 += reps2 + ins;
                    reps1 = 0;
                    reps2 = 0;
                    dels = 0;
                    ins = 0;
                } else {
                    if (dels != 0) {
                        eventsP1.add(new long[]{1, cur1, cur1 + dels - 1});
                        eventsP2.add(new long[]{2, cur2, cur2 - 1});
                        cur1 += dels;
                        dels = 0;
                    } else if (ins != 0) {
                        eventsP1.add(new long[]{2, cur1, cur1 - 1});
                        eventsP2.add(new long[]{1, cur2, cur2 + ins - 1});
                        cur2 += ins;
                        ins = 0;
                    }
                }
                eventsP1.add(new long[]{0, cur1, cur1 + a[2] - a[1]});
                eventsP2.add(new long[]{0, cur2, cur2 + a[2] - a[1]});
                cur1 += a[2] - a[1] + 1;
                cur2 += a[2] - a[1] + 1;
            } else if (a[0] == 3) {
                reps1 += a[2] - a[1] + 1;
                reps2 += a[2] - a[1] + 1;
            } else {
                if (a[0] == 1) {
                    dels = a[2] - a[1] + 1;
                } else if (a[0] == 2) {
                    ins = a[2] - a[1] + 1;
                }
                if (ins > 0 && dels > 0) {
                    reps1 += dels;
                    reps2 += ins;
                    ins = 0;
                    dels = 0;
                }
            }
        }
        if (reps1 != 0) {
            eventsP1.add(new long[]{3, cur1, cur1 + reps1 + dels - 1});
            eventsP2.add(new long[]{3, cur2, cur2 + reps2 + ins - 1});
        } else if (dels != 0) {
            eventsP1.add(new long[]{1, cur1, cur1 + dels - 1});
            eventsP2.add(new long[]{2, cur2, cur2 - 1});
        } else if (ins != 0) {
            eventsP1.add(new long[]{2, cur1, cur1 - 1});
            eventsP2.add(new long[]{1, cur2, cur2 + ins - 1});
        }
    }

    public List<long[]> getEventsN() {
        return eventsN;
    }

    public List<long[]> getEventsPLong() {
        return eventsPLong;
    }

    public List<long[]> getEventsP1() {
        return eventsP1;
    }

    public List<long[]> getEventsP2() {
        return eventsP2;
    }

    public long getI1frame() {
        return i1frame;
    }

    public Transcript getT1() {
        return t1;
    }

    public Transcript getT2() {
        return t2;
    }

    public boolean isStrand() {
        return strand;
    }

}
