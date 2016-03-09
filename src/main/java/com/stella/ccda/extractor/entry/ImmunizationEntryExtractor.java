package com.stella.ccda.extractor.entry;

import java.text.ParseException;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stella.utils.Utils;

public class ImmunizationEntryExtractor implements CcdaEntryExtractor {
	
	private String immunGroupId;
	private String m2hid;
	private String name;
	private String datesPreviouslyGiven;
	private String nextDue;
	private String description;
	private String reportUrl;
	private String lastEnquiryDate;
	private String timeStamp;
	
	@Override
	public void setGroupId(final String groupId) {
		this.immunGroupId =  groupId;
	}
	
	@Override
	public String extractData(final Node entry) throws XPathExpressionException, ParseException {	
		
		final String nameRef = getNamRefFromNode(entry);
		
		System.out.println("----------------------------");		
		System.out.println("Name Ref : " + nameRef);
		
		
		String sqlImmunization = "INSERT INTO records.Immunization(m2hid, name, datespreviouslygiven, nextdue, description, reporturl, lastenquirydate, timestamp, immunGroupId) "
					+ "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
		
		System.out.println("----------------------------");		
		System.out.println("Reading Immunization Entry");
		
		//System.out.println(Utils.nodeToString(entry));
		
		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			
			m2hid = Utils.getM2hid();
			
			name = getNameFromNode(entry, nameRef);
			datesPreviouslyGiven = getDatesPreviouslyGiven(entry, nameRef);
			nextDue = getNextDue(entry, nameRef);
			description = getDescription(entry);
			reportUrl = getReportUrl(entry);
			lastEnquiryDate = getLastEnquiryDate(entry);
			timeStamp = getTimeStampFromNode(entry);	
			timeStamp = Utils.formatStringtoDbDate(timeStamp, "yyyyMMdd");
			
			sqlImmunization = String.format(sqlImmunization, m2hid, name, datesPreviouslyGiven, nextDue, description, reportUrl, lastEnquiryDate, timeStamp, immunGroupId);	
			
			System.out.println("----------------------------");			
			System.out.println("SQL Generated : " + sqlImmunization);

		}
		return sqlImmunization;
	}
	
	private String getNamRefFromNode(final Node entry) throws XPathExpressionException {
		
		XPathExpression nameRefXpathExp = Utils.getXPathExpression("substanceAdministration/text/reference/@value");
		String nameRef =  nameRefXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return nameRef = nameRef.replace("#", "");
	}
	
	private String getNameFromNode(final Node entry, final String nameRef) throws XPathExpressionException {
		
		XPathExpression nameXpathExp = Utils.getXPathExpression("../text/table/tbody/tr[@ID='" + nameRef + "']/td[1]/text()");
		return nameXpathExp.evaluate(entry,  XPathConstants.STRING).toString();		
	}
	
	private String getDatesPreviouslyGiven(final Node entry, final String nameRef) throws XPathExpressionException {
		
		String strDates = "";
		
		XPathExpression datesPreviouslyXpathExp = Utils.getXPathExpression("../text/table/tbody/tr[@ID='" + nameRef + "']/td[2]/content");
		NodeList datesList = (NodeList) datesPreviouslyXpathExp.evaluate(entry,  XPathConstants.NODESET);
		
		for (int temp = 0; temp < datesList.getLength(); temp++) {

            Node dateNode = datesList.item(temp);

            //System.out.println("----------------------------");
            //System.out.println("Dates : " + Utils.nodeToString(dateNode));
            
            if(temp > 0) {
            	strDates += " , ";
            }

            strDates += Utils.nodeToString(dateNode);
        }
		
		return strDates;
	}
		
	private String getNextDue(final Node entry, final String nameRef) throws XPathExpressionException {
			
		XPathExpression nextDueXpathExp = Utils.getXPathExpression("../text/table/tbody/tr[@ID='" + nameRef + "']/td[3]/text()");
		return nextDueXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
	}
	
	private String getDescription(final Node entry) throws XPathExpressionException {
		
		//XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		//return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return "";
	}
	
	private String getReportUrl(final Node entry) throws XPathExpressionException {
		
		//XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		//return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return "";
	}
	
	private String getLastEnquiryDate(final Node entry) throws XPathExpressionException {
		
		//XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		//return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return Utils.getCurrentDate();
	}
	
	private String getTimeStampFromNode(final Node entry) throws XPathExpressionException {
		
		XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
	}	
}
