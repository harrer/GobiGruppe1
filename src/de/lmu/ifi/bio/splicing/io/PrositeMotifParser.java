package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;

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
            String line, name = null, pattern = null, id = null;
            String[] split;
            String delimit = ";?\\s+";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ID")) {
                    split = line.split(delimit);
                    name = split[1];
                } else if (line.startsWith("AC")) {
                    split = line.split(delimit);
                    id = split[1];
                } else if (line.startsWith("PA")) {
                    split = line.split(delimit);
                    if (pattern == null) {
                        pattern = split[1];
                    } else {
                        pattern += split[1];
                    }
                } else if (line.startsWith("//")) {
                    if (id != null) {
                        addPattern(id, name, pattern);
                        id = null;
                        name = null;
                        pattern = null;
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.printf("[PrositeMotifParser]: %s%n", file);
        }
    }

    private static void addPattern(String id, String name, String pattern) {
        //TODO save to mysql database
    }
}
