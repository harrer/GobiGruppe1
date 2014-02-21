package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by schmidtju on 14.02.14.
 */
public class DSSPParser {

    public static DSSPData runDSSP(String proteinId, String pdbDirectory) {
        Runtime rt = Runtime.getRuntime();
        StreamWrapper error, output;
        String out = "";
        try {
            System.out.println("/home/proj/biosoft/PROTEINS/software/dsspcmbi " + pdbDirectory + proteinId + ".pdb");
            Process proc = rt.exec("/home/proj/biosoft/PROTEINS/software/dsspcmbi " + pdbDirectory + proteinId + ".pdb");
            error = new StreamWrapper(proc.getErrorStream(), "ERROR");
            output = new StreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;
            error.start();
            output.start();
            error.join(3000);
            output.join(3000);
            exitVal = proc.waitFor();
            if(error.message == null || error.message.length() > 0)
                return null;
            out = output.message;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean acids = false;
        List<Integer> accessibility = new ArrayList<>();
        List<Character> secondaryStructure = new ArrayList<>();
        StringBuilder sequence = new StringBuilder();
        for (String line : out.split("\n")) {
            if (acids) {
                if (line.length() > 1) {
                    accessibility.add(Integer.parseInt(line.substring(35, 38).replaceAll(" ", "")));
                    secondaryStructure.add(line.charAt(16));
                    sequence.append(line.charAt(13));
                } else
                    break;
            } else if (line.startsWith("  #")) {
                acids = true;
            }
        }
        return new DSSPData(accessibility.toArray(new Integer[0]), secondaryStructure.toArray(new Character[0]), sequence.toString(), proteinId);
    }

    public static DSSPData parseDSSPFile(File file, String proteinId) {
        boolean acids = false;
        List<Integer> accessibility = new ArrayList<>();
        List<Character> secondaryStructure = new ArrayList<>();
        StringBuilder sequence = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (acids) {
                    if (line.length() > 1) {
                        accessibility.add(Integer.parseInt(line.substring(35, 38).replaceAll(" ", "")));
                        secondaryStructure.add(line.charAt(16));
                        sequence.append(line.charAt(13));
                    } else
                        break;
                } else if (line.startsWith("  #")) {
                    acids = true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DSSPData(accessibility.toArray(new Integer[0]), secondaryStructure.toArray(new Character[0]), sequence.toString(), proteinId);
    }

    public static DSSPData getDSSPData(String proteinId) {
        File f = new File(Setting.DSSPDIR);
        File[] matchingFiles = f.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".dssp");
            }
        });
        for (File file : matchingFiles) {
            if (file.getName().startsWith(proteinId)) {
                return parseDSSPFile(file, proteinId);
            }
        }
//        return runDSSP(proteinId, Setting.PDBREPCCHAINSDIR);
        return null;
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
                String line;
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
