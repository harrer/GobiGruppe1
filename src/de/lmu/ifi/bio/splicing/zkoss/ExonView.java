package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.zkoss.entity.EventDisplay;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Init;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 * Created by Carsten Uhlig on 16.02.14.
 */
public class ExonView {
    Gene gene;
    long start, length;
    int width, height, size, lineHeight;

    private void calcOverallLength() {
        long stop = 0;
        start = Long.MAX_VALUE;
        for (Transcript transcript : gene.getHashmap_transcriptid().values()) {
            start = Math.min(start, transcript.getCds().get(0).getStart());
            stop = Math.max(stop, transcript.getCds().get(transcript.getCds().size() - 1).getStop());
        }
        length = stop - start + 1;
    }

    @Init
    public void init(@BindingParam("event") EventDisplay eventDisplay, @BindingParam("w") int width, @BindingParam("h") int height) {
        this.gene = Setting.dbq.getGeneForTranscriptID(eventDisplay.getI1());
        this.width = width;
        this.height = height;
        calcOverallLength();
        size = gene.getHashmap_transcriptid().size();
        lineHeight = height / (size + 1);
    }

    public ExonView(Gene gene, int width, int height) {
        this.gene = gene;
        this.width = width;
        this.height = height;
        calcOverallLength();
        size = gene.getHashmap_transcriptid().size();
        lineHeight = height / (size + 1);
    }

    public RenderedImage renderExonView() {
        BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics2D(bufferedImage);
        int line = 0;
        for (Transcript transcript : gene.getHashmap_transcriptid().values()) {
            g.setColor(Color.BLACK);
            g.drawString(transcript.getTranscriptId(), 0, lineHeight / 4 + lineHeight * line);
            boolean first = true;
            int cur = 0;
            if (gene.getStrand()) {
                for (Exon exon : transcript.getCds()) {
                    if (first) {
                        first = false;
                    } else {
                        g.setColor(Color.BLACK);
                        g.drawLine(cur, lineHeight / 2 + lineHeight * line, (int) (((exon.getStart() - start) * width) / length - 1), lineHeight / 2 + lineHeight * line);
                    }
                    g.setColor(Color.RED);
                    g.fillRoundRect((int) (((exon.getStart() - start) * width) / length), lineHeight / 4 + lineHeight * line, (int) (((exon.getStop() - exon.getStart()) * width) / length) + 1, lineHeight / 2 + 1, 10, 10);
                    cur = (int) (((exon.getStop() - start) * width) / length);
                }
            } else {
                for (Exon exon : transcript.getCds()) {
                    if (first) {
                        first = false;
                    } else {
                        g.setColor(Color.BLACK);
                        g.drawLine(width - cur, width - lineHeight / 2 + lineHeight * line, width - (int) (((exon.getStart() - start) * width) / length - 1), width - lineHeight / 2 + lineHeight * line);
                    }
                    g.setColor(Color.RED);
                    g.fillRoundRect(width -  (int) (((exon.getStart() - start) * width) / length), width - lineHeight / 4 + lineHeight * line, width - (int) (((exon.getStop() - exon.getStart()) * width) / length) + 1, width - lineHeight / 2 + 1, 10, 10);
                    cur = width - (int) (((exon.getStop() - start) * width) / length);
                }
            }
            line++;

        }
        g.dispose();
        return bufferedImage;
    }

    private Graphics2D createGraphics2D(BufferedImage bufferedImage) {
        Graphics2D g = bufferedImage.createGraphics();
        g.setBackground(Color.WHITE);
        g.clearRect(0, 0, width, height);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}
