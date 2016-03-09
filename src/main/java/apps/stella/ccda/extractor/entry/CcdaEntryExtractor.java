package apps.stella.ccda.extractor.entry;

import org.w3c.dom.Node;

public interface CcdaEntryExtractor {
	
	String extractData(Node entry);
	
	String getSql(Node entry);

}
