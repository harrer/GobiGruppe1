package de.lmu.ifi.bio.splicing.structures.mapping;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.io.DSSPParser;
import de.lmu.ifi.bio.splicing.io.PDBParser;
import de.lmu.ifi.bio.splicing.structures.PDBData;
import junit.framework.TestCase;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by schmidtju on 18.02.14.
 */
public class DSSPTest extends TestCase {
    public void setUp() throws Exception {
        super.setUp();
    }


    public void testUpdateEventWithAccAndSS() throws Exception {
        HashMap<Integer, Integer> aligned = new HashMap<>();
        for(int i = 10; i < 479; i++){
            aligned.put(i, i - 10);
        }
//        Model model = new Model("1bj4.A", aligned);
//        Event event = new Event("ENST00000316694", "ENST00000582653", 166, 481, 'D');
//        int[] borders = model.getBoundaries(event);
//        DSSP.updateEventWithAccAndSS(model, event);
    }

    public void testCalculateSecondaryStructureDistribution() throws  Exception {
        File f = new File(Setting.DSSPDIR);
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".dssp");
            }
        });
        List<DSSPData> data = new LinkedList<>();
        for (File matchingFile : matchingFiles) {
            data.add(DSSPParser.parseDSSPFile(matchingFile, matchingFile.getName().substring(0, matchingFile.getName().lastIndexOf(".dssp"))));
//            System.out.println(matchingFile.getName().substring(0, matchingFile.getName().lastIndexOf(".dssp")));
        }
        int[] ss = DSSP.calcSecondaryStructureDistribution(data);
        for (int s : ss) {
            System.out.println(s);
        }
    }
}
