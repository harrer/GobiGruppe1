package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import de.lmu.ifi.bio.splicing.zkoss.entity.ModelSequenceEntity;
import de.lmu.ifi.bio.splicing.zkoss.entity.SequenceEntity;
import de.lmu.ifi.bio.splicing.zkoss.entity.SpliceEventFilter;

import java.awt.image.RenderedImage;
import java.util.List;

/**
 * Created by Carsten Uhlig on 13.02.14.
 */
public interface Data {
    public List<String> findAll();

    public List<String> search(String keyword);

    public List<EventDisplay> select(List<String> keylist);

    public List<EventDisplay> filter(SpliceEventFilter sef);

    public RenderedImage renderImage(EventDisplay eventDisplay, int heigth, int width);

    public SequenceEntity prepareSequences(EventDisplay eventDisplay);

    public Gene getSelectedGene(EventDisplay eventDisplay);
    
    public ModelSequenceEntity getModel(EventDisplay eventDisplay);
}
