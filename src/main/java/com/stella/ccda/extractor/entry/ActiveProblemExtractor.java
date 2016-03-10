package com.stella.ccda.extractor.entry;

import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

import com.stella.utils.Utils;

/**
 * @author ali
 *
 */
public class ActiveProblemExtractor implements CcdaEntryExtractor {
	
	private static final String M2HID = "200";
	private static final String PROBLEM_ID_OBSERVATION = "2.16.840.1.113883.10.20.22.4.4";
	
	
	public String extractData(final Node entry) throws XPathExpressionException {
		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			String problemName = extractName(entry);
			String notedDate = extractNotedDate(entry);
			String timestamp = extractTimestamp(entry);
			
			if (notedDate.trim().equals("")) {
				notedDate = Utils.getCurrentDate();
			}
			
			if (timestamp.trim().equals("")) {
				timestamp = Utils.getCurrentDate();
			}
			
			return buildQuery(problemName, notedDate, timestamp);
		}
		return null;
	}
	
	public String buildQuery(String problemName, String notedDate, String timestamp) {
		final String query = "INSERT INTO records.\"ActiveProblem\"(id, m2hid, name, noteddate, timestamp)"
				+ " VALUES (" + M2HID + ", '" + problemName + "', '"
				+ notedDate + "','" + timestamp + "');";
		return query;
	}
	
	private String extractName(final Node entry) throws XPathExpressionException{
		return Utils.getStringNode(entry, "entryRelationship/observation["
				+ "templateId/@root='" + PROBLEM_ID_OBSERVATION + "']/value/@displayName");
	}
	
	private String extractNotedDate(final Node entry) throws XPathExpressionException{
		return Utils.getStringNode(entry, "entryRelationship/observation["
				+ "templateId/@root='" + PROBLEM_ID_OBSERVATION + "']/effectiveTime/low/@value");
	}
	
	private String extractTimestamp(final Node entry) throws XPathExpressionException{
		return Utils.getStringNode(entry, "effectiveTime/low/@value");
	}

	@Override
	public void setGroupId(String groupId) {
		// TODO Auto-generated method stub
		
	}
	

}
