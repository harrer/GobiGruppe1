package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.structures.mapping.DSSPData;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
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
            if (error.message == null || error.message.length() > 0)
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
        ArrayList<double[]> c_alpha = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (acids) {
                    if (line.length() > 1) {
                        accessibility.add(Integer.parseInt(line.substring(35, 38).replaceAll(" ", "")));
                        secondaryStructure.add(line.charAt(16));
                        sequence.append(line.charAt(13));
                        String[] split = line.split("\\s+");
                        c_alpha.add(new double[]{Double.parseDouble(split[split.length - 3]), Double.parseDouble(split[split.length - 2]), Double.parseDouble(split[split.length - 1])});
                    } else
                        break;
                } else if (line.startsWith("  #")) {
                    acids = true;
                }
            }
        } catch (IOException | NumberFormatException | NullPointerException exception) {
        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
        DSSPData data = new DSSPData(accessibility.toArray(new Integer[0]), secondaryStructure.toArray(new Character[0]), sequence.toString(), proteinId);
        data.setCa_coordinates(c_alpha.toArray(new double[][]{}));
        return data;
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

    public static void saveSSDistribution(int[] distribution) {
        FileWriter fstream = null;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("/home/sch/schmidtju/Dokumente/SSDistribution.txt"));
            int overall = distribution[0] + distribution[1] + distribution[2];
            out.write("Helix: " + distribution[0] / (double) overall +
                    "\nExtended: " + distribution[1] / (double) overall +
                    "\nCoil: " + distribution[2] / (double) overall);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveExpectedSS(Collection<double[]> expSS, Collection<double[]> expAcc) {
        FileWriter fstream = null;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("/home/sch/schmidtju/Dokumente/Expectations.txt"));
            double[] expectedSSD = new double[3], expectedSSI = new double[3], expectedSSR = new double[3];
            for (double[] e : expSS) {
                for (int i = 0; i < 3; i++)
                    if (e[0] == 68)
                        expectedSSD[i] += e[i + 1];
                    else if (e[0] == 73)
                        expectedSSI[i] += e[i + 1];
                    else if (e[0] == 82)
                        expectedSSR[i] += e[i + 1];
            }
            out.write("Deletions" +
                    "\nHelix: " + expectedSSD[0] +
                    "\nExtended: " + expectedSSD[1] +
                    "\nCoil: " + expectedSSD[2] +
                    "\n\nInsertions" +
                    "\nHelis: " + expectedSSI[0] +
                    "\nExtended: " + expectedSSI[1] +
                    "\nCoil: " + expectedSSI[2] +
                    "\n\nReplaces" +
                    "\nHelix: " + expectedSSR[0] +
                    "\nExtended: " + expectedSSR[1] +
                    "\nCoil: " + expectedSSR[2]);
            double[] expectedAccD = new double[3], expectedAccI = new double[3], expectedAccR = new double[3];
            for (double[] e : expAcc) {
                for (int i = 0; i < 3; i++)
                    if (Math.abs(e[0] - 68) < 0.1)
                        expectedAccD[i] += e[i + 1];
                    else if (Math.abs(e[0] - 73) < 0.1)
                        expectedAccI[i] += e[i + 1];
                    else if (Math.abs(e[0] - 82) < 0.1)
                        expectedAccR[i] += e[i + 1];
            }
            out.write("\n\nDeletions" +
                    "\nBuried: " + expectedAccD[0] +
                    "\nPartly buried: " + expectedAccD[1] +
                    "\nExposed: " + expectedAccD[2] +
                    "\n\nInsertions" +
                    "\nBuried: " + expectedAccI[0] +
                    "\nPartly buried: " + expectedAccI[1] +
                    "\nExposed: " + expectedAccI[2] +
                    "\n\nReplaces" +
                    "\nBuried: " + expectedAccR[0] +
                    "\nPartly buried: " + expectedAccR[1] +
                    "\nExposed: " + expectedAccR[2]);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
