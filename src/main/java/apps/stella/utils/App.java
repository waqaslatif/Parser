package apps.stella.utils;


/**
 * Shamsi
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    	try {

    		//TBD take folder path and read all files iteratively
    		
    		String filePath = "C:\\Users\\Mansoor\\Desktop\\HUGO CCDA\\HU\\FI\\200601\\HUTTEN_FINLEY_RILEY__02_02_16_1317_1703_20.xml";
    		
    		CcdaSectionExtractor extractor = new CcdaSectionExtractor();
    		extractor.extract(filePath);
    		
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
    }
}
