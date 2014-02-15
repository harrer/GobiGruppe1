package de.lmu.ifi.bio.splicing.io;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by uhligc on 15.02.14.
 */
public class PrositeMotifParserTest {

    @Test
    public void testInsert() throws Exception {
        String file = "/home/proj/biosoft/software/ps_scan/prosite.dat";
        PrositeMotifParser.readPrositeMotifFile(file);
    }

    @Test
    public void testPattern() throws Exception {
        String regexdelimit = "\\s+";
        String sample = "ID   KRINGLE_1; PATTERN.\n" +
                "AC   PS00021;\n" +
                "DT   APR-1990 (CREATED); SEP-2002 (DATA UPDATE); NOV-2013 (INFO UPDATE).\n" +
                "DE   Kringle domain signature.\n" +
                "PA   [FY]-C-[RH]-[NS]-x(7,8)-[WY]-C.\n" +
                "NR   /RELEASE=2013_12,541954;\n" +
                "NR   /TOTAL=216(95); /POSITIVE=214(93); /UNKNOWN=0(0); /FALSE_POS=2(2);\n" +
                "NR   /FALSE_NEG=1; /PARTIAL=1;\n" +
                "CC   /TAXO-RANGE=??E??; /MAX-REPEAT=38;\n" +
                "CC   /SITE=2,disulfide; /SITE=7,disulfide;\n" +
                "CC   /VERSION=1;\n" +
                "DR   P08519, APOA_HUMAN , T; P14417, APOA_MACMU , T; Q04962, FA12_CAVPO , T;\n" +
                "DR   P00748, FA12_HUMAN , T; Q80YC5, FA12_MOUSE , T; O97507, FA12_PIG   , T;\n" +
                "DR   D3ZTE0, FA12_RAT   , T; Q5E9Z2, HABP2_BOVIN, T; Q14520, HABP2_HUMAN, T;\n" +
                "DR   Q8K0D2, HABP2_MOUSE, T; Q6L711, HABP2_RAT  , T; Q6QNF4, HGFA_CANFA , T;\n" +
                "DR   Q04756, HGFA_HUMAN , T; Q9R098, HGFA_MOUSE , T; Q24K22, HGFL_BOVIN , T;\n" +
                "DR   P26927, HGFL_HUMAN , T; P26928, HGFL_MOUSE , T; Q76BS1, HGF_BOVIN  , T;\n" +
                "DR   Q867B7, HGF_CANFA  , T; Q9BH09, HGF_FELCA  , T; P14210, HGF_HUMAN  , T;\n" +
                "DR   Q08048, HGF_MOUSE  , T; P17945, HGF_RAT    , T; Q96MU8, KREM1_HUMAN, T;\n" +
                "DR   Q99N43, KREM1_MOUSE, T; Q924S4, KREM1_RAT  , T; Q90Y90, KREM1_XENLA, T;\n" +
                "DR   Q8NCW0, KREM2_HUMAN, T; Q8K1S7, KREM2_MOUSE, T; Q16609, LPAL2_HUMAN, T;\n" +
                "DR   Q2TV78, MST1L_HUMAN, T; Q8AXY6, MUSK_CHICK , T; Q5G270, NETR_GORGO , T;\n" +
                "DR   P56730, NETR_HUMAN , T; Q5G267, NETR_MACMU , T; O08762, NETR_MOUSE , T;\n" +
                "DR   Q5G268, NETR_NOMLE , T; Q5G271, NETR_PANTR , T; Q5G269, NETR_PONPY , T;\n" +
                "DR   G3V801, NETR_RAT   , T; Q5G265, NETR_SAGLB , T; Q5G266, NETR_TRAPH , T;\n" +
                "DR   Q1RMT9, P3IP1_BOVIN, T; Q7SXB3, P3IP1_DANRE, T; Q96FE7, P3IP1_HUMAN, T;\n" +
                "DR   Q7TMJ8, P3IP1_MOUSE, T; Q5RCS3, P3IP1_PONAB, T; Q56A20, P3IP1_RAT  , T;\n" +
                "DR   P06868, PLMN_BOVIN , T; P80009, PLMN_CANFA , T; Q7M323, PLMN_CAPHI , T;\n" +
                "DR   Q29485, PLMN_ERIEU , T; P80010, PLMN_HORSE , T; P00747, PLMN_HUMAN , T;\n" +
                "DR   O18783, PLMN_MACEU , T; P12545, PLMN_MACMU , T; P20918, PLMN_MOUSE , T;\n" +
                "DR   P33574, PLMN_PETMA , T; P06867, PLMN_PIG   , T; Q5R8X6, PLMN_PONAB , T;\n" +
                "DR   Q01177, PLMN_RAT   , T; P81286, PLMN_SHEEP , T; Q24488, ROR1_DROME , T;\n" +
                "DR   Q01973, ROR1_HUMAN , T; Q9Z139, ROR1_MOUSE , T; Q9V6K3, ROR2_DROME , T;\n" +
                "DR   Q01974, ROR2_HUMAN , T; Q9Z138, ROR2_MOUSE , T; P00735, THRB_BOVIN , T;\n" +
                "DR   P00734, THRB_HUMAN , T; P19221, THRB_MOUSE , T; Q19AZ8, THRB_PIG   , T;\n" +
                "DR   Q5R537, THRB_PONAB , T; P18292, THRB_RAT   , T; Q28198, TPA_BOVIN  , T;\n" +
                "DR   P00750, TPA_HUMAN  , T; P11214, TPA_MOUSE  , T; Q8SQ23, TPA_PIG    , T;\n" +
                "DR   Q5R8J0, TPA_PONAB  , T; P19637, TPA_RAT    , T; Q05589, UROK_BOVIN , T;\n" +
                "DR   P15120, UROK_CHICK , T; P00749, UROK_HUMAN , T; P06869, UROK_MOUSE , T;\n" +
                "DR   P16227, UROK_PAPCY , T; P04185, UROK_PIG   , T; Q5RF29, UROK_PONAB , T;\n" +
                "DR   Q8MHY7, UROK_RABIT , T; P29598, UROK_RAT   , T; P98119, URT1_DESRO , T;\n" +
                "DR   P15638, URT2_DESRO , T; P98121, URTB_DESRO , T; P49150, URTG_DESRO , T;\n" +
                "DR   P84122, THRB_SALSA , P;\n" +
                "DR   P98140, FA12_BOVIN , N;\n" +
                "DR   Q3YEG4, CO141_CONMI, F; P84811, PWAP_HALLA , F;\n" +
                "3D   1A0H; 1B2I; 1BHT; 1CEA; 1CEB; 1GMN; 1GMO; 1GP9; 1HPJ; 1HPK; 1I5K; 1I71;\n" +
                "3D   1JFN; 1KDU; 1KI0; 1KIV; 1KRN; 1NK1; 1NL1; 1NL2; 1PK2; 1PK4; 1PKR; 1PMK;\n" +
                "3D   1PML; 1TPK; 1URK; 2DOH; 2DOI; 2FD6; 2FEB; 2HPP; 2HPQ; 2I9A; 2I9B; 2K4R;\n" +
                "3D   2K51; 2KNF; 2L0S; 2PF1; 2PF2; 2PK4; 2QJ2; 2QJ4; 2SPT; 3BT1; 3BT2; 3E6P;\n" +
                "3D   3HN4; 3K65; 3KIV; 3LAQ; 3MKP; 3NXP; 3SP8; 3U73; 4A5T; 4DUR; 4DUU; 4HZH;\n" +
                "3D   4IUA; 4KIV; 5HPG;\n" +
                "DO   PDOC00020;\n" +
                "//";
        String[] lines = sample.split("\n");
        for (String line : lines) {
            String[] split = line.split(regexdelimit);
            for (String s : split) {
                System.out.print(s + "+++");
            }
            System.out.println();
        }
        Assert.assertEquals("PS00001",lines[1].split(regexdelimit)[1].replace(";",""));
    }
}
