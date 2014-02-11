package de.lmu.ifi.bio.splicing.genome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.lmu.ifi.bio.splicing.util.Pair;

public class Protein {
	
	private String proteinId;
	private Integer taxId; 
	private String refSeqIdAndVersion;
	
	private Transcript parentTranscript;
	private List<Exon> codingExons=null;
	
	public String getRefSeqIdAndVersion() {
		return refSeqIdAndVersion;
	}
	public Transcript getParentTranscript() {
		return parentTranscript;
	}

	public Protein(String proteinId, Transcript parentTranscript ){
		this.proteinId = proteinId;
		this.parentTranscript = parentTranscript;
		this.codingExons = new ArrayList<Exon>();
	}
	
	
	public String getProteinId() {
		return proteinId;
	}
	public String getTranscriptIdentifier() {
		return parentTranscript.getTranscriptId();
	}
	public String getGeneIdentifier() {
		return parentTranscript.getParentGene().getGeneId();
	}
	public Integer getTaxId() {
		return taxId;
	}
	public Integer getSize() {
		Integer size = 0;
		for(Exon exon: codingExons){
			size+=(exon.getEnd()-exon.getStart());
			
		}
		return  size;
	}
	

	public static HashMap<String,String> readMapping(List<Protein> proteins,List<File> files) throws Exception{
		HashMap<String,String> ret = new HashMap<String,String>();
		HashSet<String> genes = new HashSet<String>();
		
		for(File file : files){
			BufferedReader br = new BufferedReader(new FileReader(file));
			String currentLine = br.readLine();
			while( (currentLine = br.readLine())!=null){
				String[] tokens = currentLine.split("\t");
				if ( tokens.length < 2) continue;
				String proteinId = tokens[0];
				String refSeqId = tokens[1].split("\\.")[0];
				
				if ( ret.containsKey(refSeqId) || genes.contains(proteinId)){
					System.out.println("Ambigious:" + currentLine);
					continue;
				}
				genes.add(tokens[0]);
				ret.put(refSeqId,proteinId);
				
			}
			br.close();
		}
		return ret;
	}


	public List<Exon> getCodingExons() {
		return codingExons;
	}
	public void add(Exon exon) {
		codingExons.add(exon);
	}
	
	@Override
	public String toString(){
		if ( this.codingExons != null)
			return this.proteinId  + "  " +this.codingExons.toString() ;
		else
			return this.proteinId;
	}

}
