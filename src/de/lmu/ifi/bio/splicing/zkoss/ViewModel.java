package de.lmu.ifi.bio.splicing.zkoss;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carsten on 13.02.14.
 */
public class ViewModel {
    private String keyword;
    private List<Object> searchList = new ArrayList<>();
    private List<Object> selectedSearchList = new ArrayList<>();
    private Data data = new DataImpl();

    public String getKeyword() {
        return keyword;
    }

    public List<Object> getSearchList() {
        return searchList;
    }

    public List<Object> getSelectedSearchList() {
        return selectedSearchList;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public void setSearchList(List<Object> searchList) {
        this.searchList = searchList;
    }

    public void setSelectedSearchList(List<Object> selectedSearchList) {
        this.selectedSearchList = selectedSearchList;
    }
}
