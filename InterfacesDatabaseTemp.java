interface Exon {
	long start, stop;
	int frame;

	Exon(long start, long stop, int frame);
	public long getStart();
	public long getStop();
	public long getFrame();
}

interface Transcript {
	abstract List<Exon> list;
	abstract String transcript_id, protein_id;

	Transcript abstract(String transcript_id, String protein_id);

	abstract public void addExon(Exon exon);

	abstract public List<Exon> getExons();
	abstract public String getTranscriptId();
	abstract public String getProteinId();
}

interface Gene {
	HashMap<String, Transcript> hashmap_transcriptid; //transcript_id
	HashMap<String, Transcript> hashmap_proteinid; //protein_id als key
	String gene_id, chromosome;
	boolean strand;

	Gene(String gene_id, boolean strand, String chromosome);

	public void addTranscript(Transcript transcript);

	public Transcript getTranscriptByTranscriptId(String transcript_id);
	public Transcript getTranscriptByProteinId(String protein_id);
	public String getGeneId();
	public boolean getStrand();
}

interface Genes {
	HashMap<String, Genes> genes; //key=gene_id

	Genes();

	public void addGene(Gene gene);

	// public List<AbstractExon> getIsoform(String transcript_id);
	// public List<AbstractTranscript> getTranscriptsByGeneId(String gene_id);
	// public List<AbstractGene> getGenesFromList(List<String> gene_ids);

	public Gene getGeneById(String gene_id);
	public List<Gene> getGeneByList(List<String> gene_ids);
	public List<Gene> getGeneByPartId(String gene_id); //z.b. nur "ENSG00023*"
}