package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by uhligc on 14.02.14.
 */
public class GenomeSequenceExtractor {
    public static String getNucleotideSequence(String chromosome, long start, long stop) {
        byte[] b = new byte[0];
        try {
            RandomAccessFile raf = new RandomAccessFile(Setting.GTFDIR + chromosome + ".fa", "r");
            raf.seek(0);
            int headeroffset = raf.readLine().length();
            start += (headeroffset + start / 60);
            stop += (headeroffset + stop / 60);
            b = new byte[(int) (stop - start + 1)];
            raf.seek(start);
            raf.read(b);
            raf.close();
        } catch (IOException e) {
            System.err.println("Datei konnte nicht gefunden oder gelesen werden. [GenomeSequenceExtractor]");
            e.printStackTrace();
        }

        String tmp = new String(b);
        String[] tmp2 = tmp.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String str : tmp2) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static String getProteinSequence(Transcript transcript, String chromosome) {
        Iterator<Exon> itexon = transcript.getCds().iterator();
        StringBuilder sb = new StringBuilder();
        long start;
        long stop;
        int length;
        int extra;

        while (itexon.hasNext()) {
            Exon next = itexon.next();
            start = next.getStart() + next.getFrame();
            length = (int) (next.getStop() - start);
            extra = length % 3;
            stop = next.getStop() - extra;
            sb.append(getNucleotideSequence(chromosome, start, stop));
        }

        return sb.toString();
    }

    public static void writeAllSequencesToFile(String file) {
        Path p = Setting.FS.getPath(file);

        try {
            BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8);

            List<String> genestrings = Setting.dbq.findAllGenes();
            Gene g;
            HashMap<String, Transcript> transenhashmap;
            for (String s : genestrings) {
                g = Setting.dbq.getGene(s);
                transenhashmap = g.getHashmap_transcriptid();
                for (Transcript t : transenhashmap.values()) {
                    bw.write(">" + t.getTranscriptId() + "\n" + getProteinSequence(t, g.getChromosome() + "\n"));
                }
            }
            bw.close();
        } catch (IOException e) {
            System.err.println("Datei \"" + file + "\" konnte nicht erstellt werden. [GenomeSequenceExtractor]");
            e.printStackTrace();
        }
    }

    public static void writeListOfTranscriptIds(String file, List<String> trlist) {
        Path p = Setting.FS.getPath(file);

        try {
            BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8);

            Transcript t;
            HashMap<String, Transcript> transenhashmap;
            for (String s : trlist) {
                t = Setting.dbq.getTranscript(s);
                bw.write(">" + t.getTranscriptId() + "\n" + getProteinSequence(t, /** TODO chromosome zu transcript id finden **/"FAIL" + "\n"));
            }
            bw.close();
        } catch (IOException e) {
            System.err.println("Datei \"" + file + "\" konnte nicht erstellt werden. [GenomeSequenceExtractor]");
            e.printStackTrace();
        }
    }
}
