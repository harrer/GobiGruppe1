package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.util.AminoAcidType;
import de.lmu.ifi.bio.splicing.util.GenomicUtils;

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
        if (stop < start)
            return "";
        byte[] b = new byte[0];
        try {
            RandomAccessFile raf = new RandomAccessFile(Setting.GTFDIRPATH + chromosome + ".fa", "r");
            raf.seek(0);
            int headeroffset = raf.readLine().length();
            start += (headeroffset + start / 60);
            stop += (headeroffset + stop / 60);
            b = new byte[(int) (stop - start + 1)];
            raf.seek(start);
            raf.read(b);
            raf.close();
        } catch (IOException e) {
            System.err.printf("[GenomeSequenceExtractor]: %s%s.fa%n", Setting.GTFDIRPATH, chromosome);
        }

        String tmp = new String(b);
        String[] tmp2 = tmp.split("\n");
        StringBuilder sb = new StringBuilder();
        for (String str : tmp2) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static String getProteinSequence(Transcript transcript) {
        String chromosome = Setting.dbq.getChrForTranscriptID(transcript.getTranscriptId());
        boolean strand = Setting.dbq.getStrandForTranscriptID(transcript.getTranscriptId());
        Iterator<Exon> itexon = transcript.getCds().iterator();
        StringBuilder sb = new StringBuilder();
        long start;
        long stop;
        Exon next;

        boolean first = true;

        while (itexon.hasNext()) {
            next = itexon.next();
            if (first) {
                start = next.getStart() + next.getFrame();
                first = false;
            } else
                start = next.getStart();
            stop = next.getStop();
            sb.append(getNucleotideSequence(chromosome, start, stop));
        }

        //Strand = '-'
        if (!strand) {
            sb = new StringBuilder(GenomicUtils.convertToStrandPlus(sb.toString()));
            sb.reverse();
        }

        StringBuilder sbaa = new StringBuilder();
        for (int i = 0; i < sb.length() - 2; i += 3) {
            sbaa.append(AminoAcidType.get(sb.substring(i, i + 3)));
        }

        return sbaa.toString();
    }

    public static String getProteinSequence(Transcript transcript, String chromosome, boolean strand) {
        Iterator<Exon> itexon = transcript.getCds().iterator();
        StringBuilder sb = new StringBuilder();
        long start;
        long stop;
        Exon next;
        boolean first = true;

        while (itexon.hasNext()) {
            next = itexon.next();
            if (first) {
                start = next.getStart() + next.getFrame();
                first = false;
            } else
                start = next.getStart();
            stop = next.getStop();
            sb.append(getNucleotideSequence(chromosome, start, stop));
        }

        //Strand = '-'
        if (!strand) {
            sb = new StringBuilder(GenomicUtils.convertToStrandPlus(sb.toString()));
            sb.reverse();
        }

        StringBuilder sbaa = new StringBuilder();
        for (int i = 0; i < sb.length() - 2; i += 3) {
            sbaa.append(AminoAcidType.get(sb.substring(i, i + 3)));
        }

        return sbaa.toString();
    }

    public static void writeAllSequencesToFile(String file) {
        Path p = Setting.FS.getPath(file);

        try {
            BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8);

            List<String> genestrings = Setting.dbq.findAllGenes();
            int size = genestrings.size();
            int counter = 0;
            Gene g;
            HashMap<String, Transcript> transenhashmap;
            for (String s : genestrings) {
                g = Setting.dbq.getGene(s);

                transenhashmap = g.getHashmap_transcriptid();
                for (Transcript t : transenhashmap.values()) {
                    bw.write(">" + t.getTranscriptId() + "\n" + getProteinSequence(t, g.getChromosome(), g.getStrand()) + "\n");
                }
                if (counter % (size / 144) == 0)
                    System.out.printf("Bereits %.2f %%%n", counter / (double) size * 100);
                counter++;
            }

            bw.close();
        } catch (IOException e) {
            System.err.printf("[GenomeSequenceExtractor]: Datei \"%s\" konnte nicht erstellt werden.%n", file);
            e.printStackTrace();
        }
    }

    public static void writeAllSequencesToFileFromGTFParser(String file, HashMap<String, Gene> hashMap) {

        Path p = Setting.FS.getPath(file);

        try {
            BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8);

            int size = hashMap.size();
            int counter = 0;
            HashMap<String, Transcript> transenhashmap;
            for (Gene g : hashMap.values()) {
                transenhashmap = g.getHashmap_transcriptid();
                for (Transcript t : transenhashmap.values()) {
                    bw.write(">" + t.getTranscriptId() + "\n" + getProteinSequence(t, g.getChromosome(), g.getStrand()) + "\n");
                }
                if (counter % (size / 144) == 0)
                        System.out.printf("Bereits %.2f %%%n", counter / (double) size * 100);
                counter++;
            }

            bw.close();
        } catch (IOException e) {
            System.err.printf("[GenomeSequenceExtractor]: Datei \"%s\" konnte nicht erstellt werden.%n", file);
            e.printStackTrace();
        }
    }

    public static void writeListOfTranscriptIds(String file, List<String> trlist) {
        Path p = Setting.FS.getPath(file);

        try {
            BufferedWriter bw = Files.newBufferedWriter(p, StandardCharsets.UTF_8);

            Transcript t;
            for (String s : trlist) {
                t = Setting.dbq.getTranscript(s);
                bw.write(String.format(">%s\n%s\n", t.getTranscriptId(), getProteinSequence(t)));
            }

            bw.close();
        } catch (IOException e) {
            System.err.printf("[GenomeSequenceExtractor]: Datei \"%s\" konnte nicht erstellt werden.%n", file);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String file = args[0];
        writeAllSequencesToFile(file);
    }
}
