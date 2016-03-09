package com.stella.ccda.extractor.entry;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import com.stella.utils.Utils;

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
		
		//System.out.println(Utils.nodeToString(entry));
		
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
		
		XPathExpression timeStampXpathExp = Utils.getXPathExpression("./substanceAdministration/effectiveTime/@value");
		return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
	}	
}
