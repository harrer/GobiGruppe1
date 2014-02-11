package de.lmu.ifi.bio.splicing.zkoss.data;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.bio.splicing.util.StringUtil;

/**
 * Java bean containing information for the INDEX file
 * @author pesch
 *
 */
public class GeneModellingInformation implements Comparable<GeneModellingInformation>  {
	/**
	 * Representation of structure templates which can be used to model the isoforms of the current gene.
	 * For each template the best fitting protein for the current gene (geneId) together with the sequence coverage and sequence identify is stored.
	 * @author pesch
	 *
	 */
	public static class Template implements Comparable<Template> {
		private String proteinId;
		
		private String pdbId;
		private float sequenceIdentitify;
		private float coverage;
		
		public Template(String proteinId, String pdbId, float coverage, float sequenceIdentitify) {
			this.proteinId = proteinId;
			this.pdbId = pdbId;
			this.sequenceIdentitify = sequenceIdentitify;
			this.coverage = coverage;
		}

		public float getCoverage(){
			return coverage;
		}
		public String getPdbId() {
			return pdbId;
		}

		public float getSequenceIdentitify() {
			return sequenceIdentitify;
		}
		@Override
		public int compareTo(Template o) {
			return new Float(o.getCoverage()).compareTo(this.getCoverage());
		}
		@Override
		public String toString(){
			return String.format("%s;%s;%.2f;%.2f",pdbId,proteinId,coverage,sequenceIdentitify);
		}
		
	}
	
	private Integer numberIsoforms;
	private String geneId;
	private String geneName;
	private List<Template> templates;
	private long startPosition;
	private long endPosition;
	public String getGeneId() {
		return geneId;
	}
	public List<Template> getTemplates() {
		return templates;
	}
	/**
	 * Returns the URL for the details page.
	 * @return
	 */
	public String getLink(){
		return String.format("details.zul?geneId=%s&start=%d&end=%d&templates=%s",this.geneId,this.getStartPosition(),this.getEndPosition(),this.getTemplatesAsString(';'));
	}
	public long getStartPosition() {
		return startPosition;
	}
	public long getEndPosition() {
		return endPosition;
	}
	public GeneModellingInformation(String geneId, String geneName, Integer numberIsoforms, List<Template> templates, long startPosition, long endPosition) {
		this.geneId = geneId;
		this.geneName = geneName;
		this.templates = templates;
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		this.numberIsoforms = numberIsoforms;
	}
	public Integer getNumberIsoforms() {
		return numberIsoforms;
	}
	public String getGeneName() {
		return geneName;
	}
	
	public float getMaxCoverage(){
		float coverage = 0;
		for(Template template : templates){
			coverage = Math.max(coverage, template.getCoverage());
		}
		return coverage;
	}
	public float getMaxSeqId(){
		float seqId = 0;
		for(Template template : templates){
			seqId = Math.max(seqId, template.getSequenceIdentitify());
		}
		return seqId;
	}
	public String getTemplatesAsString(char seperator){
		List<String> tmpAsStr = new ArrayList<String>();
		for(Template template : templates){
			tmpAsStr.add(template.getPdbId());
		}
		return StringUtil.getAsString(tmpAsStr, seperator );
	}
	public String getTemplatesAsString(){
		return getTemplatesAsString(' ');
	}
	@Override
	public String toString(){
		return String.format("%s\t%s\t%d\t%d\t%d\t%s\n",
				this.getGeneId(),
				this.getGeneName(),
				this.getStartPosition(),
				this.getEndPosition(),
				this.numberIsoforms,
				StringUtil.getAsString(templates, '|')
				);
	}
	@Override
	public int compareTo(GeneModellingInformation o) {
		return this.getGeneId().compareTo(o.getGeneId());
	}


}
