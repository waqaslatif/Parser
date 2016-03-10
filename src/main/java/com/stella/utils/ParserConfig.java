package com.stella.utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stella.ccda.extractor.entry.ImmunizationSectionExtractor;

/**
 * @author ali
 *
 */
public class ParserConfig {
	

	public static final String DATASET_DIR_PATH = "datasetDir";
	public static final String SQL_SCRIPT_FILE_PATH = "sqlScriptFilePath";
	
	private static final Logger LOG = LoggerFactory.getLogger(ParserConfig.class);
	
	private static ParserConfig instance= null;
	private Properties prop =  new Properties();
	private Map<String, String> propMap = null;
	
	/**
	 * Loads the configurations from the configuration file.
	 */
	private ParserConfig(){
		final String fileName = "conf/config.properties";
		propMap = new HashMap<String, String>();
		LOG.info("loading configuration :");
		InputStream is;
		try {
			is = ClassLoader.getSystemResourceAsStream(fileName);
			prop.load(is);
			for( Entry<Object, Object> entry:prop.entrySet()){
				propMap.put((String) entry.getKey(), (String) entry.getValue());
			}
			
			LOG.info("configurations Loaded ... ");
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch(IOException | NullPointerException e){
			e.printStackTrace();
		}
		
	}
	
	public static ParserConfig getInstance(){
		if(instance == null){
			instance = new ParserConfig();
		}
		return instance;
	}
	
	public Map<String, String> getConfigMap(){
		return propMap;
	}
	
	public String get(String propertyName){
		return propMap.get(propertyName);
	}
	
}
