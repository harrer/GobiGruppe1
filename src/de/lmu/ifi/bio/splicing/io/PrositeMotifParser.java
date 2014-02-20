package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.genome.Pattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by uhligc on 15.02.14.
 */
public class PrositeMotifParser {
    public static void readPrositeMotifFile(String file) {
        Path path = Setting.FS.getPath(file);
        try {
            BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            String line, name = null, pattern = null, id = null, description = null;
            String[] split;
            String delimit = "[,;]?\\s+";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ID")) {
                    split = line.split(delimit);
                    id = split[1].replace(";", "");
                } else if (line.startsWith("AC")) {
                    split = line.split(delimit);
                    name = split[1].replace(";", "");
                } else if (line.startsWith("PA")) {
                    split = line.split(delimit);
                    if (pattern == null) {
                        pattern = split[1].replace(";", "");
                    } else {
                        pattern += split[1].replace(";", "");
                    }
                } else if (line.startsWith("DE")) {
                    split = line.split("   ");
                    description = split[1].replace(";", "");
                } else if (line.startsWith("MA") && line.contains("")) {
                } else if (line.startsWith("//")) {
                    if (id != null) {
                        addPattern(id, name, pattern, description.substring(0, description.length() - 1));
                        id = null;
                        name = null;
                        pattern = null;
                        description = null;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.printf("[PrositeMotifParser]: %s%n", file);
        }
    }

    private static void addPattern(String id, String name, String pattern, String description) {
        if (description != null) {
            if (pattern != null) {
                Setting.dbu.insertPattern(new Pattern(id, name, pattern.replace(".", ""), description));
            } else {
                Setting.dbu.insertPattern(new Pattern(id, name, true, description));
            }
        } else {
            if (pattern != null) {
                Setting.dbu.insertPattern(new Pattern(id, name, pattern.replace(".", "")));
            } else {
                Setting.dbu.insertPattern(new Pattern(id, name, true));
            }
        }
    }
}
