package com.stella.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.stella.ccda.extractor.entry.CcdaEntryExtractor;
import com.stella.ccda.extractor.entry.ImmunizationEntryExtractor;
import com.stella.ccda.extractor.entry.ProgressNoteEntryExtractor;

public class CcdaSectionExtractor {

    private Document doc;

    private final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder dBuilder;

    private final String IMMUNIZATION_SECION_ID = "2.16.840.1.113883.10.20.22.2.2.1";
    private final String PROGRESS_NOTE_SECION_ID = "1.3.6.1.4.1.19376.1.5.3.1.3.4";

    private final CcdaEntryExtractor immunizationExtractor = new ImmunizationEntryExtractor();
    private final CcdaEntryExtractor progressNoteEntryExtractor = new ProgressNoteEntryExtractor();

    public void extract(final String filePath) {

        try {

            File xmlFile = new File(filePath);

            System.out.println("----------------------------");

            System.out.println("Reading File : " + xmlFile.getName());

            dBuilder = dbFactory.newDocumentBuilder();

            doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            // M Utils.printDocument(doc, System.out);

            // extractImmunizationSection(doc);
            extractProgressNoteSection(doc);

            // ActiveProblemExtractor.extractActiveProblem(doc);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String extractProgressNoteSection(final Document doc) throws XPathExpressionException {

        System.out.println("----------------------------");

        System.out.println("Reading Progress Note Section");

        final Node sectionNode = extractSectionByID(doc, "//section[templateId/@root='" + PROGRESS_NOTE_SECION_ID
                + "']");

        final NodeList entryList = getSectionEntries(sectionNode, "entry");

        System.out.println("EntryList Size = " + entryList.getLength());

        for (int temp = 0; temp < entryList.getLength(); temp++) {

            Node entryNode = entryList.item(temp);

            String sql = progressNoteEntryExtractor.extractData(entryNode);

            System.out.println("--------------" + sql);
        }

        return "";
    }

    private String extractImmunizationSection(final Document doc) throws XPathExpressionException {

        System.out.println("----------------------------");

        System.out.println("Reading Immunization Section");

        final Node sectionNode = extractSectionByID(doc, "//section[templateId/@root='" + IMMUNIZATION_SECION_ID + "']");

        // System.out.println(Utils.nodeToString(sectionNode));

        final NodeList entryList = getSectionEntries(sectionNode, "entry");

        for (int temp = 0; temp < entryList.getLength(); temp++) {

            Node entryNode = entryList.item(temp);

            // System.out.println("----------------------------");

            // System.out.println(Utils.nodeToString(entryNode));

            String sql = immunizationExtractor.extractData(entryNode);
        }

        return "";
    }

    private Node extractSectionByID(final Document doc, String sectionXpath) throws XPathExpressionException {
        XPathExpression sectionXpathExp = Utils.getXPathExpression(sectionXpath);
        return (Node) sectionXpathExp.evaluate(doc, XPathConstants.NODE);
    }

    private NodeList getSectionEntries(final Node sectionNode, String entryXpath) throws XPathExpressionException {
        XPathExpression entryXpathExp = Utils.getXPathExpression(entryXpath);
        return (NodeList) entryXpathExp.evaluate(sectionNode, XPathConstants.NODESET);
    }

}
