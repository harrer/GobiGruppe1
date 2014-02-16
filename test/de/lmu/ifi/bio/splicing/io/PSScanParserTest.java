package de.lmu.ifi.bio.splicing.io;

import junit.framework.TestCase;

/**
 * Created by Carsten Uhlig on 16.02.14.
 */
public class PSScanParserTest extends TestCase {
    public void testReadPSScanFastaResultFile() throws Exception {
        String file = "/Volumes/SSD/git/gobi2/res/psscan.fasta";
        PSScanParser.readPSScanFastaResultFile(file);
    }
}
