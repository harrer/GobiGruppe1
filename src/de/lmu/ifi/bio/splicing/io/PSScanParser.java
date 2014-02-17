package de.lmu.ifi.bio.splicing.io;

import de.lmu.ifi.bio.splicing.config.Setting;
import de.lmu.ifi.bio.splicing.zkoss.entity.PatternEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Carsten Uhlig on 16.02.14.
 */
public class PSScanParser {

    public static void readPSScanFastaResultFile(String file) {
        Path path = Setting.FS.getPath(file);
        try {
            BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8);
            String line, transcriptid = null, id = null;
            int start = 0, stop = 0;
            String regex = "^>(\\w+)/(\\d+)\\-(\\d+) : (\\w+).*";
            Pattern pat = Pattern.compile(regex);
            Matcher m = pat.matcher("");
            while ((line = br.readLine()) != null) {
                if (m.reset(line).matches()) {
                    transcriptid = m.group(1);
                    start = Integer.valueOf(m.group(2));
                    stop = Integer.valueOf(m.group(3));
                    id = m.group(4);
                } else {
                    if (transcriptid != null)
                        addPatternEventToDatabase(new PatternEvent(id, transcriptid, start, stop));
                    transcriptid = null;
                }
            }
            br.close();
        } catch (IOException e) {
            System.err.printf("[PSScanParser]: %s%n", file);
        }

    }

    private static void addPatternEventToDatabase(PatternEvent patternEvent) {
        Setting.dbu.insertPatternEvent(patternEvent);
    }

    public static void main(String[] args) {
        PSScanParser.readPSScanFastaResultFile(args[0]);
    }
}
