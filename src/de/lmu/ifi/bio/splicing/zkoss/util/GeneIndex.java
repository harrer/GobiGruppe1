package de.lmu.ifi.bio.splicing.zkoss.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.util.FileUtil;
import de.lmu.ifi.bio.splicing.util.Pair;
import de.lmu.ifi.bio.splicing.zkoss.data.GeneModellingInformation;
import de.lmu.ifi.bio.splicing.zkoss.data.GeneModellingInformation.Template;

/**
 * Writes the reads (by creating GeneModellingInformation objects)  the Index file used for the web service.
 * @author pesch
 *
 */
public class GeneIndex {
	
	//the main index data!
	private List<GeneModellingInformation> data;
	/**
	 * @return the Index data
	 */
	public List<GeneModellingInformation> getData() {
		return data;
	}
	/**
	 * Creates an index from the PDB-protein mapping file and the GTF file
	 * @param proteinPDBMapping -- mapping file
	 * @param gtfFile -- gtf file
	 * @throws IOException
	 */
	public GeneIndex(File proteinPDBMapping, File gtfFile) throws IOException{
		this.data = new ArrayList<GeneModellingInformation> ();
		
		System.out.println("Reading mapping file");
		HashMap<String, List<Template>> mapping = readProteinPDBMapping(proteinPDBMapping);
		System.out.println("Reading GTF");
		List<Gene> genes = new FileUtil.GTFReader(gtfFile).setTranscriptType("protein_coding").getGenes(); //list of genes (only with protein_coding products)
		System.out.println("Number of (protein coding) genes:\t" + genes.size());
		
		System.out.println("Generating index");
		HashMap<String, Pair<Long, Long>> index = FileUtil.getGTFIndex(gtfFile); //creates a look of for each gene
		
		Locale.setDefault(Locale.US);
		//creates GeneModellingInformation for each gene
		for(Gene gene : genes){ 
			List<Template> templates = new ArrayList<Template>();
			if ( mapping.containsKey(gene.getGeneId())){
				HashMap<String,List<Template>> aggregation = new HashMap<String,List<Template>>();
				for(Template template :  mapping.get(gene.getGeneId())){
					if (! aggregation.containsKey(template.getPdbId())){
						aggregation.put(template.getPdbId(), new ArrayList<Template>());
					}
					aggregation.get(template.getPdbId()).add(template);
				}
				for(Entry<String, List<Template>> e : aggregation.entrySet()){
					Template bestFit = e.getValue().get(0);
					for(Template template : e.getValue()){ //naive best fit
						if ( template.getCoverage() > 0.4f && template.getSequenceIdentitify() > bestFit.getSequenceIdentitify()){
							bestFit = template;
						}
					}
					templates.add(bestFit);
				}
				
			}
			
			Pair<Long, Long> gtfPosition = index.get(gene.getGeneId());
			GeneModellingInformation gm = new GeneModellingInformation(gene.getGeneId(),gene.getGeneName(),gene.getTranscripts().size(), templates, gtfPosition.first, gtfPosition.second);
			data.add(gm);
		}
	
	}
	/**
	 * Reads a previous written Index file
	 * @param indexFile -- the index file
	 * @throws IOException
	 */
	public GeneIndex(File indexFile) throws IOException{
		this.data = new ArrayList<GeneModellingInformation> ();
		BufferedReader br = new BufferedReader(new FileReader(indexFile));
		String line = br.readLine();
		while (( line = br.readLine())!=null){
			String[] tokens = line.split("\t");
			String geneId = tokens[0];
			String geneName = tokens[1];
			Long offsetStart = Long.valueOf(tokens[2]);
			Long offsetEnd = Long.valueOf(tokens[3]);
			Integer numberIsoforms = Integer.valueOf(tokens[4]);
			List<Template> templates = new ArrayList<Template>();
			if ( tokens.length == 6){
				String[] infos = tokens[5].split("\\|");
				
				for(String info : infos){
					String[] infoTokens = info.split(";");
					String pdb  = infoTokens[0];
					String proteinId = infoTokens[1];
					Float coverage  =Float.valueOf(infoTokens[2]);
					Float seqId  =Float.valueOf(infoTokens[3]);
					Template template = new Template(proteinId,pdb,coverage,seqId);
					templates.add(template);
				}
			}
			GeneModellingInformation gm = new GeneModellingInformation(geneId, geneName, numberIsoforms, templates, offsetStart, offsetEnd);
			this.data.add(gm);
		}
		
		br.close();
	}
	
	/**
	 * Main method to write a gene GTF and structure index (see /bin/ for bash script)
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException{
		HelpFormatter lvFormater = new HelpFormatter();
		CommandLineParser parser = new BasicParser();
		
		
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("gtfFile").withArgName("FILE").isRequired().hasArgs(1).create("gtfFile"));
		options.addOption(OptionBuilder.withLongOpt("proteinPDBMapping").withArgName("FILE").isRequired().hasArgs(1).create("proteinPDBMapping"));
		options.addOption(OptionBuilder.withLongOpt("out").withArgName("FILE").isRequired().hasArgs(1).create("out"));

		CommandLine line = null;
		try{
			line = parser.parse( options, args );
		}catch(Exception e){
			System.err.println( e.getMessage());
			lvFormater.printHelp(120, "java " + GeneIndex.class.getName(), "", options, "", true);
			System.exit(1);
		}
		File gtfFile = new File(line.getOptionValue("gtfFile"));
		File proteinPDBMapping = new File(line.getOptionValue("proteinPDBMapping"));
		File out = new File(line.getOptionValue("out"));
		
		GeneIndex index = new GeneIndex(proteinPDBMapping,gtfFile);
		System.out.println("Writing output");
		index.writeIndex(out);
		
	}
	
	/**
	 * Write the index file
	 * @param out -- output file
	 * @throws IOException
	 */
	public void writeIndex( File out) throws IOException{
		
		Locale.setDefault(Locale.US);
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		bw.write("ENSEMBLGENE_ID\tGENENAME\tGTFSTART\tGTFEND\tISOFORMS\tTEMPLATES\n");
		for(GeneModellingInformation gm : data){
			bw.write(gm.toString());
		}
		bw.flush();
		bw.close();
	}
	
	/**
	 * Reads a pdb-gene/protein mapping file. 
	 * @param file -- mapping file
	 * @return gene id -> template list
	 * @throws IOException
	 */
	private HashMap<String,List<Template>> readProteinPDBMapping(File file) throws IOException{
		HashMap<String,List<Template>> ret = new HashMap<String,List<Template>>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		while((line = br.readLine())!=null){
			String[] tokens = line.split("\t");
			String pdb = tokens[0];
			String proteinId = tokens[7];
			String geneId = tokens[8];
			Integer proteinLength = Integer.valueOf(tokens[10]);
			Float seqId = Float.valueOf(tokens[11]);
			Integer start = Integer.valueOf(tokens[12]);
			Integer end = Integer.valueOf(tokens[13]);
			Integer covered = end-start;
			
			Float coverage =(float)covered/(float)proteinLength; 
			Template template = new Template(proteinId, pdb, coverage, seqId);
			if (! ret.containsKey(geneId)){
				ret.put(geneId, new ArrayList<Template>());
			}
			ret.get(geneId).add(template);
		}
		br.close();
		return ret;
	}
}
