package com.drew.imaging.zip;

import com.drew.imaging.FileType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Payton Garland
 */
public class OoxmlFilter extends ZipFilter
{
    private boolean containsRels;
    private boolean isDocument;
    private boolean isWorkbook;
    private boolean isPresentation;

    public OoxmlFilter()
    {
        containsRels = false;
        isDocument = false;
        isWorkbook = false;
        isPresentation = false;
    }

    @Override
    public void filterEntry(ZipEntry entry, ZipInputStream inputStream)
    {
        if (entry.getName().equals("_rels/.rels")) {
            containsRels = true;
            detectOoxmlType(inputStream);
        }
    }

    @Override
    HashMap<List<Boolean>, FileType> createConditionsMap()
    {
        HashMap<List<Boolean>, FileType> conditionsMap = new HashMap<List<Boolean>, FileType>();

        List<Boolean> docx = Arrays.asList(containsRels, isDocument);
        conditionsMap.put(docx, FileType.Docx);
        List<Boolean> pptx = Arrays.asList(containsRels, isPresentation);
        conditionsMap.put(pptx, FileType.Pptx);
        List<Boolean> xlsx = Arrays.asList(containsRels, isWorkbook);
        conditionsMap.put(xlsx, FileType.Xlsx);

        return conditionsMap;
    }

    private void detectOoxmlType(ZipInputStream inputStream)
    {
       StringBuilder relsContent = new StringBuilder();
       try {
           for (int i = inputStream.read(); i != -1; i = inputStream.read()) {
                relsContent.append((char)i);
           }

           SAXParserFactory spf = SAXParserFactory.newInstance();
           spf.setNamespaceAware(true);
           SAXParser saxParser = spf.newSAXParser();

           XMLReader xmlReader = saxParser.getXMLReader();
           OoxmlContentHandler handler = new OoxmlContentHandler();
           xmlReader.setContentHandler(handler);
           xmlReader.parse(new InputSource(new ByteArrayInputStream(relsContent.toString().getBytes())));
           switch (handler.getFileType()) {
               case Docx:
                   isDocument = true;
                   break;
               case Xlsx:
                   isWorkbook = true;
                   break;
               case Pptx:
                   isPresentation = true;
                   break;
           }
       } catch (Exception ignored) {

       }
    }
}
