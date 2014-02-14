package de.lmu.ifi.bio.splicing.io;

import com.sun.accessibility.internal.resources.accessibility;
import com.sun.accessibility.internal.resources.accessibility_en;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSP;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DsspRunner {

    public static DSSPData getDsspAcc(String protein, String pdbDirectory) {
        Runtime rt = Runtime.getRuntime();
        StreamWrapper error, output;
        String out = "";
        try {
            Process proc = rt.exec("dsspcmbi " + pdbDirectory + protein + ".pdb");
            error = new StreamWrapper(proc.getErrorStream(), "ERROR");
            output = new StreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
//			System.out.println("Output: " + output.message + "\nError: "
//					+ error.message);
            out = output.message;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean acids = false;
        List<Integer> accessibility = new ArrayList<>();
        List<Character> secondaryStructure = new ArrayList<>();
        for (String line : out.split("\n")) {
            if (acids) {
                if (line.length() > 1) {
                    accessibility.add(Integer.parseInt(line.substring(35, 38).replaceAll(" ", "")));
                    secondaryStructure.add(line.charAt(16));
                } else
                    break;
            } else if (line.startsWith("  #")) {
                acids = true;
            }
        }
        return new DSSPData(accessibility.toArray(new Integer[0]), secondaryStructure.toArray(new Character[0]));
    }

    static class StreamWrapper extends Thread {
        InputStream is = null;
        String type = null;
        String message = null;

        public String getMessage() {
            return message;
        }

        StreamWrapper(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        public void run() {
            try {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                StringBuffer buffer = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                message = buffer.toString();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
