package com.stella.ccda.extractor.entry;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProgressNoteEntryExtractor implements CcdaEntryExtractor {
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

	@Override
	public void setGroupId(String groupId) {
		// TODO Auto-generated method stub
		
	}

}
