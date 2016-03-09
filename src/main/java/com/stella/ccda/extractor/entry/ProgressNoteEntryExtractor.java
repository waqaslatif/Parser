package com.stella.ccda.extractor.entry;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.stella.utils.Utils;

public class ProgressNoteEntryExtractor implements CcdaEntryExtractor {
    String reportContent;
    String reportStatus;
    String sourceAddress;
    String timeStamp;

    public String extractData(final Node entry) throws DOMException, XPathExpressionException {

        // System.out.println("\n Current Element :" + Utils.nodeToString(entry));
        final String m2hid = Utils.getM2hid();

        if (entry.getNodeType() == Node.ELEMENT_NODE) {

            // System.out.println("------------- report content :" + innerXml(getReportContent(entry)));

            System.out.println("------------- report status :  " + getReportStatus(entry));

            final DateTime dateTime = new DateTime(DateTimeZone.UTC);

            System.out.println("------------- Timestamp :  " + dateTime.toString());

        }
        return "";
    }

    private Node getReportContent(final Node entry) throws XPathExpressionException {
        final XPathExpression sectionXpathExp = Utils.getXPathExpression("text");
        return (Node) sectionXpathExp.evaluate(entry, XPathConstants.NODE);
    }

    private String getReportStatus(final Node entry) throws XPathExpressionException {
        final XPathExpression sectionXpathExp = Utils.getXPathExpression("title/text()");
        return (String) sectionXpathExp.evaluate(entry, XPathConstants.STRING);
    }

    public void setGroupId(String groupId) {
        // TODO Auto-generated method stub

    }

}
