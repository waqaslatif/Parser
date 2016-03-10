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
	private static final String INSERT_PROBLEM_QUERY = "INSERT INTO records.\"ActiveProblem\"(m2hid, name,"
			+ " noteddate, timestamp, activeproblemgroupid) VALUES ('%s', '%s', '%s', '%s', '%s');";
	
	private String groupId;
	
	@Override
	public String extractData(final Node entry) throws XPathExpressionException {
		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			final String problemName = extractName(entry);
			final String notedDate = extractNotedDate(entry);
			final String timestamp = extractTimestamp(entry);
			return buildQuery(problemName, notedDate, timestamp);
		}
		return null;
	}
	
	@Override
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String buildQuery(String problemName, String notedDate, String timestamp) {
		return String.format(INSERT_PROBLEM_QUERY, M2HID, problemName, notedDate, timestamp, groupId);
	}
	
	private String extractName(final Node entry) throws XPathExpressionException{
		return Utils.getStringNode(entry, "entryRelationship/observation["
				+ "templateId/@root='" + PROBLEM_ID_OBSERVATION + "']/value/@displayName");
	}
	
	private String extractNotedDate(final Node entry) throws XPathExpressionException{
		final String notedDate = Utils.getStringNode(entry, "entryRelationship/observation["
				+ "templateId/@root='" + PROBLEM_ID_OBSERVATION + "']/effectiveTime/low/@value");
		if (notedDate.trim().equals("")) {
			return Utils.getCurrentDate();
		} else {
			return notedDate;
		}
	}
	
	private String extractTimestamp(final Node entry) throws XPathExpressionException{
		final String timestamp = Utils.getStringNode(entry, "effectiveTime/low/@value");
		if (timestamp.trim().equals("")) {
			return Utils.getCurrentDate();
		} else {
			return timestamp;
		}
	}
	

}
