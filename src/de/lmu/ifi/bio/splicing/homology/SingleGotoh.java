package de.lmu.ifi.bio.splicing.homology;

import de.lmu.ifi.bio.splicing.localAli.AlignmentMax;
import de.lmu.ifi.bio.splicing.localAli.Parser;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author harrert
 */
public class SingleGotoh {

    private int[][] matrix;
    private int gapopen;
    private int gapextend;
    private String mode;
    private boolean check;
    private HashMap<Character, Integer> aminoAcids;    
    
    private int[][] A = new int[8001][8001];
    private int[][] I = new int[8001][8001];
    private int[][] D = new int[8001][8001];

    private String seq1;
    private String seq2;

    public AlignmentMax fillMatrixLocal() {
        int max = Integer.MIN_VALUE, max_i = 0, max_j = 0;
        for (int i = 1; i < seq1.length() + 1; i++) {
            for (int j = 1; j < seq2.length() + 1; j++) {
                I[i][j] = Math.max(A[i - 1][j] + g(1), I[i - 1][j] + gapextend);
                D[i][j] = Math.max(A[i][j - 1] + g(1), D[i][j - 1] + gapextend);
                int a = Math.max(D[i][j], I[i][j]);
                A[i][j] = Math.max(0, Math.max(A[i - 1][j - 1] + getCost(seq1.charAt(i - 1), seq2.charAt(j - 1)), a));
                if (A[i][j] > max) {
                    max = A[i][j];
                    max_i = i;
                    max_j = j;
                }
            }
        }
        return new AlignmentMax(max_i, max_j, max, mode);
    }

    public String[] backtrackingLocal(AlignmentMax max) {
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        int i = seq1.length(), j = seq2.length();
        while (i > max.getMax()[0]) {
            i--;
            s1.append(seq1.charAt(i));
            s2.append('-');
        }
        while (j > max.getMax()[1]) {
            j--;
            s1.append('-');
            s2.append(seq2.charAt(j));
        }
        while (A[i][j] != 0) {
            if (A[i][j] == (A[i - 1][j - 1] + getCost(seq1.charAt(i - 1), seq2.charAt(j - 1)))) {
                i--;
                j--;
                s1.append(seq1.charAt(i));
                s2.append(seq2.charAt(j));
            } else if (A[i][j] == I[i][j]) {
                int k = 1;
                s1.append(seq1.charAt(i - 1));
                s2.append('-');
                while (!((A[i - k][j] + g(k)) == A[i][j])) {
                    k++;
                    s1.append(seq1.charAt(i - k));
                    s2.append('-');
                }
                i -= k;
            } else if (A[i][j] == D[i][j]) {
                int k = 1;
                s2.append(seq2.charAt(j - 1));
                s1.append('-');
                while (!((A[i][j - k] + g(k)) == A[i][j])) {
                    k++;
                    s2.append(seq2.charAt(j - k));
                    s1.append('-');
                }
                j -= k;
            }
        }
        while (j > 0) {
            j--;
            s1.append('-');
            s2.append(seq2.charAt(j));
        }
        while (i > 0) {
            i--;
            s1.append(seq1.charAt(i));
            s2.append('-');
        }
        return new String[]{s1.reverse().toString(), s2.reverse().toString()};
    }

    public static int[] getAli_StartEnd(String[] ali) {
        int end = -1, start = -1;
        for (int i = 0; i < ali[0].length(); i++) {
            if (ali[0].charAt(i) != '-' && ali[1].charAt(i) != '-') {
                start = i;
                break;
            }
        }
        for (int i = ali[0].length() - 1; i >= 0; i--) {
            if (ali[0].charAt(i) != '-' && ali[1].charAt(i) != '-') {
                end = i;
                break;
            }
        }
        return new int[]{start, end};
    }

    public double checkScoreLocal(String s1, String s2) {
        int[] startEnd = getAli_StartEnd(new String[]{s1, s2});
        int score = 0;
        for (int i = startEnd[0]; i <= startEnd[1]; i++) {
            if (s1.charAt(i) != '-' && s2.charAt(i) != '-') {
                score += getCost(s1.charAt(i), s2.charAt(i));
            } else {
                int k = 1;
                while ((i + k) < s1.length() && (s1.charAt(i + k) == '-' || s2.charAt(i + k) == '-')) {
                    k++;
                }
                score += g(k);
                i += (k - 1);
            }
        }
        return score / 10.0;
    }

    //Sequenzidentitat (Anteil der ubereinanderstehenden gleichen Aminosauren im Alignment) des lokalen alignierten Teils.
    public static double sequenceIdentity(String[] ali) {
        int identical = 0;
        int[] startEnd = getAli_StartEnd(ali);
        for (int i = startEnd[0]; i <= startEnd[1]; i++) {
            if (ali[0].charAt(i) != '-' && ali[0].charAt(i) == ali[1].charAt(i)) {
                identical++;
            }
        }
        return 1.0 * identical / (startEnd[1] - startEnd[0] + 1);
    }

    //die langer als 60 Aminosauren sind, oder  60% des Proteins abdecken
    public boolean coverage(String[] ali, int longerThan, double coverage) {
        int[] startEnd = getAli_StartEnd(ali);
        int aligned = 0, ENSP_length = 0;
        for (int i = startEnd[0]; i <= startEnd[1]; i++) {
            if (ali[0].charAt(i) != '-' && ali[1].charAt(i) != '-') {
                aligned++;
            }
        }
        for (int i = 0; i < ali[0].length(); i++) {
            if (ali[0].charAt(i) != '-') {
                ENSP_length++;
            }
        }
        return (aligned >= longerThan || 1.0 * aligned / ENSP_length >= coverage);
    }

    private int g(int n) {
        return gapopen + n * gapextend;
    }

    private int getCost(char i, char j) {
        if (i == 'X' || j == 'X') {
            return -2;
        } else {
            return matrix[i - 65][j - 65];
        }
    }

    private void initParams(String seq1, String seq2) throws IOException {
        Parser parser = new Parser();
        matrix = parser.parseMatrix("dayhoff", true);
        gapopen = -120;
        gapextend = -10;
        mode = "freeshift";
        check = true;
        String aa = "ARNDCQEGHILKMFPSTWYV";
        aminoAcids = new HashMap<>();
        this.seq1 = seq1;
        this.seq2 = seq2;
        for (int i = 0; i < aa.length(); i++) {
            aminoAcids.put(aa.charAt(i), i);
        }
    }

    public void setSeq1(String seq1) {
        this.seq1 = seq1;
    }

    public void setSeq2(String seq2) {
        this.seq2 = seq2;
        try {
            initParams(seq1, seq2);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        HashMap<String, String> params = new HashMap();
        //SingleGotoh g = new SingleGotoh("asd", "sdaf");
    }
}
