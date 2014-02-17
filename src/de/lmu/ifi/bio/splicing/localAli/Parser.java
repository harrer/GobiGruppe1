package de.lmu.ifi.bio.splicing.localAli;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Parser {

    private final String path = "/home/proj/biosoft/praktikum/genprakt-ws13/assignment1/";
    //private final String path = "/home/tobias/Desktop/";

    public int[][] parseMatrix(String matrixName, boolean mirror) throws FileNotFoundException, IOException {
        String line;
        FileReader fReader = new FileReader(path+"matrices/"+matrixName+".mat");
        BufferedReader reader = new BufferedReader(fReader);
        double[][] matrix = new double[20][20];
        int ln = 0;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("MATRIX")) {
                String[] split = line.split("\\s");
                double[] d = new double[20];
                int column = 0;
                for (int i = 0; i < split.length; i++) {
                    if(split[i].matches("-?[0-9]+(\\.)?([0-9]+)?")){
                        d[column] = Double.parseDouble(split[i]);
                        column++;
                    }
                }
                for (int i = column; i < 20; i++) {
                    d[i] = Double.NEGATIVE_INFINITY;
                }
                matrix[ln] = d;
                ln++;
            }
        }
        if (mirror) {
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    if (Double.compare(matrix[i][j], Double.NEGATIVE_INFINITY) == 0) {
                        matrix[i][j] = matrix[j][i];
                    }
                }
            }
        }
        reader.close();
        fReader.close();
        int[][] mOut = new int[26][26];
        String aa = "ARNDCQEGHILKMFPSTWYV";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                mOut[aa.charAt(i)-65][aa.charAt(j)-65] = new Double(matrix[i][j]*10).intValue();
            }
        }
        return mOut;
    }
    
    public HashMap<String, String> parseParams(String[] args) throws ParamException {
        HashMap<String, String> params = new HashMap<>();
        if (args.length < 4 || !args[0].equals("-pairs") || !args[2].equals("-seqlib")) {
            throw new ParamException("less than 4/wrong params");
        } else {
            if (args[1].matches("[a-zA-Z0-9_\\-]+\\..*pairs")) {//".+\\.(in|out)?pairs"
                params.put("-pairs", path + args[1]);
            }
            else if(args[1].matches("/([a-zA-Z0-9_\\-]+/)*[a-zA-Z0-9_\\-]+\\..*pairs")){
                params.put("-pairs", args[1]);
            }else {
                throw new ParamException("provide a valid .pairs file or path"); 
            }
            if (args[3].matches("[a-zA-Z0-9_\\-]+\\.seqlib")) {
                params.put("-seqlib", path + args[3]);
            }
            else if(args[3].matches("/([a-zA-Z0-9_\\-]+/)*[a-zA-Z0-9_\\-]+\\..*seqlib")){
                params.put("-seqlib", args[3]);
            }else {
                throw new ParamException("provide a valid .seqlib file or path"); 
            }
            for (int i = 4; i < args.length; i++) {//store parameters
                switch (args[i]) {
                    case ("-m"):
                        if (i < args.length - 1 && args[i + 1].matches("BlakeCohenMatrix|dayhoff|THREADERSimilarityMatrix|blosum50|pam250")) {
                            params.put("-m", args[i + 1]);
                        } else {
                            throw new ParamException("Enter correct matrix name: BlakeCohenMatrix|dayhoff|THREADERSimilarityMatrix|blosum50|pam250");
                        }
                        break;
                    case ("-go"):
                        if (i < args.length - 1 && args[i + 1].matches("-?\\d+")) {
                            params.put("-go", args[i + 1]);
                        } else {
                            throw new ParamException("give a valid number");
                        }
                        break;
                    case ("-ge"):
                        if (i < args.length - 1 && args[i + 1].matches("-?\\d+")) {
                            params.put("-ge", args[i + 1]);
                        } else {
                            throw new ParamException("give a valid number");
                        }
                        break;
                    case ("-mode"):
                        if (i < args.length - 1 && args[i + 1].matches("local|global|freeshift")) {
                            params.put("-mode", args[i + 1]);
                        } else {
                            throw new ParamException("provide one of these modes: local|global|freeshift");
                        }
                        break;
                    case ("-printali"):
                        params.put("-printali", "true");
                        break;
                    case ("-printmatrices"):
                        if (i < args.length - 1 && args[i + 1].matches("txt|html")) {
                            params.put("-printmatrices", args[i + 1]);
                        } else {
                            throw new ParamException("format must be txt or html");
                        }
                        break;
                    case ("-check"):
                        params.put("-check", "true");
                        break;
                }
            }
        }
        return params;
    }

    public ArrayList<SeqPair> parsePairFile(String pairFile) throws FileNotFoundException, IOException {
        String line;
        ArrayList<SeqPair> list = new ArrayList<>();
        FileReader fReader = new FileReader(pairFile);
        BufferedReader reader = new BufferedReader(fReader);
        //String pattern = "(\\d+)\\s(\\d+)";
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            String[] split = line.split("\\s");
            list.add(new SeqPair(split[0], split[1]));
        }
        reader.close();
        fReader.close();
        return list;
    }

    public HashMap<String, String> parseSeqlib(String seqlibFile) throws FileNotFoundException, IOException {
        String line;
        HashMap<String, String> map = new HashMap<>();
        FileReader fReader = new FileReader(seqlibFile);
        BufferedReader reader = new BufferedReader(fReader);
        int max = -1;
        while ((line = reader.readLine()) != null) {
//            if (line.trim().isEmpty()) {
//                continue;
//            }
            String[] split = line.split(":");
            map.put(split[0], split[1]);
            if(Math.max(split[0].length(), split[1].length()) > max){
                max = Math.max(split[0].length(), split[1].length());
            }
        }
        map.put("_maxLength_", ""+max);
        reader.close();
        fReader.close();
        return map;
    }
}