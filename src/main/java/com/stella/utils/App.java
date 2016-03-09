package com.stella.utils;


/**
 * Shamsi
 * @author ali
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	try {
    		//TBD take folder path and read all files iteratively
    		ParserConfig config = ParserConfig.getInstance();
    		String filePath = config.get(ParserConfig.DATASET_DIR_PATH);
    		
    		CcdaSectionExtractor extractor = new CcdaSectionExtractor();
    		extractor.extract(filePath);
    		
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    }
}
