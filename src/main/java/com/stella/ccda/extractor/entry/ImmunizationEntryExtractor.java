package com.stella.ccda.extractor.entry;

import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import com.stella.utils.Utils;

public class ImmunizationEntryExtractor implements CcdaEntryExtractor {
	
	private String immGroupId;
	private String m2hid;
	private String name;
	private String datesPreviouslyGiven;
	private String nextDue;
	private String description;
	private String reportUrl;
	private String lastEnquiryDate;
	private String timeStamp;
	
	public String extractData(final Node entry) throws XPathExpressionException {	
		
		immGroupId = UUID.randomUUID().toString();
		
		String sqlImmunGroup = "INSERT INTO records.ImmunizationGroup(id, m2hid) "
								+ "VALUES('%s' , '%s');";
		
		System.out.println("----------------------------");
		
		System.out.println("Creating Immunization Group");
		
		sqlImmunGroup = String.format(sqlImmunGroup, immGroupId, m2hid);
		
		String sqlImmunization = "INSERT INTO records.Immunization(m2hid, name, datespreviouslygiven, nextdue, description, reporturl, lastenquirydate, timestamp, immuGroupId) "
					+ "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');";
		
		System.out.println("----------------------------");
		
		System.out.println("Reading Immunization Entry");
		
		//System.out.println(Utils.nodeToString(entry));
		
		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			
			m2hid = Utils.getM2hid();
			name = getNameFromNode(entry);
			datesPreviouslyGiven = getDatesPreviouslyGiven(entry);
			nextDue = getNextDue(entry);
			description = getDescription(entry);
			reportUrl = getReportUrl(entry);
			lastEnquiryDate = getLastEnquiryDate(entry);
			timeStamp = getTimeStampFromNode(entry);			
			
			sqlImmunization = String.format(sqlImmunization, m2hid, name, datesPreviouslyGiven, nextDue, description, reportUrl, lastEnquiryDate, timeStamp, immGroupId);	
			
			System.out.println("----------------------------");
			
			System.out.println("SQL Generated : " + sqlImmunization);

		}
		return sqlImmunization;
	}
	
	private String getNameFromNode(final Node entry) throws XPathExpressionException {
		
		//XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/consumable/manufacturedProduct/manufacturedMaterial/code/@displayName");
		//return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return "";
	}
	
	private String getDatesPreviouslyGiven(final Node entry) throws XPathExpressionException {
		
		//XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		//return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return "";
	}
		
	private String getNextDue(final Node entry) throws XPathExpressionException {
			
		//XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		//return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
		return "";
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
		return "";
	}
	
	private String getTimeStampFromNode(final Node entry) throws XPathExpressionException {
		
		XPathExpression timeStampXpathExp = Utils.getXPathExpression("substanceAdministration/effectiveTime/@value");
		return timeStampXpathExp.evaluate(entry,  XPathConstants.STRING).toString();
	}	
}
