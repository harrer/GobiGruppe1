package de.lmu.ifi.bio.splicing.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Various static methods for the string formating
 * @author pesch
 *
 */
public class StringUtil {
	/**
	 * Returns a user specified string representation  of a collections 
	 * @param data -- the collection
	 * @param seperator -- the separator
	 * @return the collection represented as string separated by separator.
	 */
	public static String getAsString(Collection<?> data,  String format, char seperator){
		Iterator<?> it = data.iterator();
		boolean first = true;
		StringBuffer ret = new StringBuffer();
		while(it.hasNext()){
			if ( first == false){
				ret.append(seperator);
			}else{
				first = false;
			}
			ret.append(String.format(format,it.next()));
		}
		return ret.toString();
	}
	/**
	 * Returns a user specified string representation  of a collections 
	 * @param data -- the collection
	 * @param seperator -- the separator
	 * @return the collection represented as string separated by separator.
	 */
	public static String getAsString(Collection<?> data, char seperator){
		return getAsString(data,"%s",seperator);
	}
}
