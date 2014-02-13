package de.lmu.ifi.bio.splicing.zkoss;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public class ViewModel {
    private String keyword;
    private List<String> searchlist = new ArrayList<>();
    private List<String> selectedSearchList = new ArrayList<>();
    private Data data = new DataImpl();

    public String getKeyword() {
        return keyword;
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

    @Command
    @NotifyChange("searchlist")
    public void search() {
        searchlist = data.search(keyword);
    }

    @NotifyChange("detailedview")
    public void processSelectedItems() {

    }
}
