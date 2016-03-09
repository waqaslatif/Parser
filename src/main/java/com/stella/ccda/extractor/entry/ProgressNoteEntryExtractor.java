package com.stella.ccda.extractor.entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProgressNoteEntryExtractor implements CcdaEntryExtractor {
    private final String PROGRESS_NOTE_SECION_ID = "1.3.6.1.4.1.19376.1.5.3.1.3.4";
    final int m2hid = 200;
    String reportContent;
    String sourceAddress;
    String timeStamp;

    public String extractData(final Node entry) {

        System.out.println("\n Current Element :" + entry.getNodeName());

        if (entry.getNodeType() == Node.ELEMENT_NODE) {

            Element eElement = (Element) entry;

        }
        return null;
    }

}
