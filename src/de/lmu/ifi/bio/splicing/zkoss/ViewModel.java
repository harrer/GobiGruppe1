package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.*;

/**
 * Created by Carsten on 13.02.14.
 */
public class ViewModel {
    private String keyword;
    private List<String> searchlist = new ArrayList<>();
    private List<String> selectedSearchList = new ArrayList<>();
    private Data data = new DataImpl();
    private List<String> definedList = new LinkedList<>();
    private List<EventDisplay> gridlist = new ArrayList<>();
    private SpliceEventFilter filter = new SpliceEventFilter();

    public String getKeyword() {
        return keyword;
    }

    public List<String> getDefinedList() {
        return definedList;
    }

    public void setDefinedList(List<String> definedList) {
        this.definedList = definedList;
    }

    public List<EventDisplay> getGridlist() {
        return gridlist;
    }

    public void setGridlist(List<EventDisplay> gridlist) {
        this.gridlist = gridlist;
    }

    public List<String> getSearchlist() {
        return searchlist;
    }

    public List<String> getSelectedSearchList() {
        return selectedSearchList;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSearchlist(List<String> searchlist) {
        this.searchlist = searchlist;
    }

    public void setSelectedSearchList(List<String> selectedSearchList) {
        this.selectedSearchList = selectedSearchList;
    }

    public SpliceEventFilter getFilter() {
        return filter;
    }

    public void setFilter(SpliceEventFilter filter) {
        this.filter = filter;
    }

    @Command
    @NotifyChange("searchlist")
    public void search() {
        searchlist = data.search(keyword);
    }

    @Command
    @NotifyChange("gridlist")
    public void add() {
        gridlist = new ArrayList<>();
        definedList = new LinkedList<>();
        definedList.addAll(selectedSearchList);
        selectedSearchList = new ArrayList<>();
        gridlist = data.select(definedList);
    }

    @Command
    @NotifyChange("gridlist")
    public void changeFilter() {
        gridlist = data.filter(filter);
    }
}
