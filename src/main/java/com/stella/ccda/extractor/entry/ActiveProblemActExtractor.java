package com.stella.ccda.extractor.entry;

import java.text.ParseException;
import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stella.utils.Utils;

/**
 * @author ali
 *
 */
public class ActiveProblemActExtractor implements CCDElementExtractor {
	
	private static final String M2HID = "200";
    private static final String LINE_BREAK = "\n";
    
	private static final String ACTIVE_PROBLEM_ID = "2.16.840.1.113883.10.20.22.4.3";
    private static final String PROBLEM_ID_OBSERVATION = "2.16.840.1.113883.10.20.22.4.4";
	
    private static final String INSERT_PROBLEM_QUERY = "INSERT INTO records.\"ActiveProblem\"(m2hid, name,"
			+ " noteddate, timestamp, activeproblemgroupid) VALUES ('%s', '%s', '%s', '%s', '%s');";
    private static final String INSERT_PROBLEM_GROUP_QUERY = "INSERT INTO records.\"ActiveProblemGroup\"(id, m2hid, timestamp)"
			+ " VALUES ('%s', '%s', '%s');";
	private String groupId;
	
	
	/* (non-Javadoc)
	 * @see com.stella.ccda.extractor.entry.CCDElementExtractor#extract(org.w3c.dom.Document)
	 */
	@Override
	public String extract(Document document) throws XPathExpressionException,
			ParseException {
		final XPathExpression problemExp = Utils.getXPathExpression("//act[templateId"
				+ "/@root='" + ACTIVE_PROBLEM_ID + "']");
		final NodeList nList = (NodeList) problemExp.evaluate(document, XPathConstants.NODESET);
		final StringBuilder querybuilder = new StringBuilder();
		
		if (nList.getLength() > 0) {
			groupId = UUID.randomUUID().toString();
	        querybuilder.append(buildActiveProblemGroupSQL());
		}
		
		for (int temp = 0; temp < nList.getLength(); temp++) {
			final Node nNode = nList.item(temp);
			final String query = extractActiveProblem(nNode);
			querybuilder.append(LINE_BREAK);
			querybuilder.append(query);
		}
		return querybuilder.toString();
	}
	
	/**
	 * Extracts active problem discrete elements and compose sql query.
	 * @param entry active problem element
	 * @return SQL insert query for active problem
	 * @throws XPathExpressionException if the xpath used for extracting the elements is invalid.
	 */
	private String extractActiveProblem(final Node entry) throws XPathExpressionException {
		if (entry.getNodeType() == Node.ELEMENT_NODE) {
			final String problemName = extractName(entry);
			final String notedDate = extractNotedDate(entry);
			final String timestamp = extractTimestamp(entry);
			return buildQuery(problemName, notedDate, timestamp);
		}
		return null;
	}
	
	/**
	 * Builds SQL query of a single active problem record.
	 * @param problemName active problem name
	 * @param notedDate onset date
	 * @param timestamp 
	 * @return
	 */
	public String buildQuery(String problemName, String notedDate, String timestamp) {
		return String.format(INSERT_PROBLEM_QUERY, M2HID, problemName, notedDate, timestamp, groupId);
	}
	
	/**
	 * Extracts problem or condition name from the entry
	 * @param entry active problem element 
	 * @return active problem name
	 * @throws XPathExpressionException if the xpath used for extracting the name is invalid.
	 */
	private String extractName(final Node entry) throws XPathExpressionException{
		return Utils.getStringNode(entry, "entryRelationship/observation["
				+ "templateId/@root='" + PROBLEM_ID_OBSERVATION + "']/value/@displayName");
	}
	
	/**
	 * @param entry
	 * @return
	 * @throws XPathExpressionException
	 */
	private String extractNotedDate(final Node entry) throws XPathExpressionException{
		final String notedDate = Utils.getStringNode(entry, "entryRelationship/observation["
				+ "templateId/@root='" + PROBLEM_ID_OBSERVATION + "']/effectiveTime/low/@value");
		if (notedDate.trim().equals("")) {
			return Utils.getCurrentDate();
		} else {
			return notedDate;
		}
	}
	
	/**
	 * @param entry
	 * @return
	 * @throws XPathExpressionException
	 */
	private String extractTimestamp(final Node entry) throws XPathExpressionException{
		final String timestamp = Utils.getStringNode(entry, "effectiveTime/low/@value");
		if (timestamp.trim().equals("")) {
			return Utils.getCurrentDate();
		} else {
			return timestamp;
		}
	}
	
	/**
	 * @return
	 */
	private String buildActiveProblemGroupSQL(){
        return String.format(INSERT_PROBLEM_GROUP_QUERY, groupId, Utils.getM2hid(), Utils.getCurrentDate());
    }

}
