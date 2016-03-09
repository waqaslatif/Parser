package apps.stella.ccda.extractor.entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ImmunizationEntryExtractor implements CcdaEntryExtractor {
	
	public String extractData(final Node entry) {
		
		System.out.println("\nCurrent Element :" + entry.getNodeName());
		
		if (entry.getNodeType() == Node.ELEMENT_NODE) {

			Element eElement = (Element) entry;

			System.out.println("Staff id : " + eElement.getAttribute("id"));
			System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
			System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
			System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
			System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

		}
		return null;
	}

	public String getSql(final Node entry) {
		// TODO Auto-generated method stub
		return null;
	}

}
