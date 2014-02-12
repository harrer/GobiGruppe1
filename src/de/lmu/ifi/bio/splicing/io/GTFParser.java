package de.lmu.ifi.bio.splicing.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBUpdate;

/**
 * Created by uhligc on 12.02.14.
 */
public class GTFParser {

    public static final FileSystem FS = Setting.FS;
    public static final HashMap<String, Gene> genes = new HashMap<>();

    public void addGene(Gene g) {
        if (genes.containsKey(g.getGeneId())) {
            Gene tmpgene = genes.get(g.getGeneId());

        }
    }

    public void addGenes() {
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(Setting.GTFPATH,
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        int end;
        int frame;
        String strand;
        String gene_id = null;
        // transcript_id = id für sequenz
        String transcript_id = null;

        while ((line = reader.readLine()) != null) {
            // split
            pieces = line.split(regexdelimit);
            //falls nicht chromosome enthält.. egal
            if (!pieces[0].matches(regexchromosome)) {
                continue;
            }

            if (!pieces[2].equals("CDS")) {
                continue;
            }

            // save pieces
            seqname = pieces[0];
            source = pieces[1];
            feature = pieces[2];
            start = Integer.parseInt(pieces[3]);
            end = Integer.parseInt(pieces[4]);
            // TODO check for available strand inputs -> evtl als Character oder
            // Integer oder boolean
            strand = pieces[6];

            // TODO check for available strand inputs -> evtl als Character oder
            // Integer oder boolean
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

                genes.addGene(protein_id, gene_id, transcript_id, seqname, strand, start, end, frame);
            }
            protein_id = null;
        }
        reader.close();

        // save in database
        // TODO
//            System.out.print("");
        // reset values
//        frame = -Integer.MAX_VALUE;
//        score = -Integer.MAX_VALUE;

    }
}
