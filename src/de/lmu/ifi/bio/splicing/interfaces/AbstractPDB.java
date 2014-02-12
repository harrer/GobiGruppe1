package de.lmu.ifi.bio.splicing.interfaces;

import java.io.File;
import java.nio.file.Path;
import de.lmu.ifi.bio.splicing.config.Setting;

/**
 * Created by uhligc on 12.02.14.
 */
public abstract class AbstractPDB {
    String pdbid;
    File file;
    Path filepath;

    public AbstractPDB(String pdbid) {
        this.pdbid = pdbid;
//        file = new File(pdbid);
//        filepath = Setting.FS.getPath(pdbid);
    }

    public File getFile() {
        return file;
    }
}
