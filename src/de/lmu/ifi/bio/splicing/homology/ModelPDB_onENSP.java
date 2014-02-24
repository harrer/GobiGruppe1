package de.lmu.ifi.bio.splicing.homology;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Event;
import de.lmu.ifi.bio.splicing.io.DSSPParser;
import de.lmu.ifi.bio.splicing.io.GenomeSequenceExtractor;
import de.lmu.ifi.bio.splicing.io.PDBParser;
//import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.structures.PDBData;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;
import de.lmu.ifi.bio.splicing.structures.mapping.Model;
import de.lmu.ifi.bio.splicing.superimpose.Superposition;
import java.io.BufferedReader;
import java.io.File;
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

    private HashMap<String, String> pdbSequences;
    private final SingleGotoh gotoh;
    private final Superposition superposition;
    private HashMap<String, Integer> enstSequnces;
    private final int[] gtdts_frequencies;
    private final HashMap<String, String> cathPDB_id_cath_class_map;
    private final HashMap<String, int[]> cathPDB_start_stop_map;
    private final HashMap<String, ArrayList<String>> PDBid_cathPDB_id_map;

    public ModelPDB_onENSP() {
        pdbSequences = new HashMap<>();//readPDBseqlib("/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/pdb.seqlib");
        enstSequnces = new HashMap<>();
        this.gotoh = new SingleGotoh();
        superposition = new Superposition();
        gtdts_frequencies = new int[101];
        cathPDB_id_cath_class_map = new HashMap<>();
        cathPDB_start_stop_map = new HashMap<>();
        PDBid_cathPDB_id_map = new HashMap<>();
        try {
            this.read_pdb_cath_mapping();
        } catch (IOException ex) {
            Logger.getLogger(ModelPDB_onENSP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ModelPDB_onENSP(boolean noPDB_CATH_mapping){
        pdbSequences = new HashMap<>();//readPDBseqlib("/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/pdb.seqlib");
        enstSequnces = new HashMap<>();
        this.gotoh = new SingleGotoh();
        superposition = new Superposition();
        gtdts_frequencies = new int[101];
        cathPDB_id_cath_class_map = new HashMap<>();
        cathPDB_start_stop_map = new HashMap<>();
        PDBid_cathPDB_id_map = new HashMap<>();
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
            result = Setting.dbq.db.select_oneColumn(query);
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
        String ensp_id = Setting.dbq.db.getEnsp(ENST_id);//(String) Setting.dbq.db.select_oneColumn("select proteinid from Transcript where transcriptid = '" + ENST_id + "'")[0];
        return Setting.dbq.db.getPDBID(ensp_id).toArray(new String[0]);
    }

    private ArrayList<String[]> alignPDBs_onENSP(String ENST_id, String[] seq, double coverage, int longerThan, double seqIdentity) {
        ArrayList<String[]> alignments = new ArrayList<>();
        String seq1 = GenomeSequenceExtractor.getProteinSequence(Setting.dbq.getTranscript(ENST_id));
        enstSequnces.put(ENST_id, seq1.length());
        if (seq1.length() > 8000) {
            return alignments;
        }
        gotoh.setSeq1(seq1);
        for (String PDBid : seq) {
            String str;
            if (pdbSequences.containsKey(PDBid)) {
                str = pdbSequences.get(PDBid);
            } else {
                str = PDBParser.getPDBSequence(PDBid);
                pdbSequences.put(PDBid, str);
            }
            if (str.length() > 8000) {
                continue;
            }
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

    public ArrayList<Model> getModelsForENSP(String enspId, double coverage, int longerThan, double seqIdentity) {
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

    public String displayModels(String ENST_id, List<Event> events) {
        ArrayList<Model> models = getModelsForENST(ENST_id);
        String proteinSeq = GenomeSequenceExtractor.getProteinSequence(Setting.dbq.getTranscript(ENST_id));
        StringBuilder sb = new StringBuilder(ENST_id+"\n        " + proteinSeq + "\n        ");
        int old = 0;
        for (Event event : events){
            sb.append(new String(new char[event.getStart() - old]).replaceAll("\0", " "));
            if(event.getType() != 'I'){
                sb.append(new String(new char[event.getStop() - event.getStart() + 1]).replaceAll("\0", String.valueOf(event.getType())));
                old = event.getStop() + 1;
            }
        }
        sb.append("\n\n");
        for (Model model : models) {
            sb.append(model.getPdbId()).append(": ");
            HashMap<Integer, Integer> aligned = model.getAligned();
            boolean dssp_available = true;
            DSSPData dssp = null;
            try {
                dssp = DSSPParser.parseDSSPFile(new File(Setting.DSSPDIR+model.getPdbId()+".dssp"), "");
            } catch (Exception e) {
                dssp_available = false;
            }
            String pdbSeq = PDBParser.getPDBFile(model.getPdbId()).getSequence();
            try {
                for (int i = 0; i < proteinSeq.length(); i++) {
                    char append = (i >= model.getEnspStart() && i <= model.getEnspStop() && aligned.containsKey(i)) ? pdbSeq.charAt(aligned.get(i)) : '\'';//&& model.getAligned().containsKey(i)
                    sb.append(append);
                }
                sb.append("\n        ");
                if (dssp_available) {
                    Character[] secStruct = dssp.getSecondarySructure();
                    for (int i = 0; i < proteinSeq.length(); i++) {
                        char append = (i >= model.getEnspStart() && i <= model.getEnspStop() && aligned.containsKey(i)) ? secStruct[aligned.get(i)] : '\'';//&& model.getAligned().containsKey(i)
                        sb.append(append);
                    }
                }
            } catch (StringIndexOutOfBoundsException | ArrayIndexOutOfBoundsException e) {
                sb.append('+');
            }
            sb.append("\n\n");
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
            int abs = -1;
            double rel = -1;
            if (m1.getEnspStart() <= m2.getEnspStart() && m1.getEnspStop() >= m2.getEnspStop()) {//m2 is "included" in m1
                abs = (m2.getEnspStop() - m2.getEnspStart() + 1);
                rel = 1.0 * abs / (m1.getEnspStop() - m1.getEnspStart() + 1);
                return new Overlap(m1, m2, OverlapType.m2_included_in_m1, m2.getEnspStart(), m2.getEnspStop(), m2.getEnspStart(), m2.getEnspStop(), rel, abs);
            } else if (m2.getEnspStart() <= m1.getEnspStart() && m2.getEnspStop() >= m1.getEnspStop()) {//m1 is "included" in m2
                abs = m1.getEnspStop() - m1.getEnspStart() + 1;
                rel = 1.0 * abs / (m2.getEnspStop() - m2.getEnspStart() + 1);
                return new Overlap(m1, m2, OverlapType.m1_included_in_m2, m1.getEnspStart(), m1.getEnspStop(), m1.getEnspStart(), m1.getEnspStop(), rel, abs);
            } else if (m2.getEnspStop() > m1.getEnspStop()) {//partly overlap of m1(end), m2(start)
                abs = m1.getEnspStop() - m2.getEnspStart() + 1;
                rel = 1.0 * abs / (Math.max(m1.getEnspStop() - m1.getEnspStart(), m2.getEnspStop() - m2.getEnspStart()));
                return new Overlap(m1, m2, OverlapType.m1_end_m2_start_overlap, m2.getEnspStart(), m1.getEnspStop(), m2.getEnspStart(), m1.getEnspStop(), rel, abs);
            } else if (m2.getEnspStart() < m1.getEnspStart()) {//partly overlap of m1(start), m2(end)
                abs = m2.getEnspStop() - m1.getEnspStart() + 1;
                rel = 1.0 * abs / (Math.max(m1.getEnspStop() - m1.getEnspStart(), m2.getEnspStop() - m2.getEnspStart()));
                return new Overlap(m1, m2, OverlapType.m1_start_m2_end_overlap, m1.getEnspStart(), m2.getEnspStop(), m1.getEnspStart(), m2.getEnspStop(), rel, abs);
            } else {
                System.out.println("### overlap available, but not found! ###");
                return null;//should not happen
            }
        }
    }

    public ArrayList<Overlap> findOverlapForAllModels(ArrayList<Model> models) {
        ArrayList<Overlap> overlaps = new ArrayList<>();
        for (int i = 0; i < models.size(); i++) {
            for (int j = i; j < models.size(); j++) {
                Overlap overlap = findModelOverlap(models.get(i), models.get(j));
                if (overlap != null && j != i) {
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
    public Object[] superimposeOverlap(Overlap overlap) {
        Model m1 = overlap.getModel1(), m2 = overlap.getModel2();
        int start = -1, stop = -1;
        if (overlap.getType().equals(OverlapType.m1_included_in_m2)) {
            start = m1.getEnspStart();
            stop = m1.getEnspStop();
        } else if (overlap.getType().equals(OverlapType.m2_included_in_m1)) {
            start = m2.getEnspStart();
            stop = m2.getEnspStop();
        } else if (overlap.getType().equals(OverlapType.m1_end_m2_start_overlap)) {
            start = m1.getEnspStop();
            stop = m2.getEnspStart();
        } else if (overlap.getType().equals(OverlapType.m1_start_m2_end_overlap)) {
            start = m1.getEnspStart();
            stop = m2.getEnspStop();
        } else {
            System.out.println("no overlap type matching");
        }//should not happen
        ArrayList<double[]> coord1 = new ArrayList<>(), coord2 = new ArrayList<>();
        double[][] pdb1 = PDBParser.getPDBFile(m1.getPdbId()).getCACoordinates(), pdb2 = PDBParser.getPDBFile(m2.getPdbId()).getCACoordinates();//the CA-coordinates of the respective PDB files
        HashMap<Integer, Integer> aligned1 = m1.getAligned(), aligned2 = m2.getAligned();
        for (int i = start; i < stop; i++) {
            if (aligned1.containsKey(i) && aligned2.containsKey(i)) {
                coord1.add(pdb1[aligned1.get(i)]);
                coord2.add(pdb2[aligned2.get(i)]);
            }
        }
        assert (coord1.size() == coord2.size());// must be the same length

        Object[] sp = null;
        try {
            sp = superposition.superimpose(new DenseDoubleMatrix2D(coord1.toArray(new double[][]{})), new DenseDoubleMatrix2D(coord2.toArray(new double[][]{})), null);
        } catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("SVD");
        }

        return sp;
    }

    private void update_gtdts_freq(double gtd_ts) {
        this.gtdts_frequencies[(int) Math.floor(gtd_ts * 100)]++;
    }
    
    private void read_pdb_cath_mapping() throws IOException{
        BufferedReader br = new BufferedReader(new FileReader("/home/proj/biosoft/CATH/CathDomainDescriptionFile.v3.5.0"));
        String line, domain="", cathDomain = "", cathcode="";
        int[] range = new int[0];
        boolean domainFound = false, cathcodeFound = false, rangeFound = false;
        while((line = br.readLine()).startsWith("#")){}//ignore header
        while((line = br.readLine()) != null){
            boolean nfException = false;
            if(line.startsWith("DOMAIN")){
                String pdb = line.split("\\s+")[1];
                domain = pdb.substring(0,4)+'.'+pdb.charAt(4);
                cathDomain = pdb;
                domainFound = true;
            }
            else if(line.startsWith("CATHCODE")){
                cathcode = line.split("\\s+")[1];
                cathcodeFound = true;
            }
            else if(line.startsWith("SRANGE")){
                String[] splitWhitespace = line.split("\\s+");
                try {
                    range = new int[]{Integer.parseInt(splitWhitespace[1].split("=")[1]), Integer.parseInt(splitWhitespace[2].split("=")[1])};
                } catch (NumberFormatException numberFormatException) {
                    range = new int[]{0,0};
                    nfException = true;
                }
                rangeFound = true;
            }
            if(domainFound && cathcodeFound && rangeFound && !nfException){
                this.cathPDB_id_cath_class_map.put(cathDomain, cathcode);
                cathPDB_start_stop_map.put(cathDomain, range);
                ArrayList<String> list = (PDBid_cathPDB_id_map.containsKey(domain))? PDBid_cathPDB_id_map.get(domain) : new ArrayList<String>();
                list.add(cathDomain);
                PDBid_cathPDB_id_map.put(domain, list);    
                domainFound = false; cathcodeFound = false; rangeFound = false;
            }
        }
    }

    public void run(String fileOut, double coverage, int longerThan, double seqIdentity) throws IOException {
        Object[] templates = null;
        //writer.write("rmsd\tgtd-ts\tpdb1\tpdb2\tENST_id\tabs_length\trel_length\toverlap_type\n");
        try {
            templates = Setting.dbq.db.select_oneColumn("select distinct transcriptid from transcript_has_pdbs");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        int templateCount = 1, nullpointer = 0, overlapCount = 0;
        for (Object object : templates) {
            StringBuilder sb = new StringBuilder("insert into overlaps(ENST, PDB1, PDB2, rmsd, gts_ts, abs_length, rel_length, type, supFam1, supFam2) values");
            System.out.println(templateCount + " templates processed");
            templateCount++;
            String enst = "";
            ArrayList<Overlap> overlaps = new ArrayList<>();
            try {
                enst = (String) Setting.dbq.db.select_oneColumn("select transcriptid from Transcript where proteinid = '" + (String) object + "'")[0];
                ArrayList<Model> models = getModelsForENST(enst, coverage, longerThan, seqIdentity);
                overlaps = findOverlapForAllModels(models);
            } catch (ArrayIndexOutOfBoundsException|SQLException ex) {
                ex.printStackTrace();
            }
            boolean setComma = false, appendToSb = false;
            for (Overlap overlap : overlaps) {
                overlapCount++;
                if (setComma) {
                    sb.append(',');
                } else {
                    setComma = true;
                }
                Model m1 = overlap.getModel1();
                Model m2 = overlap.getModel2();
                //String supFam1 = cathPDB_id_cath_class_map.containsKey(m1.getPdbId())? cathPDB_id_cath_class_map.get(m1.getPdbId()) : "";
                //String supFam2 = cathPDB_id_cath_class_map.containsKey(m2.getPdbId())? cathPDB_id_cath_class_map.get(m2.getPdbId()) : "";
//    private final HashMap<String, String> cathPDB_id_cath_class_map;
//    private final HashMap<String, int[]> cathPDB_start_stop_map;
//    private final HashMap<String, ArrayList<String>> PDBid_cathPDB_id_map;
                String supFam1 = "";
                if(PDBid_cathPDB_id_map.containsKey(m1.getPdbId())){
                    for (String id : PDBid_cathPDB_id_map.get(m1.getPdbId())) {
                        int[] range = this.cathPDB_start_stop_map.get(id);
                        if(m1.getPdbStart()>= range[0] && m1.getPdbStop()<=range[1]){
                            supFam1 = this.cathPDB_id_cath_class_map.get(id);
                            break;
                        }
                    }
                }
                String supFam2 = "";
                if(PDBid_cathPDB_id_map.containsKey(m2.getPdbId())){
                    for(String id : PDBid_cathPDB_id_map.get(m2.getPdbId())){
                        int[] range = cathPDB_start_stop_map.get(id);
                        if(m2.getPdbStart()>=range[0] && m2.getPdbStop()<=range[1]){
                            supFam2 = cathPDB_id_cath_class_map.get(id);
                            break;
                        }
                    }
                }
                
                try {
                    Object[]sp = superimposeOverlap(overlap);
                    double[] scores = new double[]{(double) sp[2], (double) sp[3]};
                    sb.append("('").append(m1.getEnstId()).append("','").append(m1.getPdbId()).append("','").append(m2.getPdbId()).append("',");
                    sb.append(scores[0]).append(',').append(scores[1]).append(',').append(overlap.getAbsoluteLength()).append(',').append(overlap.getRelativeLength());
                    sb.append(",'").append(overlap.getType()).append("','").append(supFam1).append("','").append(supFam2).append("')");
                    appendToSb = true;
                    update_gtdts_freq(scores[1]);
                    //writer.printf("%f\t%f\t%s\t%s\t%s\t%d\t%.3f\t%s\n", scores[0], scores[1], overlap.getModel1().getPdbId(), overlap.getModel2().getPdbId(), overlap.getModel1().getEnstId(),overlap.getAbsoluteLength(),overlap.getRelativeLength(),overlap.getType());
                } catch (NullPointerException e) {
                    nullpointer++;
                    setComma = false;
                }
            }
            if (overlaps.size() > 0 && appendToSb) {
                if (sb.toString().charAt(sb.length() - 1) == ',') {
                    sb.deleteCharAt(sb.length() - 1);
                }
                try {
                    Setting.dbq.db.executeUpdate(sb.toString());
                } catch (SQLException ex) {
                    Logger.getLogger(ModelPDB_onENSP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        PrintWriter writer = new PrintWriter(fileOut);
        writer.println("gtd_ts: frequency");
        for (int i = 0; i < this.gtdts_frequencies.length; i++) {
            writer.println((i/100.0) + ": " + gtdts_frequencies[i]);
        }
        writer.close();
        System.out.println("superPos failure: " + nullpointer + " on " + overlapCount + " overlaps for " + (templateCount - 1) + " templates");
    }
    
    public Object[] superimposeFullOverlap(Overlap overlap) {
        Model m1 = overlap.getModel1(), m2 = overlap.getModel2();
        int start = -1, stop = -1;
        if (overlap.getType().equals(OverlapType.m1_included_in_m2)) {
            start = m1.getEnspStart();
            stop = m1.getEnspStop();
        } else if (overlap.getType().equals(OverlapType.m2_included_in_m1)) {
            start = m2.getEnspStart();
            stop = m2.getEnspStop();
        } else if (overlap.getType().equals(OverlapType.m1_end_m2_start_overlap)) {
            start = m1.getEnspStop();
            stop = m2.getEnspStart();
        } else if (overlap.getType().equals(OverlapType.m1_start_m2_end_overlap)) {
            start = m1.getEnspStart();
            stop = m2.getEnspStop();
        } else {System.out.println("no overlap type matching");}//should not happen
        ArrayList<double[]> coord1 = new ArrayList<>(), coord2 = new ArrayList<>();
        double[][] pdb1 = PDBParser.getPDBFile(m1.getPdbId()).getCACoordinates(), pdb2 = PDBParser.getPDBFile(m2.getPdbId()).getCACoordinates();//the CA-coordinates of the respective PDB files
        HashMap<Integer, Integer> aligned1 = m1.getAligned(), aligned2 = m2.getAligned();
        for (int i = start; i < stop; i++) {
            if (aligned1.containsKey(i) && aligned2.containsKey(i)) {
                coord1.add(pdb1[aligned1.get(i)]);
                coord2.add(pdb2[aligned2.get(i)]);
            }
        }
        assert (coord1.size() == coord2.size());// must be the same length
        double[][] pdb2_full = PDBParser.getPDBFile(m2.getPdbId()).getCoordinates();
        Object[] sp = null;
        try {
            sp = superposition.superimposeFullStructure(new DenseDoubleMatrix2D(coord1.toArray(new double[][]{})), new DenseDoubleMatrix2D(coord2.toArray(new double[][]{})), new DenseDoubleMatrix2D(pdb2_full));
        } catch (ArrayIndexOutOfBoundsException e) {
            //System.out.println("SVD");
        }

        return sp;
    }

    public static void main(String[] args) throws SQLException, IOException {
        ModelPDB_onENSP m = new ModelPDB_onENSP();
//        ArrayList<Model> models = m.getModelsForENST("ENST00000380952");
//        System.out.println(m.displayModels("ENST00000412135"));
//        //PDBData pdb = m.modelToStructure(models.get(0));
//        ArrayList<Overlap> overlaps = m.findOverlapForAllModels(models);
//        //double[] sPose = m.superimposeOverlap(overlap);
//        System.out.println("");
        m.run("/home/h/harrert/Desktop/GTD_TS_frequenciesSSSS.txt", 0.6, 60, 0.4);
//        System.out.println(GenomeSequenceExtractor.getProteinSequence(Setting.dbq.getTranscript("ENST00000308639")));
//        ArrayList<Model> models = m.getModelsForENST("ENST00000412135");
//        Overlap overlap = m.findModelOverlap(models.get(9), models.get(10));
        // ### superimposeOverlap() ###
//        Object[] sp1 = m.superimposeFullOverlap(overlap);
//        de.lmu.ifi.bio.splicing.superimpose.PDBParser.superimpose(Setting.PDBREPCCHAINSDIR + models.get(9).getPdbId()+".pdb", Setting.PDBREPCCHAINSDIR + models.get(10).getPdbId()+".pdb");
        //Object[] sp2 = m.superimposeOverlap(overlap2);
        System.out.println("");
    }

}
