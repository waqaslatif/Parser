package com.stella.ccda.extractor.entry;

import java.text.ParseException;
import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.stella.utils.CCDSQLScriptBuilder;
import com.stella.utils.Utils;

/**
 * 
 * @author WaqasLatif
 *
 */
public class ProgressNoteSectionExtractor implements CCDElementExtractor {

	private static final Logger LOG = LoggerFactory.getLogger(ProgressNoteSectionExtractor.class);
	
	private static final String PROGRESS_NOTE_SECION_ID = "1.3.6.1.4.1.19376.1.5.3.1.3.4";
	
	final String INSERT_PROGRESS_NOTE_QUERY = "INSERT INTO records.\"Report\"(id,m2hid, reportcontent, reportstatus, timestamp)"
            + "VALUES ('%s','%s', '%s', '%s', '%s');";
	
	private String reportContent;
    private String reportStatus;

    @Override
	public String extract(Document document) throws XPathExpressionException,
			ParseException {
    	 LOG.info("----------------------------");
         LOG.info("Reading Progress Note Section");

         final Node sectionNode = Utils.extractSectionByID(document, "//section[templateId/@root='" + PROGRESS_NOTE_SECION_ID
                 + "']");
         if (sectionNode != null) {
             return extractProgressNotSection(sectionNode);
         }

         return "";
	}
    
    public String extractProgressNotSection(final Node progressNoteSection) throws DOMException, XPathExpressionException {

        final String m2hid = Utils.getM2hid();

        if (progressNoteSection.getNodeType() == Node.ELEMENT_NODE) {

            reportContent = getReportContent(progressNoteSection);
            reportStatus = getReportStatus(progressNoteSection);
            return String.format(INSERT_PROGRESS_NOTE_QUERY, UUID.randomUUID().toString(), m2hid,
                    reportContent.replaceAll("'", "\""), reportStatus, new DateTime(DateTimeZone.UTC));
        }
        return "";
    }

    private String getReportContent(final Node entry) throws XPathExpressionException {
        final XPathExpression sectionXpathExp = Utils.getXPathExpression("text");
        final Node reportContent = (Node) sectionXpathExp.evaluate(entry, XPathConstants.NODE);

        if (reportContent.getNodeType() == Node.ELEMENT_NODE) {
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < reportContent.getChildNodes().getLength(); i++) {
                buffer.append(Utils.nodeToString(reportContent.getChildNodes().item(i)));
            }

            return buffer.toString();
        }
        return null;
    }

    private String getReportStatus(final Node entry) throws XPathExpressionException {
        final XPathExpression sectionXpathExp = Utils.getXPathExpression("title/text()");
        return (String) sectionXpathExp.evaluate(entry, XPathConstants.STRING);
    }
	

}
