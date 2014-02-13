package de.lmu.ifi.bio.splicing.eventdetection;


import de.lmu.ifi.bio.splicing.genome.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class EventAnnotation {
	private List<long[]> eventsN, eventsP;
	private long i1frame;
	private Transcript t1, t2;
	private boolean strand;

	public EventAnnotation(Transcript t1, Transcript t2, boolean strand) {
		this.t1 = t1;
		this.t2 = t2;
		i1frame = t1.getCds().get(0).getFrame();
		this.strand = strand;
		calculateNucleotideEvents();
		calculateProteinEvents();
		shortenEventsP();
	}

	public void calculateNucleotideEvents() {
		eventsN = new LinkedList<long[]>();
		List<Exon> cds1 = t1.getCds();
		List<Exon> cds2 = t2.getCds();
		if (!strand) {
			Collections.reverse(cds1);
			Collections.reverse(cds2);
		}
		Iterator<Exon> i1 = cds1.iterator();
		Iterator<Exon> i2 = cds2.iterator();
		Exon e1 = i1.next();
		Exon e2 = i2.next();
		long cur = Math.min(e1.getStart(), e2.getStart());
		while (true) {
			if (cur < e1.getStart() && cur >= e2.getStart()) {
				if (e2.getStop() <= e1.getStart()) {
					eventsN.add(new long[] { 2, cur, e2.getStop() });
					if (i2.hasNext()) {
						e2 = i2.next();
						cur = Math.min(e1.getStart(), e2.getStart());
					} else {
						e2 = null;
						cur = e1.getStart();
						break;
					}
				} else {
					eventsN.add(new long[] { 2, cur, e1.getStart() - 1 });
					cur = e1.getStart();
				}
			} else if (cur < e2.getStart() && cur >= e1.getStart()) {
				if (e1.getStop() <= e2.getStart()) {
					eventsN.add(new long[] { 1, cur, e1.getStop() });
					if (i1.hasNext()) {
						e1 = i1.next();
						cur = Math.min(e1.getStart(), e2.getStart());
					} else {
						cur = e2.getStart();
						e1 = null;
						break;
					}
				} else {
					eventsN.add(new long[] { 1, cur, e2.getStart() - 1 });
					cur = e2.getStart();
				}
			} else if (cur >= e1.getStart() && cur >= e2.getStart()) {
				if (e1.getStop() < e2.getStop()) {
					eventsN.add(new long[] { 0, cur, e1.getStop() });
					cur = e1.getStop() + 1;
					if (i1.hasNext()) {
						e1 = i1.next();
					} else {
						e1 = null;
						break;
					}
				} else if (e1.getStop() == e2.getStop()) {
					eventsN.add(new long[] { 0, cur, e1.getStop() });
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
					eventsN.add(new long[] { 0, cur, e2.getStop() });
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
			eventsN.add(new long[] { 1, cur, e1.getStop() });
			if (i1.hasNext()) {
				e1 = i1.next();
				cur = e1.getStart();
			} else {
				e1 = null;
			}
		}
		while (e2 != null) {
			eventsN.add(new long[] { 2, cur, e2.getStop() });
			if (i2.hasNext()) {
				e2 = i2.next();
				cur = e2.getStart();
			} else {
				e2 = null;
			}
		}
	}

	public void calculateProteinEvents() {
		eventsP = new LinkedList<long[]>();
		Iterator<long[]> it = eventsN.iterator();
		long[] a = it.next();
		long frameshift = 0;
		long cur = 0, len, extra = -i1frame;
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
					if ((len + extra) / 3 - len / 3 == 1) {
						eventsP.add(new long[] { 3, cur, cur });
						cur++;
					}
					eventsP.add(new long[] { 0, cur, cur + (len) / 3 - 1 });
				} else {
					if ((len + extra) / 3 - len / 3 == 1) {
						eventsP.add(new long[] { 3, cur, cur + (len) / 3 });
						cur++;
					} else {
						eventsP.add(new long[] { 3, cur, cur + (len) / 3 - 1 });
					}
				}
				cur += (len) / 3;
				extra = (len + extra) % 3;
			} else if (a[0] == 1) { // deletion
				len = 0;
				do {
					len += a[2] - a[1] + 1;
					if (it.hasNext())
						a = it.next();
					else
						a = null;
				} while (a != null && a[0] == 1);
				eventsP.add(new long[] { 1, cur, cur + (len + extra) / 3 - 1 });
				cur += (len + extra) / 3;
				extra = (len + extra) % 3;
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
				eventsP.add(new long[] { 2, cur, cur + (len + extra) / 3 - 1 });
				cur += (len + extra) / 3;
				extra = (len + extra) % 3;
				frameshift = (frameshift - len) % 3;
			}
		}
	}

    public void shortenEventsPNew(){
        List<long[]> shortAreasP1 = new LinkedList<long[]>();
        List<long[]> shortAreasP2 = new LinkedList<long[]>();
        long cur1 = 0;
        long cur2 = 0;
        long dels = 0;
        long ins = 0;
        for (long[] a : eventsP) {
            if (a[0] == 0) {
                if (dels != 0) {
                    shortAreasP1.add(new long[]{1, cur1, cur1 + dels});
                    shortAreasP2.add(new long[]{2, cur1, cur1 - 1});
                    cur1 += dels + 1;
                    dels = 0;
                }
                if (ins != 0) {
                    shortAreasP1.add(new long[]{2, cur1, cur1 - 1});
                    shortAreasP2.add(new long[]{1, cur2, cur2 + ins});
                    cur2 += ins + 1;
                    ins = 0;
                }
                shortAreasP1.add(new long[]{0, cur1, cur1 + a[2] - a[1]});
                shortAreasP2.add(new long[]{0, cur2, cur2 + a[2] - a[1]});
                cur1 += a[2] - a[1] + 1;
                cur2 += a[2] - a[1] + 1;
            } else if (a[0] == 1) {
                dels = a[2] - a[1];
                if(ins > 0){
                    shortAreasP1.add(new long[]{1, cur1, cur1 + dels});
                    shortAreasP2.add(new long[]{2, cur2, cur2 + ins});
                    cur1 += dels + 1;
                    cur2 += ins + 1;
                }
            } else if (a[0] == 2) {
                ins = a[2] - a[1];
                if(ins > 0){
                    shortAreasP1.add(new long[]{2, cur1, cur1 + dels});
                    shortAreasP2.add(new long[]{1, cur2, cur2 + ins});
                    cur1 += dels + 1;
                    cur2 += ins + 1;
                }
            } else {

            }
        }
        if (dels != 0) {
            shortAreasP1.add(new long[]{1, cur1, cur1 + dels});
            shortAreasP2.add(new long[]{2, cur1 - 1, cur1});
            cur1 += dels + 1;
            dels = 0;
        }
        if (ins != 0) {
            shortAreasP1.add(new long[]{2, cur1 - 1, cur1});
            shortAreasP2.add(new long[]{1, cur2, cur2 + ins});
            cur2 += ins + 1;
            ins = 0;
        }
    }

	public void shortenEventsP() {
		List<long[]> shortAreasP1 = new LinkedList<long[]>();
        List<long[]> shortAreasP2 = new LinkedList<long[]>();
		long cur1 = 0;
        long cur2 = 0;
		long dels = 0;
		long ins = 0;
		for (long[] a : eventsP) {
			if (a[0] == 0) {
				if (ins != 0) {
					shortAreasP1.add(new long[]{2, cur1, cur1 + ins});
                    shortAreasP2.add(new long[]{2, cur2, cur2 + ins});
					cur1 += ins + 1;
                    cur2 += ins + 1;
					ins = 0;
				}
				if (dels != 0) {
					shortAreasP1.add(new long[]{1, cur1, cur1 + dels});
                    shortAreasP2.add(new long[]{1, cur1, cur1 + dels});
					cur1 += dels + 1;
					dels = 0;
				}
				shortAreasP1.add(new long[]{0, cur1, cur1 += a[2] - a[1]});
				cur1++;
			} else if (a[0] == 1) {
				dels = a[2] - a[1];
				if (ins != 0) {
					if (ins > dels) {
						shortAreasP1.add(new long[]{2, cur1,
                                cur1 + dels - 1});
						cur1 += dels;
						shortAreasP2.add(new long[]{1, cur1, cur1 + dels});
						cur1 += dels + 1;
					} else {
						shortAreasP1.add(new long[]{3, cur1, cur1 + ins});
						cur1 += ins + 1;
						shortAreasP1.add(new long[]{1, cur1,
                                cur1 + dels - ins - 1});
						cur1 += dels - ins;
					}
					ins = 0;
					dels = 0;
				}
			} else if (a[0] == 2) {
				ins = a[2] - a[1];
				if (dels != 0) {
					if (dels > ins) {
						shortAreasP1.add(new long[]{1, cur1,
                                cur1 + dels - ins - 1});
						cur1 += dels - ins;
						shortAreasP1.add(new long[]{3, cur1, cur1 + ins});
						cur1 += ins + 1;
					} else {
						shortAreasP1.add(new long[]{3, cur1, cur1 + dels});
						cur1 += dels + 1;
						shortAreasP1.add(new long[]{2, cur1,
                                cur1 + ins - dels - 1});
						cur1 += ins - dels;
					}
					ins = 0;
					dels = 0;
				}
			} else {
				if (ins != 0) {
					shortAreasP1.add(new long[]{2, cur1, cur1 + ins});
					cur1 += ins + 1;
					ins = 0;
                }
				if (dels != 0) {
					shortAreasP1.add(new long[]{1, cur1, cur1 + dels});
					cur1 += dels + 1;
					dels = 0;
				}
				shortAreasP1.add(new long[]{3, cur1, cur1 += a[2] - a[1]});
				cur1++;
			}
		}
		eventsP = shortAreasP1;
	}

	public List<long[]> getEventsN() {
		return eventsN;
	}

	public List<long[]> getEventsP() {
		return eventsP;
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
