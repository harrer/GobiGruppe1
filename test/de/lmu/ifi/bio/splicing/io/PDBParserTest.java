package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.structures.PDBData;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by schmidtju on 13.02.14.
 */
public class PDBParserTest extends TestCase {

    @Test
    public void testAddGenes() throws Exception {
        PDBData pdb = PDBParser.getPDBFile("1a0a.A");
        PDBData pdb2 = PDBParser.getPDBFile("1a0a.A");
    }


}
