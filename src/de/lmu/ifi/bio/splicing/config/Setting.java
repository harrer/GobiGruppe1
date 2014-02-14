package de.lmu.ifi.bio.splicing.config;

import de.lmu.ifi.bio.splicing.jsqlDatabase.DBQuery;
import de.lmu.ifi.bio.splicing.jsqlDatabase.DBUpdate;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by uhligc on 12.02.14.
 */
public class Setting {
    public static final String GTFFILEPATH = "/home/proj/biosoft/GENOMIC/HUMAN/Homo_sapiens.GRCh37.73.gtf";
    public static final File GTFFILE = new File(GTFFILEPATH);
    public static final FileSystem FS = FileSystems.getDefault();
    public static final Path GTFPATH = FS.getPath(GTFFILEPATH);
    public static final String PDBREPCCHAINSDIR = "/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/STRUCTURES/";
    public static final String PDBCATHSCOPDIR = "/home/proj/biosoft/PROTEINS/CATHSCOP/STRUCTURES/";
    public static final String TMALIGNDIR = "/home/proj/biosoft/PROTEINS/software/TMalign/";
    public static final String BLASTDIR = "/home/proj/biosoft/PROTEINS/PDB_REP_CHAINS/BLAST/";
    public static final String GTFDIR = "/home/proj/biosoft/GENOMIC/HUMAN/HUMAN_GENOME_FASTA/";
    public static final DBQuery dbq = new DBQuery();
    public static final DBUpdate dbu = new DBUpdate();
}
