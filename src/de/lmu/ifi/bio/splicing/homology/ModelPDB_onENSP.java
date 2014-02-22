package de.lmu.ifi.bio.splicing.homology;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.io.PDBParser;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.structures.PDBData;
import de.lmu.ifi.bio.splicing.structures.mapping.Model;
import de.lmu.ifi.bio.splicing.superimpose.Superposition;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author harrert
 */
public class ModelPDB_onENSP {

    private final DBQuery dbq;
    private HashMap<String, String> pdbSequences;
    private final SingleGotoh gotoh;
    private final Superposition superposition;
    private HashMap<String, Integer> enstSequnces;

    public ModelPDB_onENSP() {
        dbq = new DBQuery();
        pdbSequences = new HashMap<>();//readPDBseqlib("/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/pdb.seqlib");
        enstSequnces = new HashMap<>();
        this.gotoh = new SingleGotoh();
        superposition = new Superposition();
    }

    private HashMap<String, String> readPDBseqlib(String file) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        while ((line = br.readLine()) != null) {
            String[] split = line.split(":");
            map.put(split[0], split[1]);
        }
        br.close();
        return map;
    }
    
    private String[] getModelSequencesForENSP(String ensp_id) {
        Object[] result = null;
        try {
            String query = "select pdbId from transcript_has_pdbs where transcriptId = '" + ensp_id + "'";
            result = dbq.db.select_oneColumn(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        String[] s = new String[result.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (String) result[i];
        }
        return s;
    }

    //returns an array of PDBids that are modelable on the given ENST transcript
    private String[] getModelSequences(String ENST_id) {
        String ensp_id = dbq.db.getEnsp(ENST_id);//(String) dbq.db.select_oneColumn("select proteinid from Transcript where transcriptid = '" + ENST_id + "'")[0];

//        ArrayList<String> result = null;
//        try {
//            String query = "select pdbId from transcript_has_pdbs where transcriptId = '" + ensp_id + "'";
//            result = dbq.db.//select_oneColumn(query);
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        String[] s = new String[result.length];
//        for (int i = 0; i < s.length; i++) {
//            s[i] = (String) result[i];
//        }
        return dbq.db.getPDBID(ensp_id).toArray(new String[0]);
    }

    private ArrayList<String[]> alignPDBs_onENSP(String ENST_id, String[] seq, double coverage, int longerThan, double seqIdentity) {
        ArrayList<String[]> alignments = new ArrayList<>();
        String seq1 = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscript(ENST_id));
        enstSequnces.put(ENST_id, seq1.length());
        if(seq1.length() > 8000){return alignments;}
        gotoh.setSeq1(seq1);
        for (String PDBid : seq) {
            String str;
            if (pdbSequences.containsKey(PDBid)) {
                str = pdbSequences.get(PDBid);
            } else {
                str = PDBParser.getPDBSequence(PDBid);
                pdbSequences.put(PDBid, str);
            }
            if(str.length() > 8000){continue;}
            gotoh.setSeq2(str);
            String[] ali = gotoh.backtrackingLocal(gotoh.fillMatrixLocal());
            if (gotoh.sequenceIdentity(ali) >= seqIdentity && gotoh.coverage(ali, longerThan, coverage)) {//if alignment is significant given the 3 paramsz
                alignments.add(new String[]{ali[0], ali[1], PDBid});
            }
        }
        return alignments;
    }

    private ArrayList<Model> modelAlignmentsOnProtein(ArrayList<String[]> alignments, String ENST_id) {
        ArrayList<Model> models = new ArrayList<>();//Object[7]: int ENSPstart, int ENSPend, String pdbId, int PDBstart, int PDBend, double seqIdentity HashMap<int,int> alignedPos
        for (String[] ali : alignments) {//iterate over all alignements ENSP -> PDB
            int[] ali_StartEnd = SingleGotoh.getAli_StartEnd(ali);
            int enspStart = -1, pdbStart = -1;
            for (int i = 0; i <= ali_StartEnd[0]; i++) {
                if (ali[0].charAt(i) != '-') {
                    enspStart++;
                }
                if (ali[1].charAt(i) != '-') {
                    pdbStart++;
                }
            }
            int enspEnd = enstSequnces.get(ENST_id), pdbEnd = pdbSequences.get(ali[2]).length();
            for (int i = ali[0].length() - 1; i >= ali_StartEnd[1]; i--) {
                if (ali[0].charAt(i) != '-') {
                    enspEnd--;
                }
                if (ali[1].charAt(i) != '-') {
                    pdbEnd--;
                }
            }
            HashMap<Integer, Integer> alignedPos = new HashMap<>();
            int ensp = enspStart, pdb = pdbStart;
            for (int i = ali_StartEnd[0]; i <= ali_StartEnd[1]; i++) {
                boolean noUpperDash = ali[0].charAt(i) != '-', noLowerDash = ali[1].charAt(i) != '-';
                if (noUpperDash && noLowerDash) {
                    alignedPos.put(ensp, pdb);
                }
                if (noUpperDash) {
                    ensp++;
                }
                if (noLowerDash) {
                    pdb++;
                }
            }
            //models.add(new Object[]{enspStart, enspEnd, ali[2], pdbStart, pdbEnd, SingleGotoh.sequenceIdentity(ali)});
            models.add(new Model(ENST_id, enspStart, enspEnd, ali[2], pdbStart, pdbEnd, alignedPos, SingleGotoh.sequenceIdentity(ali)));
        }
        return models;
    }
    
    public ArrayList<Model> getModelsForENSP(String enspId, double coverage, int longerThan, double seqIdentity){
        String[] pdbs = getModelSequencesForENSP(enspId);
        ArrayList<String[]> alignments = alignPDBs_onENSP(enspId, pdbs, 0.6, 60, 0.4);
        return modelAlignmentsOnProtein(alignments, enspId);
    }

    public ArrayList<Model> getModelsForENST(String enstId) {
        String[] pdbs = getModelSequences(enstId);
        ArrayList<String[]> alignments = alignPDBs_onENSP(enstId, pdbs, 0.6, 60, 0.4);
        return modelAlignmentsOnProtein(alignments, enstId);
    }

    public ArrayList<Model> getModelsForENST(String enstId, double coverage, int longerThan, double seqIdentity) {
        String[] pdbs = getModelSequences(enstId);
        ArrayList<String[]> alignments = alignPDBs_onENSP(enstId, pdbs, coverage, longerThan, seqIdentity);
        return modelAlignmentsOnProtein(alignments, enstId);
    }

    public String displayModels(ArrayList<Model> models, String ENST_id) {
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscript(ENST_id));
        StringBuilder sb = new StringBuilder("        " + proteinSeq + '\n');
        for (Model model : models) {
            sb.append(model.getPdbId()).append(": ");
            HashMap<Integer, Integer> aligned = model.getAligned();
            for (int i = 0; i < proteinSeq.length(); i++) {
                char append = (i >= model.getEnspStart() && i <= model.getEnspStop() && aligned.containsKey(i)) ? '+' : '\'';//&& model.getAligned().containsKey(i)
                sb.append(append);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public PDBData modelToStructure(Model model) {
        PDBData pdb = PDBParser.getPDBFile(model.getPdbId());
        String sequence = pdb.getSequence();
        StringBuilder sb = new StringBuilder();
        double[][] coordinates = pdb.getCACoordinates();
        ArrayList<double[]> coord = new ArrayList<>();
        List<String> atoms = new ArrayList<>();
        List<Character> chain = pdb.getChain();
        List<Character> chain_model = new ArrayList<>();
        //filter modelled structure
        HashMap<Integer, Integer> aligned = model.getAligned();
        for (int i = model.getPdbStart(); i <= model.getPdbStop(); i++) {
            if (aligned.containsValue(i)) {
                sb.append(sequence.charAt(i));
                coord.add(coordinates[i]);
                atoms.add("CA");
                chain_model.add(chain.get(i));
            }
        }
        return new PDBData(pdb.getPdbId(), sequence, coord.toArray(new double[][]{}), atoms, chain_model);
    }

    public Overlap findModelOverlap(Model m1, Model m2) {
        if (m1.getEnspStart() > m2.getEnspStop() || m1.getEnspStop() < m2.getEnspStart()) {//models do not overlap
            return null;
        } else {
            if (m1.getEnspStart() <= m2.getEnspStart() && m1.getEnspStop() >= m2.getEnspStop()) {//m2 is "included" in m1
                return new Overlap(m1, m2, OverlapType.m2_included_in_m1, m2.getEnspStart(), m2.getEnspStop(), m2.getEnspStart(), m2.getEnspStop());
            } else if (m2.getEnspStart() <= m1.getEnspStart() && m2.getEnspStop() >= m1.getEnspStop()) {//m1 is "included" in m2
                return new Overlap(m1, m2, OverlapType.m1_included_in_m2, m1.getEnspStart(), m1.getEnspStop(), m1.getEnspStart(), m1.getEnspStop());
            } else if (m2.getEnspStop() > m1.getEnspStop()) {//partly overlap of m1(end), m2(start)
                return new Overlap(m1, m2, OverlapType.m1_end_m2_start_overlap, m2.getEnspStart(), m1.getEnspStop(), m2.getEnspStart(), m1.getEnspStop());
            } else if (m2.getEnspStart() < m1.getEnspStart()) {//partly overlap of m1(start), m2(end)
                return new Overlap(m1, m2, OverlapType.m1_start_m2_end_overlap, m1.getEnspStart(), m2.getEnspStop(), m1.getEnspStart(), m2.getEnspStop());
            } else {
                System.out.println("### overlap available, but not found! ###");
                return null;//should not happen
            }
        }
    }
    
    public ArrayList<Overlap> findOverlapForAllModels(ArrayList<Model> models){
        ArrayList<Overlap> overlaps = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            for (int j = i; j < models.size(); j++) {
                Overlap overlap = findModelOverlap(models.get(i), models.get(j));
                if(overlap != null && j != i){
                    overlaps.add(overlap);
                }
            }
        }
        return overlaps;
    }

    /**
     * @param overlap the overlap
     * @return double[] contains the rmsd and the gtd-ts sccores
     */
    public double[] superimposeOverlap(Overlap overlap) {
        Model m1 = overlap.getModel1(), m2 = overlap.getModel2();
        int start = -1, stop = -1;
        if(overlap.getType().equals(OverlapType.m1_included_in_m2)){
            start = m1.getEnspStart();
            stop = m1.getEnspStop();
        }
        else if(overlap.getType().equals(OverlapType.m2_included_in_m1)){
            start = m2.getEnspStart();
            stop = m2.getEnspStop();
        }
        else if(overlap.getType().equals(OverlapType.m1_end_m2_start_overlap)){
            start = m1.getEnspStop();
            stop = m2.getEnspStart();
        }
        else if(overlap.getType().equals(OverlapType.m1_start_m2_end_overlap)){
            start = m1.getEnspStart();
            stop = m2.getEnspStop();
        }
        else{System.out.println("no overlap type matching");}//should not happen
        ArrayList<double[]> coord1 = new ArrayList<>(), coord2 = new ArrayList<>();
        double[][] pdb1 = PDBParser.getPDBFile(m1.getPdbId()).getCACoordinates(), pdb2 = PDBParser.getPDBFile(m2.getPdbId()).getCACoordinates();//the CA-coordinates of the respective PDB files
        HashMap<Integer, Integer> aligned1 = m1.getAligned(), aligned2 = m2.getAligned();
        for (int i = start; i < stop; i++) {
            if(aligned1.containsKey(i) && aligned2.containsKey(i)){
                coord1.add(pdb1[aligned1.get(i)]);
                coord2.add(pdb2[aligned2.get(i)]);
            }
        }
        assert(coord1.size() == coord2.size());// must be the same length
        
        Object[] sp = null;
        try {
            sp = superposition.superimpose(new DenseDoubleMatrix2D(coord1.toArray(new double[][]{})), new DenseDoubleMatrix2D(coord2.toArray(new double[][]{})), null);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("SVD");
        }
        
        return new double[]{(double) sp[2], (double) sp[3]};
    }
    
    public void run(String fileOut, double coverage, int longerThan, double seqIdentity) throws IOException{
        PrintWriter writer = new PrintWriter(fileOut);
        Object[] templates = null;
        writer.write("rmsd\tgtd-ts\tpdb1\tpdb2\n");
        try {
            templates = dbq.db.select_oneColumn("select distinct transcriptid from transcript_has_pdbs");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        int templateCount = 1, nullpointer = 0, overlapCount = 0;
        for (int i=0; i<100; i++){//(Object object : templates) {
            System.out.println(templateCount+" templates processed");templateCount++;
            String enst = "";
            try {
                enst = (String) dbq.db.select_oneColumn("select transcriptid from Transcript where proteinid = '"+ (String) templates[i] +"'")[0];
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            ArrayList<Model> models = getModelsForENST(enst, coverage, longerThan, seqIdentity);
            ArrayList<Overlap> overlaps = findOverlapForAllModels(models);
            for (Overlap overlap : overlaps) {
                overlapCount++;
                try {
                    double[] scores = superimposeOverlap(overlap);
                    writer.printf("%f\t%f\t%s\t%s\t%s\n", scores[0], scores[1], overlap.getModel1().getPdbId(), overlap.getModel2().getPdbId(), overlap.getModel1().getEnstId());
                } catch (NullPointerException e) {
                    nullpointer++;
                }
            }
        }
        writer.close();
        System.out.println("superPos failure: "+nullpointer+" on "+overlapCount+" overlaps for "+templateCount+" templates");
    }
    
    public static void main(String[] args) throws SQLException, IOException {
        ModelPDB_onENSP m = new ModelPDB_onENSP();
//        ArrayList<Model> models = m.getModelsForENST("ENST00000380952");
//        System.out.println(m.displayModels(models, "ENST00000380952"));
//        //PDBData pdb = m.modelToStructure(models.get(0));
//        ArrayList<Overlap> overlaps = m.findOverlapForAllModels(models);
//        //double[] sPose = m.superimposeOverlap(overlap);
//        System.out.println("");
        m.run("/tmp/a", 0.6, 60, 0.4);//"/tmp/DISTINCT_superimposeAllModelsSSSSSSSSSSSSSS.txt"
    }

}
