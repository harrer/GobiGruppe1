package de.lmu.ifi.bio.splicing.zkoss;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public class ViewModel {
    private String keyword;
    private List<String> searchList = new ArrayList<>();
    private List<String> selectedSearchList = new ArrayList<>();
    private Data data = new DataImpl();

    public String getKeyword() {
        return keyword;
    }

    public List<String> getSearchList() {
        return searchList;
    }

    public List<String> getSelectedSearchList() {
        return selectedSearchList;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSearchList(List<String> searchList) {
        this.searchList = searchList;
    }

    public void setSelectedSearchList(List<String> selectedSearchList) {
        this.selectedSearchList = selectedSearchList;
    }

    @Command
    @NotifyChange("searchList")
    public void search() {
        searchList = data.search(keyword);
    }

}
