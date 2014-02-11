package de.lmu.ifi.bio.splicing.zkoss.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.zkoss.zk.ui.Sessions;

/**
 * Reads the applications config file (WEB-INF/data/wepapp.config) which includes 
 * file and path location and the log4j initialization.
 * Example:
 * <code>
 * 	File indexFile = new File(ConfigGetter.getInstance().getValue("INDEX"));
 * <code>
 * @author pesch
 *
 */
public class ConfigGetter {
	private static String CONFIG_FILE = new String("WEB-INF/data/wepapp.config");
	private static ConfigGetter instance;
	private Properties props;

	public Properties getProperties(){
		return props;
	}
	
	/**
	 * Constructor is private so that only an instance from the static getInstance() method can be created
	 * @param props
	 */
	private ConfigGetter(Properties props) {
		this.props = props;
	}
	/**
	 * Returns the valeu for a specific key e.g. INDEX
	 * @param option
	 * @return
	 */
	public String getValue(String key){
		return props.getProperty(key);
	}
	
	
	public Set<String> getProperties(String prefix) {
		HashSet<String> ret = new HashSet<String>();
		for(Object o : props.keySet()){
			if ( ((String)o).startsWith(prefix)){
				ret.add((String)o);
			}
		}
		return ret;
		
	}
	public static ConfigGetter getInstance(){
		if (instance == null) {
			try{
				File file = getFile(CONFIG_FILE);
				InputStream stream = new FileInputStream(file);
				Properties props = new Properties();
				props.load(stream);
				instance = new ConfigGetter(props);
			}catch(IOException e){ //in the case the config file can not be found
				throw new RuntimeException("Cannot init. application",e);
			}
		}
		return instance;
	}
	/**
	 * Locates the CONFIG_FILE file
	 * @param file -- relative file path
	 * @return  absolute file
	 * @throws IOException when file can not be found
	 */
	private static File getFile(String file) throws IOException{
		File ret = null;
		if ( Sessions.getCurrent() == null){ //not within ZKOSS context
			URL res = ConfigGetter.class.getClassLoader().getResource(file);
			ret = new File(res.getPath());
		}else{
			ret = new File(Sessions.getCurrent().getWebApp().getRealPath(file));
		}
			
		if (! ret.exists()){
			throw new IOException(String.format("Cannot find file  %s not found at %s",file,ret.toString()));
		}
		
		return ret;
	}
}
