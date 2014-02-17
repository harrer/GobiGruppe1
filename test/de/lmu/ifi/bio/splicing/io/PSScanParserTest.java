package de.lmu.ifi.bio.splicing.io;

import junit.framework.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Carsten Uhlig on 16.02.14.
 */
public class PSScanParserTest extends TestCase {
    public void testReadPSScanFastaResultFile() throws Exception {
        String file = "res/prosite_scan_without_profiles_and_without_unimportant.fasta";
        PSScanParser.readPSScanFastaResultFile(file);
    }

    public void testName() throws Exception {
        String sample = ">ENST00000359541/621-635 : PS00678 WD_REPEATS_1";
        String regex = "^>(\\w+)/(\\d+)\\-(\\d+) : (\\w+).*";

        Matcher m = Pattern.compile(regex).matcher(sample);
        if (m.matches())
            System.out.println(m.group(1));
        else
            System.out.println("fuck");
    }
}
