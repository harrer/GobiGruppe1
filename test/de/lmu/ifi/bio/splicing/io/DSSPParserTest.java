package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;
import junit.framework.TestCase;

/**
 * Created by schmidtju on 16.02.14.
 */
public class DSSPParserTest extends TestCase {
    public void testGetDSSPData() throws Exception {
        DSSPData d = DSSPParser.runDSSP("1a7i.A", Setting.PDBREPCCHAINSDIR);
        System.out.println();
    }
}
