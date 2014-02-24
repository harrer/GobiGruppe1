package de.lmu.ifi.bio.splicing.stats;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Carsten Uhlig on 24.02.14.
 */
public class Pass {
    public static void main(String[] args) {
        HashMap<String, Geneprosite> genes = new HashMap<>();
        HashMap<String, Transcriptprosite> transcripts = new HashMap<>();
        HashSet<String> patternset = new HashSet<>();
        FileSystem fs = FileSystems.getDefault();
        Path p = fs.getPath(args[0]);
        Path p2 = fs.getPath(args[1]);
        try {
        BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8);
        String line ="";
        String[] split = null;
        Geneprosite genetmp = null;
            while ((line = br.readLine()) != null) {
                split = line.split(",");
                if (genes.containsKey(split[0]))
                    genetmp = genes.get(split[0]);
                else {
                    genetmp = new Geneprosite(split[0]);
                    genes.put(split[0], genetmp);
                }
                if (!patternset.contains(split[2]))
                    patternset.add(split[2]);

                genetmp.addPair(split[1],split[2]);
            }
            br.close();

            BufferedWriter wr = Files.newBufferedWriter(p2, StandardCharsets.UTF_8);
            for (String s : patternset) {
                int total = 0;
                int partly = 0;

                for (Geneprosite geneprosite: genes.values()) {
                    if (!geneprosite.hasPrositeid(s))
                        continue;
                    total += geneprosite.getTotal();
                    partly += geneprosite.getParts(s);
                }

                wr.write(String.format("%s %d %d%n", s, total, partly));
            }
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
