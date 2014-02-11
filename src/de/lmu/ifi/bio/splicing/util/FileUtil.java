package de.lmu.ifi.bio.splicing.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.zkoss.io.Files;

import de.lmu.ifi.bio.splicing.genome.Exon;
import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Protein;
import de.lmu.ifi.bio.splicing.genome.Strand;
import de.lmu.ifi.bio.splicing.genome.Transcript;




/**
 * Collection of commonly used methods to read files
 * @author rpesch
 *
 */
public class FileUtil {
	private static Pattern pattern = Pattern.compile("\t");
	private static Pattern annotation = Pattern.compile("([^\\s]+)\\s+\"([^;]+)\";");

	public static StringBuffer readFileInBuffer(File file) throws IOException{

		return Files.readAll(new FileReader(file));

	}
	
	public static File checkFile(String fileStr) throws IOException{
		File file = new File(fileStr);
		if (!file.exists()) throw new IOException(String.format("File %s not found",fileStr));
		return file;
	}
	/**
	 * Reads a GTF index file and provides a gene look up map
	 * @param indexFile the GTF index file
	 * @return look-up
	 * @throws IOException when indexFile can not be opened
	 */
	public static HashMap<String,Pair<Integer,Integer>> readIndex(File indexFile) throws IOException{
		HashMap<String, Pair<Integer, Integer>> gtfIndex = new HashMap<String,Pair<Integer,Integer>>();
		BufferedReader br = new BufferedReader(new FileReader(indexFile));
		String line = null;
		while((line = br.readLine())!=null){
			String[] tokens = line.split("\t");
			String geneId = tokens[0];
			Integer start = Integer.valueOf(tokens[1]);
			Integer end = Integer.valueOf(tokens[2]);
			gtfIndex.put(geneId, new Pair<Integer,Integer>(start,end));
		}
		
		br.close();
		return gtfIndex;
	}
	/**
	 * Reads a GTF file and creates an index
	 * @param gtfFile the GTF file
	 * @return mapping of geneIds to start and end positions
	 * @throws IOException when gtfFile can not be read, or indexFile can be written
	 */
	public static HashMap<String,Pair<Long,Long>> getGTFIndex(File gtfFile) throws IOException{
		HashMap<String,Pair<Long,Long>>  index = new HashMap<String,Pair<Long,Long>>();
		
		String line = null;
		RandomAccessFile raf = new RandomAccessFile(gtfFile,"r");
		String currentGeneId = null;
		
		long start = 0;
		long lastLineOffset = 0;
		while((line=raf.readLine())!=null){
			String[] tokens = pattern.split(line);
			Matcher matcher = annotation.matcher(tokens[8]);
			while(matcher.find()){
				String type = matcher.group(1);
				if ( type.equals("gene_id") ) {
					if (currentGeneId != null &&  !currentGeneId.equals(matcher.group(2))){
						index.put(currentGeneId, new Pair<Long,Long>(start,lastLineOffset));
						start = lastLineOffset;
					}
					currentGeneId = matcher.group(2);
				}
				lastLineOffset = raf.getFilePointer();
				
			}
		}
		index.put(currentGeneId, new Pair<Long,Long>(start,lastLineOffset));
		raf.close();
		
		return index;
	}
	/**
	 * Reader for GTF files using the Builder pattern
	 * @author pesch
	 *
	 */
	public static class GTFReader{
		
		
		private File gtfFile;
		private String transcriptType;
		private List<String> chroms;
		private Long startOffset;
		private Long endOffset;
		
		
		public GTFReader(File gtfFile) {
			super();
			this.gtfFile = gtfFile;
		}
		
		public GTFReader setGtfFile(File gtfFile) {
			this.gtfFile = gtfFile;
			return this;
		}

		public GTFReader setTranscriptType(String transcriptType) {
			this.transcriptType = transcriptType;
			return this;
		}

		public GTFReader setChroms(List<String> chroms) {
			this.chroms = chroms;
			return this;
		}

		public GTFReader setStartOffset(Long startOffset) {
			this.startOffset = startOffset;
			return this;
		}

		public GTFReader setEndOffset(Long endOffset) {
			this.endOffset = endOffset;
			return this;
		}

		public Gene getGene() throws IOException{
			List<Gene> genes = getGenes();
			if ( genes.size() == 1)
				return genes.get(0);
			else{
				return null;
			}
		}
		/**
		 * Reads a GTF file with specified parameters
		 * @return list of genes
		 * @throws IOException when GTF file not found
		 */
		public List<Gene> getGenes() throws IOException{
			if ( !gtfFile.exists()){
				throw new IOException(gtfFile.getAbsoluteFile().toString() + " does not exist");
			}
			
			Set<String> foundChrosoms = new HashSet<String>();
			Set<String> toConsider = null;
			if (chroms != null) toConsider = new HashSet<String>(chroms);
			RandomAccessFile br = new RandomAccessFile(gtfFile,"r");
			String line = null;
			List<Gene> genes = new ArrayList<Gene>();
			Gene currentGene = null;
			Transcript currentTranscript = null;
			Protein currentProtein = null;
			if ( startOffset != null) br.seek(startOffset);
			while((line=br.readLine())!=null){
				if ( endOffset != null && br.getFilePointer() > endOffset) break;
				String[] tokens = pattern.split(line);
				String chrom = tokens[0];
				foundChrosoms.add(chrom);
				if ( chroms != null && !toConsider.contains(chrom)) continue;
				String frame = tokens[7];
				String tType = tokens[1]; //e.g. protein coding
				if ( transcriptType != null && !tType.equals(transcriptType)) continue;
				String annotationType = tokens[2]; // CDS, exon, start_codon, stop_codon
				Integer start = Integer.valueOf(tokens[3]);
				Integer end = Integer.valueOf(tokens[4]);
				Strand strand = null;
				if ( tokens[6].equals("+")){
					strand = Strand.PLUS;
				}else{
					strand = Strand.MINUS;
				}
				
				String geneId = null;
				String transcriptId = null;
				String transcriptName = null;
				String geneName = null;
				String proteinId = null;
				Matcher matcher = annotation.matcher(tokens[8]);
				String exonId = null;;
				
				while(matcher.find()){
					String type = matcher.group(1);
					if ( type.equals("gene_id")) geneId = matcher.group(2).toUpperCase(); //default for entity class (otherwise equals fails)
					else if ( type.equals("transcript_name")) transcriptName = matcher.group(2);
					else if ( type.equals("transcript_id")) transcriptId = matcher.group(2).toUpperCase(); 
					else if ( type.equals("gene_name")) geneName = matcher.group(2);
					else if ( type.equals("protein_id")) proteinId = matcher.group(2).toUpperCase();
					else if ( type.equals("exon_number")) exonId = "EXON" + Integer.valueOf(matcher.group(2));
				}
				if ( currentGene == null || !currentGene.getGeneId().equals(geneId)){
					if (currentGene != null ) genes.add(currentGene);
					currentGene = new Gene(chrom,geneId,geneName,strand,start,end);
				}
				if ( currentTranscript == null || !currentTranscript.getTranscriptId().equals(transcriptId)){
					currentTranscript = new Transcript(currentGene,transcriptId,transcriptName,tType);
					currentGene.addTranscript(currentTranscript);
				}
				if ( proteinId != null && (currentProtein == null || !currentProtein.getProteinId().equals(proteinId))){
					currentProtein = new Protein(proteinId,currentTranscript);
					currentTranscript.setProtein(currentProtein);
				}
				
				if ( annotationType.equals("exon")){
					Exon currentExon = new Exon(exonId,start,end);
					currentTranscript.addExon(currentExon);
				}
				if ( annotationType.equals("CDS")){
					Exon currentExon = new Exon(exonId,start,end); 
					currentExon.setCoding(Integer.valueOf(frame));
					currentProtein.add(currentExon);
				}
				
			}
			if ( currentGene != null) genes.add(currentGene);
			br.close();
			
			
			return genes;
		}
	}



}
