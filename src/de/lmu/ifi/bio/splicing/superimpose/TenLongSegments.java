package de.lmu.ifi.bio.splicing.superimpose;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author harrert
 */
public class TenLongSegments {

    public static ArrayList<DoubleMatrix2D> tenLongSegments(String file) throws IOException{
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        String[] split;
        ArrayList<double[]> backbone = new ArrayList();
        while ((line = br.readLine()) != null) {
            split = line.split("\\t");
            backbone.add(new double[]{Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3])});
        }
        br.close();
        double[][] bbArray = backbone.toArray(new double[0][0]);
        ArrayList<DoubleMatrix2D> tenLongs = new ArrayList<>();
        for (int i = 0; i <= backbone.size()-10; i++) {
            double[][] tmp = new double[10][3];
            for (int j = 0; j < tmp.length; j++) {
                tmp[j] = bbArray[i+j];
            }
            tenLongs.add(new DenseDoubleMatrix2D(tmp));
        }
        return tenLongs;
    }
    
    public static ArrayList<String[]> readcInpairs(String file) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line, s = "";
        String[] split;
        int c = 0;
        ArrayList<Boolean> bList = new ArrayList<>();
        ArrayList<String[]> pairsList = new ArrayList<>();
        while((line = br.readLine()) != null){
            split = line.split("\\s");
            bList.add(split[2].equalsIgnoreCase(split[3]));
            if(split[2].equalsIgnoreCase(split[3])){c++;}
            pairsList.add(new String[]{split[0], split[1]});
        }
//        System.out.println(c+" matches");
        br.close();
        ArrayList<String[]> out = new ArrayList<>();
        for (int i=0; i<bList.size(); i++) {
            if(bList.get(i)){
                out.add(new String[]{pairsList.get(i)[0], pairsList.get(i)[1]});
            }
        }
        return out;//return new ArrayList[]{bList, pairsList};
    }
    
    public static void main(String[] args) throws IOException {
//        HashMap<String, String> inpairs = readcInpairs("/home/proj/biosoft/praktikum/genprakt-ws13/assignment1/cathscop.inpairs");//"/Users/Tobias/Desktop/cathscop.inpairs";
        String file = "/Users/Tobias/Dropbox/UNI/GoBi/Blatt 4/1r5ra00.backbone";
        ArrayList<DoubleMatrix2D> m = tenLongSegments(file);
        DoubleMatrix2D P = m.get(43);
        DoubleMatrix2D Q = m.get(49);
        //Object[] s = new Superposition().superimpose(P, Q);
        System.out.println("");
    }
    
}
