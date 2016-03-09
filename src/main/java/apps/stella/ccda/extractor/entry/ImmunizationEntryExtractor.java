package apps.stella.ccda.extractor.entry;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ImmunizationEntryExtractor implements CcdaEntryExtractor {
	
	private String m2hid;
	private String name;
	private String datesPreviouslyGiven;
	private String nextDue;
	private String description;
	private String reportUrl;
	private String lastEnquiryDate;
	private String timeStamp;
	
	public String extractData(final Node entry) throws XPathExpressionException {
		
		System.out.println("----------------------------");
		
		System.out.println("Reading Immunization Section Entry");
		
		System.out.println(entry.getNodeType());
		
		if (entry.getNodeType() == Node.ELEMENT_NODE) {

			//System.out.println("name : " + eElement.getAttribute("id"));
			//System.out.println("datesPreviouslyGiven : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
			//System.out.println("nextDue : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
			//System.out.println("description : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
			//System.out.println("reportUrl : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
			//System.out.println("lastEnquiryDate : " );
			System.out.println("timeStamp : " + getTimeStampFromNode(entry));

		}
		return null;
	}
	
	private String getTimeStampFromNode(final Node entry) throws XPathExpressionException {
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
		String timeStampXpath = "/entry/substanceAdministration/effectiveTime/@value";                                  
		XPathExpression timeStampXpathExp = xpath.compile(timeStampXpath);
		return timeStampXpathExp.evaluate(entry, XPathConstants.STRING).toString();
	}	
}
