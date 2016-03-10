package com.stella.ccda.extractor.entry;

import java.util.UUID;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.stella.utils.Utils;

/**
 * 
 * @author WaqasLatif
 *
 */
public class ProgressNoteEntryExtractor implements CcdaEntryExtractor {
    private String reportContent;
    private String reportStatus;
    private String sourceAddress;

    public String extractData(final Node progressNoteSection) throws DOMException, XPathExpressionException {

        final String sqlProgressNote = "INSERT INTO records.Report(id,m2hid, reportcontent, reportstatus, timestamp)"
                + "VALUES ('%s','%s', '%s', '%s', '%s');";

        final String m2hid = Utils.getM2hid();

        if (progressNoteSection.getNodeType() == Node.ELEMENT_NODE) {

            reportContent = getReportContent(progressNoteSection);
            reportStatus = getReportStatus(progressNoteSection);
            return String.format(sqlProgressNote, UUID.randomUUID().toString(), m2hid,
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

    public void setGroupId(final String groupId) {
        // TODO Auto-generated method stub

    }

}
