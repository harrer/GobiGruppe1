package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;

import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public interface Data {
    public List<String> findAll();
    public List<String> search(String keyword);
    public List<EventDisplay> select(List<String> keylist);
    public List<EventDisplay> filter(SpliceEventFilter sef);
}
