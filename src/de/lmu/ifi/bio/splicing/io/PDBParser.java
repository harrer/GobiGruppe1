package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.structures.PDBData;
import de.lmu.ifi.bio.splicing.util.AAConversion;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by schmidtju on 13.02.14.
 */
public class PDBParser {
    public static String getPDBFile(String pdbId){
        List<String> atoms = new ArrayList<>();
        List<String> atomType = new ArrayList<>();
        List<Character> chain = new ArrayList<>();
        StringBuilder sequence = new StringBuilder();
        int CAcount = 0;
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(Setting.PDBREPCCHAINSDIR + pdbId + ".pdb"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ATOM")) {
                    atoms.add(line);
                    atomType.add(line.substring(13, 16));
                    chain.add(line.charAt(21));
                    if (line.substring(13, 15).equalsIgnoreCase("CA")) {
                        CAcount++;
                        sequence.append(AAConversion.getOneLetter(line
                                .substring(17, 20)));
                    }
                }
            }
            reader.close();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        double[][] coordinates = new double[atoms.size()][3];
        for (int i = 0; i < atoms.size(); i++) {
            double x = Double.parseDouble(atoms.get(i).substring(30, 38));
            double y = Double.parseDouble(atoms.get(i).substring(38, 46));
            double z = Double.parseDouble(atoms.get(i).substring(46, Math.min(55, atoms.get(i).length())));
            coordinates[i] = new double[] { x, y, z };
        }
        return new PDBData(pdbId, sequence.toString(), coordinates, atomType, chain);
    }
}
