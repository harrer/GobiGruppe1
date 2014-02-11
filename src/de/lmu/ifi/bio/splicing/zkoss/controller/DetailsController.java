package de.lmu.ifi.bio.splicing.zkoss.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;

import de.lmu.ifi.bio.splicing.genome.Gene;
import de.lmu.ifi.bio.splicing.genome.Transcript;
import de.lmu.ifi.bio.splicing.util.FileUtil;
import de.lmu.ifi.bio.splicing.zkoss.util.ApplicationLogger;
import de.lmu.ifi.bio.splicing.zkoss.util.ConfigGetter;

/**
 * Controller for the details.zul which should display spicing information for a specific gene
 * @author pesch
 *
 */
public class DetailsController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	private Gene gene;
	
	@Wire
	Grid transcriptGrid;
	
	@Wire
	Button exampleBtn;
	
	@Wire
	Div jmolDiv;
	
	List<File> templates = new ArrayList<File>();
	
	/**
	 * Loads the first PDB file given in the templates list. Only used for demonstrations.
	 * @throws IOException
	 */
	@Listen("onClick=#exampleBtn")
	public void onClick() throws IOException{
		
		if ( templates != null && templates.size() > 0){
			loadPDB(templates.get(0));
		}
	}
	
	/**
	 * Reads a PDB file and invokes the client loadModel function (see details.zul)
	 * @param pdbFile -- pdbFile on locale system
	 * @throws IOException when pdbFile not found
	 */
	public void loadPDB(File pdbFile) throws IOException{
		String pdbContent = FileUtil.readFileInBuffer(pdbFile).toString(); //the PDB file containing ATOM positions 
		pdbContent = pdbContent.replaceAll("\n", "\\\\n"); //replace line breaks
		//actual loadModel() (java script function in details.zul)
		Clients.evalJavaScript(String.format("loadModel(\"%s\")",pdbContent));
	}

	/**
	 * If you have to handle the components after ZK Loader initializing them, you could override SelectorComposer.doAfterCompose(T). 
	 * It is important to call back super.doAfterCompose(comp). Otherwise, the wiring won't work. 
	 * It also means that none of the data members are wired before calling super.doAfterCompose(comp).
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		File gtfFile = new File(ConfigGetter.getInstance().getValue("GTFFILE"));    
		File pdbStructureDir = new File(ConfigGetter.getInstance().getValue("PDB_FILE_DIR"));
		
		Long start = null;
		Long end = null;

		//read URL parameters
		//list of templates for current gene
		if ( Executions.getCurrent().getParameter("templates") != null){
			 String[] templatesStr = Executions.getCurrent().getParameter("templates").split(";");
			 for(String t : templatesStr){
				 if ( t.trim().length() == 0) continue;
				 File pdb = new File(pdbStructureDir + "/" +t.replace(":", ".") + ".pdb");
					if ( pdb.exists()){
						templates.add(pdb);
						ApplicationLogger.getLogger().debug(String.format("Register PDB %s",t));
					}else{
						 Messagebox.show(String.format("Can not find PDB %s",pdb), "Warning", Messagebox.OK, Messagebox.EXCLAMATION);
					}
			 }
			 
			 ApplicationLogger.getLogger().debug(String.format("Templates: %s", Arrays.asList(templates)));
		}
		if ( templates.size() > 0){
			jmolDiv.setVisible(true);
		}else{
			jmolDiv.setVisible(false);
		}
		
		//start & end position in GTF
		if ( Executions.getCurrent().getParameter("start") != null){
			start = Long.valueOf((String) Executions.getCurrent().getParameter("start"));
		}
		if ( Executions.getCurrent().getParameter("end") != null){
			end = Long.valueOf((String) Executions.getCurrent().getParameter("end"));
		}
	
		if ( start!=null && end != null )
		{
			gene = new FileUtil.GTFReader(gtfFile).setStartOffset(start).setEndOffset(end).getGene();
			transcriptGrid.setModel(new ListModelList<Transcript>(gene.getTranscripts()));
			ApplicationLogger.getLogger().debug(String.format("Gene %s from GTF loaded with %d transcripts",gene.getGeneId(),gene.getTranscripts().size()));
		}
		
	}
}
