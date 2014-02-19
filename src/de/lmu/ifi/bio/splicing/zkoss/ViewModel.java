package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import java.awt.image.RenderedImage;
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
    private EventDisplay eventDisplay;
    private RenderedImage renderedImage; // set int und height by init
    private int height_img_ri = 400, width_img_ri = 400;
    private Gene selectedGene;

    public Gene getSelectedGene() {
        return selectedGene;
    }

    public void setSelectedGene(Gene selectedGene) {
        this.selectedGene = selectedGene;
    }

    public EventDisplay getEventDisplay() {
        return eventDisplay;
    }

    public RenderedImage getRenderedImage() {
        return renderedImage;
    }

    public void setRenderedImage(RenderedImage renderedImage) {
        this.renderedImage = renderedImage;
    }

    public int getHeight_img_ri() {
        return height_img_ri;
    }

    public void setHeight_img_ri(int height_img) {
        this.height_img_ri = height_img;
    }

    public int getWidth_img_ri() {
        return width_img_ri;
    }

    public void setWidth_img_ri(int width_img_ri) {
        this.width_img_ri = width_img_ri;
    }

    public void setEventDisplay(EventDisplay eventDisplay) {
        this.eventDisplay = eventDisplay;
    }

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
    @NotifyChange("exonview")
    public void selectgriditem() {
        //TODO was passiert wenn auf item in gridlist geklickt wird
        // pattern
        // superposition
        // ....

        //ExonView by selected Transcript --> Gene
        renderedImage = data.renderImage(eventDisplay, height_img_ri, width_img_ri);
        selectedGene = data.getSelectedGene(eventDisplay);
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
