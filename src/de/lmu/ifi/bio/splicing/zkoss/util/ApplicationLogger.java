package de.lmu.ifi.bio.splicing.zkoss.util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Proxy for the log4j logger
 * @author pesch
 *
 */
public class ApplicationLogger {
	private static Logger logger;
	private ApplicationLogger(){}
	
	public static Logger getLogger()  {
		if ( logger == null){ 
			PropertyConfigurator.configure(ConfigGetter.getInstance().getProperties());
		
			logger = Logger.getRootLogger();
			
			logger.info("Logger started");
		}
		return logger;
	}
}
