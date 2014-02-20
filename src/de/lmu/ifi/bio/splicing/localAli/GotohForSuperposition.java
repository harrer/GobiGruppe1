package de.lmu.ifi.bio.splicing.localAli;

import java.io.IOException;
import java.util.HashMap;

public class GotohForSuperposition {
    
    private int[][] matrix;
    private int gapopen;
    private int gapextend;
    private String mode;
    private HashMap<Character, Integer> aminoAcids;

    private int[][] A;
    private int[][] I;
    private int[][] D;

    private String seq1;
    private String seq2;

    public GotohForSuperposition(HashMap<String, String> params) throws IOException {
        initParams(params);
    }

    public void setSequences(String seq1, String seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
    }

    public AlignmentMax fillMatrixFreeshift() {
        AlignmentMax max = null;
        max = new AlignmentMax(0, 0, Integer.MIN_VALUE, "freeshift");
        for (int i = 1; i < seq1.length() + 1; i++) {
            for (int j = 1; j < seq2.length() + 1; j++) {
                I[i][j] = Math.max(A[i - 1][j] + g(1), I[i - 1][j] + gapextend);
                D[i][j] = Math.max(A[i][j - 1] + g(1), D[i][j - 1] + gapextend);
                int a = Math.max(D[i][j], I[i][j]);
                A[i][j] = Math.max(A[i - 1][j - 1] + getCost(seq1.charAt(i - 1), seq2.charAt(j - 1)), a);
            }
        }
        for (int i = 1; i < seq1.length()+1; i++) {
            if(A[i][seq2.length()] >= max.getMax()[2]){
                max.setMax(i, seq2.length(), A[i][seq2.length()]);
            }
        }
        for (int i = 1; i < seq2.length()+1; i++) {
            if(A[seq1.length()][i] >= max.getMax()[2]){
                max.setMax(seq1.length(), i, A[seq1.length()][i]);
            }
        }
//        System.out.println("Score: "+max.getMax()[2]/10.0);
        return max;
    }

    public AlignmentMax fillMatrixLocal() {
        int max = Integer.MIN_VALUE, max_i = 0, max_j = 0;
        for (int i = 1; i < seq1.length() + 1; i++) {
            for (int j = 1; j < seq2.length() + 1; j++) {
                I[i][j] = Math.max(A[i - 1][j] + g(1), I[i - 1][j] + gapextend);
                D[i][j] = Math.max(A[i][j - 1] + g(1), D[i][j - 1] + gapextend);
                int a = Math.max(D[i][j], I[i][j]);
                A[i][j] = Math.max(0, Math.max(A[i - 1][j - 1] + getCost(seq1.charAt(i - 1), seq2.charAt(j - 1)), a));
                if (A[i][j] > max) {
                    max = A[i][j]; max_i = i; max_j = j;
                }
            }
        }
//        System.out.println("Score: "+max/10.0);
        return new AlignmentMax(max_i, max_j, max, mode);
    }

    public int fillMatrixGlobal() {
        for (int i = 1; i < seq1.length() + 1; i++) {
            for (int j = 1; j < seq2.length() + 1; j++) {
                I[i][j] = Math.max(A[i - 1][j] + g(1), I[i - 1][j] + gapextend);
                D[i][j] = Math.max(A[i][j - 1] + g(1), D[i][j - 1] + gapextend);
                int a = Math.max(D[i][j], I[i][j]);
                A[i][j] = Math.max(A[i - 1][j - 1] + getCost(seq1.charAt(i - 1), seq2.charAt(j - 1)), a);
            }
        }
//        System.out.println("Score: "+A[seq1.length()][seq2.length()]/10.0);
        return A[seq1.length()][seq2.length()];
    }
    
    public String[] backtrackingLocal(AlignmentMax max) {
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        int i = seq1.length(), j = seq2.length();
        while(i > max.getMax()[0]){
            i--;
            s1.append(seq1.charAt(i));
            s2.append('-');
        }
        while(j > max.getMax()[1]){
            j--;
            s1.append('-');
            s2.append(seq2.charAt(j));
        }
        while(A[i][j] != 0){
            if(A[i][j] == (A[i - 1][j - 1] + getCost(seq1.charAt(i - 1), seq2.charAt(j - 1)))){
                i--;j--;
                s1.append(seq1.charAt(i));
                s2.append(seq2.charAt(j));
            }
            else if (A[i][j] == I[i][j]) {
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
        while(j>0){
            j--;
            s1.append('-');
            s2.append(seq2.charAt(j));
        }
        while(i>0){
            i--;
            s1.append(seq1.charAt(i));
            s2.append('-');
        }
        return new String[] {s1.reverse().toString(), s2.reverse().toString()};
    }

    public String[] backtrackingFreeshift(AlignmentMax max) {
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        int i = seq1.length(), j = seq2.length();
        while(i > max.getMax()[0]){
            i--;
            s1.append(seq1.charAt(i));
            s2.append('-');
        }
        while(j > max.getMax()[1]){
            j--;
            s1.append('-');
            s2.append(seq2.charAt(j));
        }
        while (i > 0 && j > 0) {
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
        if (i == 0) {
            while (j > 0) {
                s2.append(seq2.charAt(j - 1));
                s1.append('-');
                j--;
            }
        } else if (j == 0) {
            while (i > 0) {
                s1.append(seq1.charAt(i - 1));
                s2.append('-');
                i--;
            }
        }
        return new String[]{s1.reverse().toString(), s2.reverse().toString()};
    }

    public String[] backtrackingGlobal() {
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();
        int i = seq1.length(), j = seq2.length();
        while (i > 0 && j > 0) {
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
        if (i == 0) {
            while (j > 0) {
                s2.append(seq2.charAt(j - 1));
                s1.append('-');
                j--;
            }
        } else if (j == 0) {
            while (i > 0) {
                s1.append(seq1.charAt(i - 1));
                s2.append('-');
                i--;
            }
        }
        return new String[] {s1.reverse().toString(), s2.reverse().toString()};
    }

    public double checkScoreFreeshift(String s1, String s2){
        int left = 0, right = s1.length()-1, score = 0;
        if(s1.startsWith("-")){
            while(s1.charAt(left) == '-'){
                left++;
            }
        }
        else if(s2.startsWith("-")){
            while(s2.charAt(left) == '-'){
                left++;
            }
        }
        if(s2.endsWith("-")){
                while(s2.charAt(right) == '-'){
                    right--;
                }
            }
            else if(s1.endsWith("-")){
                while(s1.charAt(right) == '-'){
                    right--;
                }
            }
        for (int i = left; i <= right; i++) {
            if(s1.charAt(i) == '-' || s2.charAt(i) == '-'){
                int k=1;
                while((i+k)<s1.length() && (s1.charAt(i+k) == '-' || s2.charAt(i+k) == '-')){
                    k++;
                }
                score += g(k);
                i += (k-1);
            }
            else{
                score += getCost(s1.charAt(i), s2.charAt(i));
            }
        }
        return score/10.0;
    }
    
    private double checkScoreGlobal(String s1, String s2){
        int score = 0;
        for (int i = 0; i < s1.length(); i++) {
            if(s1.charAt(i) == '-'){
                int k=1;
                while((i+k)<s1.length() && s1.charAt(i+k) == '-'){
                    k++;
                }
                score += g(k);
                i += (k-1);
            }
            else if(s2.charAt(i) == '-'){
                int k=1;
                while((i+k)<s2.length() && s2.charAt(i+k) == '-'){
                    k++;
                }
                score += g(k);
                i += (k-1);
            }
            else{
                score += getCost(s1.charAt(i), s2.charAt(i));
            }
        }
        return score/10.0;
    }
    
    private double checkScoreLocal(String s1, String s2){
        int end = -1, start = -1, score = 0;
        for (int i = 0; i < s1.length(); i++) {
            if(s1.charAt(i) != '-' && s2.charAt(i) != '-'){
                start = i;
                break;
            }
        }
        for (int i = s1.length()-1; i >= 0; i--) {
            if(s1.charAt(i) != '-' && s2.charAt(i) != '-'){
                end = i;
                break;
            }
        }
        for (int i = start; i <= end; i++) {
            if(s1.charAt(i) != '-' && s2.charAt(i) != '-'){
                score += getCost(s1.charAt(i), s2.charAt(i));
            }
            else{
                int k=1;
                while((i+k)< s1.length() && (s1.charAt(i+k) == '-' || s2.charAt(i+k) == '-')){
                    k++;
                }
                score += g(k);
                i += (k-1);
            }
        }
        return score/10.0;
    }
    
    private int g(int n) {
        return gapopen + n * gapextend;
    }

    private int getCost(char i, char j) {
        return matrix[i - 65][j - 65];
    }

    private void initParams(HashMap<String, String> params) throws IOException {
        Parser parser = new Parser();
        matrix = params.containsKey("-m") ? parser.parseMatrix(params.get("-m"), true) : parser.parseMatrix("dayhoff", true);
        gapopen = params.containsKey("-go") ? (new Double(Double.parseDouble(params.get("-go")) * 10)).intValue() : -120;
        gapextend = params.containsKey("-ge") ? (new Double(Double.parseDouble(params.get("-ge")) * 10)).intValue() : -10;
        mode = params.containsKey("-mode") ? params.get("-mode") : "freeshift";
        String aa = "ARNDCQEGHILKMFPSTWYV";
        aminoAcids = new HashMap<>();
        for (int i = 0; i < aa.length(); i++) {
            aminoAcids.put(aa.charAt(i), i);
        }
        int size = 5000;
        A = new int[size + 1][size + 1];
        I = new int[size + 1][size + 1];
        D = new int[size + 1][size + 1];
        for (int i = 1; i < size + 1; i++) {//init
            A[i][0] = (mode.equals("global"))? g(i) : 0;
            D[i][0] = -99999999;
        }
        for (int i = 1; i < size + 1; i++) {
            A[0][i] = (mode.equals("global"))? g(i) : 0;
            I[0][i] = -99999999;
        }
    }
}
