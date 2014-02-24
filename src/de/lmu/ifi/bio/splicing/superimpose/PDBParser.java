package de.lmu.ifi.bio.splicing.superimpose;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import de.lmu.ifi.bio.splicing.localAli.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author harrert
 */
public class PDBParser {

    private final static HashMap<String, String> STANDARD_AAS = new HashMap<>();

    public PDBParser() {
        STANDARD_AAS.put("ALA", "A");
        STANDARD_AAS.put("ARG", "R");
        STANDARD_AAS.put("ASN", "N");
        STANDARD_AAS.put("ASP", "D");
        STANDARD_AAS.put("CYS", "C");
        STANDARD_AAS.put("GLU", "E");
        STANDARD_AAS.put("GLN", "Q");
        STANDARD_AAS.put("GLY", "G");
        STANDARD_AAS.put("HIS", "H");
        STANDARD_AAS.put("ILE", "I");
        STANDARD_AAS.put("LEU", "L");
        STANDARD_AAS.put("LYS", "K");
        STANDARD_AAS.put("MET", "M");
        STANDARD_AAS.put("PHE", "F");
        STANDARD_AAS.put("PRO", "P");
        STANDARD_AAS.put("SER", "S");
        STANDARD_AAS.put("THR", "T");
        STANDARD_AAS.put("VAL", "V");
        STANDARD_AAS.put("TYR", "Y");
        STANDARD_AAS.put("TRP", "W");
        int s = STANDARD_AAS.size();
        HashMap<String, String> tmp = new HashMap(STANDARD_AAS.size());
        for (Map.Entry<String, String> entry : STANDARD_AAS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            tmp.put(value, key);
        }
        for (Map.Entry<String, String> entry : tmp.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            STANDARD_AAS.put(k, v);
        }
    }

    public static DoubleMatrix2D parseToMatrix(String file, boolean[] allignedPositions, boolean CA_only) throws IOException {//, int positions
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int aaCount = -1, cc = -1;
        ArrayList<double[]> list = new ArrayList();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("ATOM")) {
                String[] split = line.split("\\s+");
                if (cc != Integer.parseInt(split[5])) {
                    aaCount++;
                    cc = Integer.parseInt(split[5]);
                }
                if (CA_only && allignedPositions[aaCount] && split[2].equalsIgnoreCase("CA")) {//split[2].equalsIgnoreCase("N") || split[2].equalsIgnoreCase("CA") || split[2].equalsIgnoreCase("C")
                    list.add(new double[]{Double.parseDouble(split[6]), Double.parseDouble(split[7]), Double.parseDouble(split[8])});
                }
                else if(!CA_only){
                    list.add(new double[]{Double.parseDouble(split[6]), Double.parseDouble(split[7]), Double.parseDouble(split[8])});
                }
            }
        }
        br.close();
        double[][] dOut = list.toArray(new double[list.size()][3]);
        return new DenseDoubleMatrix2D(dOut);
    }

    public static ArrayList<AminoAcid> parseAll(String file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int aaCount = -1, end = -2;
        ArrayList<AminoAcid> list = new ArrayList<>();
        ArrayList<double[]> posList = new ArrayList<>();
        ArrayList<String> atomList = new ArrayList<>();
        AminoAcid current = new AminoAcid(aaCount, -1, "", "");
        while ((line = br.readLine()) != null) {
            if (line.startsWith("ATOM")) {
                String[] split = line.split("\\s+");
                if (Integer.parseInt(split[5]) != aaCount) {
                    current.setCoordinates(new DenseDoubleMatrix2D(posList.toArray(new double[][]{})));
                    current.setEndPos(end);
                    current.setAtomNames(atomList);
                    atomList = new ArrayList();
                    list.add(current);
                    current = new AminoAcid(Integer.parseInt(split[5]), Integer.parseInt(split[1]), split[3], split[4]);
                    posList = new ArrayList();
                    aaCount = Integer.parseInt(split[5]);
                }
                end = Integer.parseInt(split[1]);
                atomList.add(split[2]);
                if (split[2].equalsIgnoreCase("CA")) {
                    current.setcAlpha(posList.size());
                } else if (split[2].equalsIgnoreCase("C")) {
                    current.setC(posList.size());
                } else if (split[2].equalsIgnoreCase("N")) {
                    current.setN(posList.size());
                }
                posList.add(new double[]{Double.parseDouble(split[6]), Double.parseDouble(split[7]), Double.parseDouble(split[8])});
            }
        }
        current.setCoordinates(new DenseDoubleMatrix2D(posList.toArray(new double[][]{})));
        current.setEndPos(end);
        current.setAtomNames(atomList);
        list.add(current);
        list.remove(0);
        br.close();
        return list;
    }

    private static String pdbToSequence(String file) throws FileNotFoundException, IOException {
        PDBParser parser = new PDBParser();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int aaCount = -1;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("ATOM")) {
                String[] split = line.split("\\s+");
                int cc = Integer.parseInt(split[5]);
                if (cc != aaCount) {
                    aaCount = cc;
                    sb.append(STANDARD_AAS.get(split[3]));
                }
            }
        }
        br.close();
        return sb.toString();
    }
    
    public static ArrayList<String[]> pdbToList(String path) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        ArrayList<String[]> list = new ArrayList();
        while ((line = br.readLine()) != null) {
            if (line.startsWith("ATOM")) {
                list.add(line.split("\\s+"));
            }
        }
        return list;
    }

    public static String matrixToPDB(DoubleMatrix2D matrix, ArrayList<String[]> list, String sequence, String path_out, String name) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder("REMARK\nREMARK Protein ");
        DecimalFormat dec = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.US));
        sb.append(name).append("\nREMARK File written by harrert\nREMARK\n");
        for (int i = 0; i < matrix.rows(); i++) {
            sb.append("ATOM   ");
            if(i<10){sb.append("   ");}
            else if(i<100){sb.append("  ");}
            else if(i<1000){sb.append(" ");}
            sb.append(i).append(" ");
            if(list.get(i)[2].length()<3){sb.append(" ");}
            sb.append(list.get(i)[2]).append(" ");
            if(list.get(i)[2].length()==3){sb.append(" ");}
            else if(list.get(i)[2].length()==2){sb.append(" ");}
            else if(list.get(i)[2].length()==1){sb.append("  ");}
            sb.append(list.get(i)[3]).append(" ").append(list.get(i)[4]).append(" ");
            if(list.get(i)[5].length()==1){sb.append("  ");}
            else if(list.get(i)[5].length()==2){sb.append(" ");}
            sb.append(list.get(i)[5]).append("    ");
            if(matrix.get(i, 0)>0){sb.append(" ");}
            if(matrix.get(i, 0)<10.0 && matrix.get(i, 0)>-10.0){sb.append("  ");}
            else if(matrix.get(i, 0)<100.0 && matrix.get(i, 0)>-100.0){sb.append(" ");}
            sb.append(dec.format(matrix.get(i, 0)));
            //sb.append(" ");
            if(matrix.get(i, 1)>0){sb.append(" ");}
            if(matrix.get(i, 1)<10.0 && matrix.get(i, 1)>-10.0){sb.append("  ");}
            else if(matrix.get(i, 1)<100.0 && matrix.get(i, 1)>-100.0){sb.append(" ");}
            sb.append(dec.format(matrix.get(i, 1)));
            //sb.append(" ");
            if(matrix.get(i, 2)>0){sb.append(" ");}
            if(matrix.get(i, 2)<10.0 && matrix.get(i, 2)>-10.0){sb.append("  ");}
            else if(matrix.get(i, 2)<100.0 && matrix.get(i, 2)>-100.0){sb.append(" ");}
            sb.append(dec.format(matrix.get(i, 2))).append("\n");
        }
        sb.append("TER");
        PrintWriter writer = new PrintWriter(path_out + name);
        writer.write(sb.toString());
        writer.close();
        return sb.toString();
    }

    public static String alignedSequence(String sequence, boolean[] alignedPositions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sequence.length(); i++) {
            if (alignedPositions[i]) {
                sb.append(sequence.charAt(i));
            }
        }
        return sb.toString();
    }

    private static HashMap<String, String> params(String matrix, String go, String ge, String mode) {
        HashMap<String, String> params = new HashMap<>();
        params.put("-m", matrix);//"BlakeCohenMatrix|dayhoff|THREADERSimilarityMatrix|blosum50|pam250"
        params.put("-go", go);
        params.put("-ge", ge);
        params.put("-mode", mode);//"local|global|freeshift"
        return params;
    }

    private static boolean[] alignedPositions(String[] alignment, boolean upperSequence, int seqLength) {
        boolean[] b = new boolean[seqLength];
        int c = 0;
        if (upperSequence) {
            for (int i = 0; i < alignment[0].length(); i++) {
                if ((alignment[0].charAt(i) != '-') && (alignment[1].charAt(i) != '-')) {
                    b[c] = true;
                    c++;
                } else if ((alignment[0].charAt(i) != '-') && (alignment[1].charAt(i) == '-')) {
                    b[c] = false;
                    c++;
                }
            }
        } else {
            for (int i = 0; i < alignment[0].length(); i++) {
                if ((alignment[0].charAt(i) != '-') && (alignment[1].charAt(i) != '-')) {
                    b[c] = true;
                    c++;
                } else if ((alignment[1].charAt(i) != '-') && (alignment[0].charAt(i) == '-')) {
                    b[c] = false;
                    c++;
                }
            }
        }
//        for (int i = 0; i < alignment[0].length() - b.size(); i++) {
//            b.add(Boolean.FALSE);
//        }
        return b;
    }

    private static double seqIdentity(String[] ali) {//soll exakt identische AA / Anzahl alinierter, siehe checkscore
        int id = 0;
        int left = 0, right = ali[0].length() - 1, score = 0;
        if (ali[0].startsWith("-")) {
            while (ali[0].charAt(left) == '-') {
                left++;
            }
        } else if (ali[1].startsWith("-")) {
            while (ali[1].charAt(left) == '-') {
                left++;
            }
        }
        if (ali[1].endsWith("-")) {
            while (ali[1].charAt(right) == '-') {
                right--;
            }
        } else if (ali[0].endsWith("-")) {
            while (ali[0].charAt(right) == '-') {
                right--;
            }
        }
        for (int i = left; i <= right; i++) {
            id = ((ali[0].charAt(i) != '-') && (ali[1].charAt(i) != '-')) ? id + 1 : id;
        }
        return 1.0*id/(right-left+1);
    }

    public static String start(ArrayList<String[]> readcInpairs, String outFile) throws IOException {
        StringBuilder sb = new StringBuilder("P\tQ\tidentity\tRMSD\tgtd-ts");
        GotohForSuperposition g = new GotohForSuperposition(params("dayhoff", "-12", "-1", "freeshift"));
        String pdbPath = "/home/proj/biosoft/PROTEINS/CATHSCOP/STRUCTURES/";//"/Users/Tobias/Desktop/pdb/"
        int errCount = 0, count = 0, p = 0;
        Superposition s = new Superposition();
        for (String[] seq : readcInpairs) {
            count++;
            if (count >= (p * 558)) {
                System.out.println(p + "%");
                p++;
            }
            String file_p = pdbPath + seq[0] + ".pdb";
            String file_q = pdbPath + seq[1] + ".pdb";
            try {
                String seq1 = pdbToSequence(file_p), seq2 = pdbToSequence(file_q);
                g.setSequences(seq1, seq2);
                String[] ali = g.backtrackingFreeshift(g.fillMatrixFreeshift());
                DoubleMatrix2D P = parseToMatrix(file_p, alignedPositions(ali, true, seq1.length()), true);
                DoubleMatrix2D Q = parseToMatrix(file_q, alignedPositions(ali, false, seq2.length()), true);
                Object[] superposition = null;//s.superimpose(P, Q);
                double identity = seqIdentity(ali);
                sb.append(seq[0]).append('\t').append(seq[1]).append('\t').append(identity).append('\t').append(superposition[2]).append('\t').append(superposition[3]).append('\n');
            } catch (Exception e) {
                //System.out.println(entry.getKey() + ", " + entry.getValue());
                errCount++;
            }
        }
//        System.out.println(errCount+" errors on "+count);
        PrintWriter writer = new PrintWriter(outFile);
        writer.write(sb.toString());
        writer.close();
        return sb.toString();
    }

    public static void superimpose(String p, String q) throws IOException {
        GotohForSuperposition g = new GotohForSuperposition(params("dayhoff", "-12", "-1", "freeshift"));
        String seq1 = pdbToSequence(p), seq2 = pdbToSequence(q);
        g.setSequences(seq1, seq2);
        String[] ali = g.backtrackingFreeshift(g.fillMatrixFreeshift());
        DoubleMatrix2D P = parseToMatrix(p, alignedPositions(ali, true, seq1.length()), true);
        DoubleMatrix2D Q = parseToMatrix(q, alignedPositions(ali, false, seq2.length()), true);
        DoubleMatrix2D Q_full = parseToMatrix(q, null , false);
        Superposition s = new Superposition();
        Object[] superposition = s.superimposeFullStructure(P, Q, Q_full);
        ArrayList list = pdbToList(q);
        matrixToPDB((DoubleMatrix2D) superposition[0], list, seq2, "/tmp/", "SSumperimposeEE.pdb");
    }

    public static void main(String[] args) throws IOException {
        long timeBefore = new Date().getTime();
        PDBParser p = new PDBParser();
        String file_p = "//Users/Tobias/Desktop/pdb/1tfxC00.pdb";//"/home/tobias/Documents/GoBi/Blatt4/1ev0B00.pdb";
        String file_q = "/Users/Tobias/Desktop/pdb/1ca0I00.pdb";//"/Users/Tobias/Desktop/pdb/1lddB00.pdb";/home/proj/biosoft/PROTEINS/CATHSCOP/STRUCTURES/
//        superimpose(file_p, file_q);
//        System.out.println("gdt-ts: "+s.gdt_ts(P, (DoubleMatrix2D)superposition[0]));
//        matrixToPDB((DoubleMatrix2D)superposition[0], alignedSequence(seq2, b), "/Users/Tobias/Desktop/", "out.pdb");
//        ArrayList<String[]> inpairs = readcInpairs("/home/proj/biosoft/praktikum/genprakt-ws13/assignment1/cathscop.inpairs");
  //      String s = start(inpairs, "/home/h/harrert/Desktop/inpairs_mapping.txt");
        superimpose(file_p, file_q);
        System.out.println(new Date().getTime() - timeBefore + " ms");
    }
}
