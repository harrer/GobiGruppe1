package de.lmu.ifi.bio.splicing.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.HashMap;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBUpdate;

/**
 * Created by uhligc on 12.02.14.
 */
public class GTFParser {

    public static final FileSystem FS = Setting.FS;
    private static HashMap<String, Gene> genes = new HashMap<>();
    private static Gene curGene;
    private static Transcript curTranscript;

    public static void addExon(String geneid, String transcriptid, String proteinid, String chr, boolean strand, long start, long stop, int frame) {
        if (curGene == null || !curGene.getGeneId().equals(geneid)) {

            if (!genes.containsKey(geneid)) {
                curGene = new Gene(geneid, chr, strand);
                genes.put(geneid, curGene);
            } else {
                curGene = genes.get(geneid);
            }
        }

        if (curTranscript == null || !curTranscript.getTranscriptId().equals(transcriptid)) {
            if (curGene.getTranscriptByTranscriptId(transcriptid) == null) {
                curTranscript = new Transcript(transcriptid, proteinid);
                curGene.addTranscript(curTranscript);
            } else {
                curTranscript = (Transcript) curGene.getTranscriptByTranscriptId(transcriptid);
            }
        }

        //Exon wird nicht überprüft ob es schon vorkommt wenn ganz sicher dann hier implementieren
        //POSSIBLE BUG Exon not ... sth
        curTranscript.addExon(new Exon(start, stop, frame));
    }

    public void addGenes(DBUpdate dbu) {
        try {

            BufferedReader reader = null;
            curGene = null;
            curTranscript = null;
            reader = Files.newBufferedReader(Setting.GTFPATH,
                    StandardCharsets.UTF_8);

            String regexdelimit = "\\s+|;\\s*";
            String regexchromosome = "^(X|Y|\\d+).*";
            String regexchr = "^.*chromosome\\.(\\d+).*$";

            String line;
            String[] pieces;
            String seqname;
            String protein_id = null;
            String source;
            String feature;
            int start;
            int stop;
            int frame;
            String strand;
            boolean strandb;
            String gene_id = null;
            // transcript_id = id für sequenz
            String transcript_id = null;

            while ((line = reader.readLine()) != null) {
                // split
                pieces = line.split(regexdelimit);
                //falls nicht chromosome enthält.. egal
                //TODO Kannst anmachen um nur 1-22 oder x oder y chromosome zu catchen
//                if (!pieces[0].matches(regexchromosome)) {
//                    continue;
//                }

                if (!pieces[2].equals("CDS")) {
                    continue;
                }
                // save pieces
                seqname = pieces[0];
                source = pieces[1];
                feature = pieces[2];
                start = Integer.parseInt(pieces[3]);
                stop = Integer.parseInt(pieces[4]);
                strand = pieces[6];
                strandb = strand.equals("+");
                strand = pieces[6];
                frame = Integer.parseInt(pieces[7]);

                for (int i = 8; i < pieces.length; i += 2) {
                    switch (pieces[i]) {
                        case "gene_id":
                            gene_id = pieces[i + 1].replace("\"", "");
                            break;
                        case "transcript_id":
                            transcript_id = pieces[i + 1].replace("\"", "");
                            break;
                        case "protein_id":
                            protein_id = pieces[i + 1].replace("\"", "");
                            break;
                        default:
//                      System.err.println("fehlende Eigenschaft gefunden. Bitte beheben!");
                            break;
                    }
                }
                if (protein_id != null) {
                    addExon(gene_id, transcript_id, protein_id, seqname, strandb, start, stop, frame);
                }
                protein_id = null;
            }
            reader.close();
            saveToDatabase(dbu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addGenes(String file) {
        try {
            BufferedReader reader = null;
            curGene = null;
            curTranscript = null;
            reader = Files.newBufferedReader(Setting.GTFPATH,
                    StandardCharsets.UTF_8);

            String regexdelimit = "\\s+|;\\s*";
            String regexchromosome = "^(X|Y|\\d+).*";
            String regexchr = "^.*chromosome\\.(\\d+).*$";

            String line;
            String[] pieces;
            String seqname;
            String protein_id = null;
            String source;
            String feature;
            int start;
            int stop;
            int frame;
            String strand;
            boolean strandb;
            String gene_id = null;
            // transcript_id = id für sequenz
            String transcript_id = null;

            while ((line = reader.readLine()) != null) {
                // split
                pieces = line.split(regexdelimit);
                //falls nicht chromosome enthält.. egal
                //TODO Kannst anmachen um nur 1-22 oder x oder y chromosome zu catchen
//                if (!pieces[0].matches(regexchromosome)) {
//                    continue;
//                }

                if (!pieces[2].equals("CDS")) {
                    continue;
                }
                // save pieces
                seqname = pieces[0];
                source = pieces[1];
                feature = pieces[2];
                start = Integer.parseInt(pieces[3]);
                stop = Integer.parseInt(pieces[4]);
                strand = pieces[6];
                strandb = strand.equals("+");
                strand = pieces[6];
                frame = Integer.parseInt(pieces[7]);

                for (int i = 8; i < pieces.length; i += 2) {
                    switch (pieces[i]) {
                        case "gene_id":
                            gene_id = pieces[i + 1].replace("\"", "");
                            break;
                        case "transcript_id":
                            transcript_id = pieces[i + 1].replace("\"", "");
                            break;
                        case "protein_id":
                            protein_id = pieces[i + 1].replace("\"", "");
                            break;
                        default:
//                      System.err.println("fehlende Eigenschaft gefunden. Bitte beheben!");
                            break;
                    }
                }
                if (protein_id != null) {
                    addExon(gene_id, transcript_id, protein_id, seqname, strandb, start, stop, frame);
                }
                protein_id = null;
            }
            GenomeSequenceExtractor.writeAllSequencesToFile(file);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToDatabase(DBUpdate dbu) {
        for (Gene gene : genes.values()) {
            dbu.insertGene(gene);
        }
    }

    public void printString() {
        System.out.println(genes.toString());
    }

    public int countGenes() {
        return genes.size();
    }

    public static void main(String[] args) {
        addGenes(args[0]);
    }
}
