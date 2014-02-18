package de.lmu.ifi.bio.splicing.zkoss;

import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 * Created by schmidtju on 16.02.14.
 */
public class ExonView {
    Gene gene;
    long length;
    int width, height, size, lineHeight;

    private void calcOverallLength() {
        long start = Long.MAX_VALUE, stop = 0;
        for (Transcript transcript : gene.getHashmap_transcriptid().values()) {
            start = Math.min(start, transcript.getCds().get(0).getStart());
            stop = Math.min(stop, transcript.getCds().get(transcript.getCds().size() - 1).getStop());
        }
        length = stop - start + 1;
    }

    public ExonView(Gene gene, int width, int height) {
        this.gene = gene;
        this.width = width;
        this.height = height;
        calcOverallLength();
        size = gene.getHashmap_transcriptid().size() + 1;
        lineHeight = height / (size + 1);
    }

    public RenderedImage renderExonView() {
        BufferedImage bufferedImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = createGraphics2D(bufferedImage);
        for (Transcript transcript : gene.getHashmap_transcriptid().values()) {
            //print transcript.getTranscriptId()
            boolean first = true;
            int cur = 0;
            int line = 0;
            for (Exon exon : transcript.getCds()) {
                g.setColor(Color.BLACK);
                if (!first) {
                    g.drawLine(cur, (int) (exon.getStart() * size / length), lineHeight / 2 + line * lineHeight, lineHeight / 2 + line * lineHeight);
                } else {
                    first = false;
                }
                g.setColor(Color.RED);
                g.fillRect((int) (exon.getStart() * size / length), (int) (exon.getStop() * size / length), lineHeight / 4 + line * lineHeight, lineHeight / 4 * 3 + line * lineHeight);
                cur = (int) (exon.getStop() * size / length);
                line++;
            }
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
}
