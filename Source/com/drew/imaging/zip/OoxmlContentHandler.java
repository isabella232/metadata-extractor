package com.drew.imaging.zip;

import com.drew.imaging.FileType;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Payton Garland
 */
public class OoxmlContentHandler extends DefaultHandler
{
    FileType fileType = FileType.Zip;

    @Override
    public void startDocument() throws SAXException
    {

    }

    @Override
    public void endDocument() throws SAXException
    {

    }

    @Override
    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes atts)
        throws SAXException
    {

        if (localName != null && localName.equals("Relationship")) {
            if (atts != null) {
                if (atts.getValue("Type") != null && atts.getValue("Target") != null) {
                    if (atts.getValue("Type").equals("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument")) {
                        switch (atts.getValue("Target")) {
                            case "word/document.xml":
                                fileType = FileType.Docx;
                                break;
                            case "xl/workbook.xml":
                                fileType = FileType.Xlsx;
                                break;
                            case "ppt/presentation.xml":
                                fileType = FileType.Pptx;
                                break;
                        }
                    }
                }
            }
        }

    }

    public FileType getFileType()
    {
        return fileType;
    }
}
