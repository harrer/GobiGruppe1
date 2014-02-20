package de.lmu.ifi.bio.splicing.homology;

import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.io.PDBParser;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.structures.PDBData;
import de.lmu.ifi.bio.splicing.structures.mapping.Model;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author harrert
 */
public class ModelPDB_onENSP {

    private DBQuery dbq;
    private HashMap<String, String> pdbSequences;

    public ModelPDB_onENSP() {
        dbq = new DBQuery();
        try {
            pdbSequences = readPDBseqlib("/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/pdb.seqlib");
        } catch (IOException ex) {
        }
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

    //returns an array of PDBids that are modelable on the given ENSP protein
    private String[] getModelSequences(String ENST_id) throws SQLException {
        String ensp_id = (String) dbq.db.select_oneColumn("select proteinid from Transcript where transcriptid = '"+ ENST_id +"'")[0];
        String query = "select pdbId from transcript_has_pdbs where transcriptId = '" + ensp_id + "'";
        Object[] result = dbq.db.select_oneColumn(query);
        String[] s = new String[result.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = (String) result[i];
        }
        return s;
    }

    private ArrayList<String[]> alignPDBs_onENSP(String ENST_id, String[] seq, double coverage, int longerThan, double seqIdentity) throws IOException {
        Transcript t = dbq.getTranscript(ENST_id);
        SingleGotoh gotoh = new SingleGotoh(GenomeSequenceExtractor.getProteinSequence(t), "");
        ArrayList<String[]> alignments = new ArrayList<>();
        for (String PDBid : seq) {
            gotoh.setSeq2(this.pdbSequences.get(PDBid));
            String[] ali = gotoh.backtrackingLocal(gotoh.fillMatrixLocal());
            if (gotoh.sequenceIdentity(ali) >= seqIdentity && gotoh.coverage(ali, longerThan, coverage)) {//if alignment is significant given the 3 paramsz
                alignments.add(new String[]{ali[0], ali[1], PDBid});
            }
        }
        return alignments;
    }

    private ArrayList<Model> modelAlignmentsOnProtein(ArrayList<String[]> alignments, String ENST_id) {
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscript(ENST_id));
        ArrayList<Model> models = new ArrayList<>();//Object[7]: int ENSPstart, int ENSPend, String pdbId, int PDBstart, int PDBend, double seqIdentity HashMap<int,int> alignedPos
        for (String[] ali : alignments) {//iterate over all alignements ENSP -> PDB
            int[] ali_StartEnd = SingleGotoh.getAli_StartEnd(ali);
            int enspStart = -1, pdbStart = -1;
            for (int i = 0; i <= ali_StartEnd[0]; i++) {
                if(ali[0].charAt(i) != '-'){enspStart++;}
                if(ali[1].charAt(i) != '-'){pdbStart++;}
            }
            int enspEnd = proteinSeq.length(), pdbEnd = this.pdbSequences.get(ali[2]).length();
            for (int i = ali[0].length()-1; i >= ali_StartEnd[1]; i--) {
                if(ali[0].charAt(i) != '-'){enspEnd--;}
                if(ali[1].charAt(i) != '-'){pdbEnd--;}
            }
            HashMap<Integer, Integer> alignedPos = new HashMap<>();
            int ensp=-1, pdb=-1;
            for (int i = ali_StartEnd[0]; i <= ali_StartEnd[1]; i++) {
                boolean noUpperDash = ali[0].charAt(i) != '-', noLowerDash = ali[1].charAt(i) != '-';
                if(noUpperDash){ensp++;}
                if(noLowerDash){pdb++;}
                if(noUpperDash && noLowerDash){
                    alignedPos.put(ensp, pdb);
                }
            }
            //models.add(new Object[]{enspStart, enspEnd, ali[2], pdbStart, pdbEnd, SingleGotoh.sequenceIdentity(ali)});
            models.add(new Model(ENST_id, enspStart, enspEnd, ali[2], pdbStart, pdbEnd, alignedPos, SingleGotoh.sequenceIdentity(ali)));
        }
        return models;
    }
    
    public ArrayList<Model> getModelsForENSP(String enstId) throws SQLException, IOException{
        String[] pdbs = getModelSequences(enstId);
        ArrayList<String[]> alignments = alignPDBs_onENSP(enstId, pdbs, 0.6, 60, 0.4);
        return modelAlignmentsOnProtein(alignments, enstId);
    }
    
    public ArrayList<Model> getModelsForENSP(String enstId, double coverage, int longerThan, double seqIdentity) throws SQLException, IOException{
        String[] pdbs = getModelSequences(enstId);
        ArrayList<String[]> alignments = alignPDBs_onENSP(enstId, pdbs, coverage, longerThan, seqIdentity);
        return modelAlignmentsOnProtein(alignments, enstId);
    }
    
    public String displayModels(ArrayList<Model> models, String ENST_id){
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(dbq.getTranscript(ENST_id));
        StringBuilder sb = new StringBuilder("        "+proteinSeq+'\n');
        for(Model model : models){
            sb.append(model.getPdbId()).append(": ");
            HashMap<Integer, Integer> aligned = model.getAligned();
            for (int i = 0; i < proteinSeq.length(); i++) {
                char append = (i >= model.getEnspStart() && i<= model.getEnspStop() && aligned.containsKey(i-model.getEnspStart()))? '+' : '\'';//&& model.getAligned().containsKey(i)
                sb.append(append);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
    public PDBData modelToStructure(Model model){
        PDBData pdb = PDBParser.getPDBFile(model.getPdbId());
        String sequence = pdb.getSequence(); StringBuilder sb = new StringBuilder();
        double[][] coordinates = pdb.getCACoordinates(); ArrayList<double[]> coord = new ArrayList<>();
        List<String> atoms = new ArrayList<>();
        List<Character> chain = pdb.getChain(); List<Character> chain_model = new ArrayList<>();
        //filter modelled structure
        HashMap<Integer, Integer> aligned = model.getAligned();
        for (int i = model.getPdbStart(); i <= model.getPdbStop(); i++) {
            if(aligned.containsValue(i-model.getPdbStart())){
                sb.append(sequence.charAt(i));
                coord.add(coordinates[i]);
                atoms.add("CA");
                chain_model.add(chain.get(i));
            }
        }
        return new PDBData(pdb.getPdbId(), sequence, coord.toArray(new double[][]{}), atoms, chain_model);
    }
    
    public Overlap findModelOverlap(Model m1, Model m2){
        if(m1.getEnspStart() > m2.getEnspStop() || m1.getEnspStop() < m2.getEnspStart()){//models do not overlap
            return null;
        }
        else{
            if(m2.getEnspStart() < m1.getEnspStart()){//partly overlap of m1(start), m2(end)
                return new Overlap(m1.getPdbId(), m2.getPdbId(), "partly overlap of m1(start), m2(end)",m1.getEnspStart(), m2.getEnspStop(), m1.getEnspStart(), m2.getEnspStop());
            }
            else if(m2.getEnspStop() > m1.getEnspStop()){//partly overlap of m1(end), m2(start)
                return new Overlap(m1.getPdbId(), m2.getPdbId(), "partly overlap of m1(end), m2(start)", m2.getEnspStart(), m1.getEnspStop(), m2.getEnspStart(), m1.getEnspStop());
            }
            else if(m1.getEnspStart() <= m2.getEnspStart() && m1.getEnspStop() >= m2.getEnspStop()){//m2 is "included" in m1
                return new Overlap(m1.getPdbId(), m2.getPdbId(), "m2 is included in m1", m2.getEnspStart(), m2.getEnspStop(), m2.getEnspStart(), m2.getEnspStop());
            }
            else if(m2.getEnspStart() <= m1.getEnspStart() && m2.getEnspStop() >= m1.getEnspStop()){//m1 is "included" in m2
                return new Overlap(m1.getPdbId(), m2.getPdbId(), "m1 is included in m2", m1.getEnspStart(), m1.getEnspStop(), m1.getEnspStart(), m1.getEnspStop());
            }
            else{
                System.out.println("### overlap available, but not found! ###");
                return null;//should not happen
            }
        }
    }
    
    /**
     * @param overlap the overlap
     * @return double[] contains the rmsd and the gtd-ts sccores
     */
    public double[] superimposeOverlap(Overlap overlap){
        return new double[]{};
    }

    public static void main(String[] args) throws SQLException, IOException {
        ModelPDB_onENSP m = new ModelPDB_onENSP();
        ArrayList<Model> models = m.getModelsForENSP("ENST00000358662");
        System.out.println(m.displayModels(models, "ENST00000358662"));
        //PDBData pdb = m.modelToStructure(models.get(0));
        Overlap overlap = m.findModelOverlap(models.get(0), models.get(1));
        System.out.println("");
    }

}
