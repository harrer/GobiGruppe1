package de.lmu.ifi.bio.splicing.zkoss.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tabbox;

import de.lmu.ifi.bio.splicing.zkoss.data.GeneModellingInformation;
import de.lmu.ifi.bio.splicing.zkoss.util.ApplicationLogger;
import de.lmu.ifi.bio.splicing.zkoss.util.ConfigGetter;
import de.lmu.ifi.bio.splicing.zkoss.util.GeneIndex;

/**
 * @author pesch
 *
 */
public class IndexController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	private List<GeneModellingInformation> data;
	
	@Wire
	private Grid geneGrid;
	
	@Wire
	private Tabbox tabbox;
	

	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//loads the Index file
		File index = new File(ConfigGetter.getInstance().getValue("INDEX"));
		ApplicationLogger.getLogger().debug(String.format("Index file: %s", index.toString() ));
		GeneIndex indexer = new GeneIndex(index);
		data = indexer.getData();
		geneGrid.setModel(new ListModelList<GeneModellingInformation>(data));
		
	
	}
}
