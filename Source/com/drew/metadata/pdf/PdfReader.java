package com.drew.metadata.pdf;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

/**
 * Reads PDF Metadata using Apache's PDFBox library
 *
 * @author Payton Garland
 */
public class PdfReader {
    public void extract(@NotNull final InputStream inputStream, @NotNull final Metadata metadata)
    {
        PdfDirectory directory = new PdfDirectory();
        metadata.addDirectory(directory);

        try {
            // Load PDF
            PDDocument pdDocument = PDDocument.load(inputStream);

            // PD Document Information data
            PDDocumentInformation docInfo = pdDocument.getDocumentInformation();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
            if (docInfo.getAuthor() != null) { directory.setString(PdfDirectory.TAG_AUTHOR, docInfo.getAuthor()); }
            if (docInfo.getCreationDate() != null) { directory.setString(PdfDirectory.TAG_CREATION_DATE, format.format(docInfo.getCreationDate().getTime())); }
            if (docInfo.getCreator() != null) { directory.setString(PdfDirectory.TAG_CREATOR, docInfo.getCreator()); }
            if (docInfo.getKeywords() != null) { directory.setString(PdfDirectory.TAG_KEYWORDS, docInfo.getKeywords()); }
            if (docInfo.getModificationDate() != null) { directory.setString(PdfDirectory.TAG_MOD_DATE, format.format(docInfo.getModificationDate().getTime())); }
            if (docInfo.getProducer() != null) { directory.setString(PdfDirectory.TAG_PRODUCER, docInfo.getProducer()); }
            if (docInfo.getSubject() != null) { directory.setString(PdfDirectory.TAG_SUBJECT, docInfo.getSubject()); }
            if (docInfo.getTitle() != null) { directory.setString(PdfDirectory.TAG_TITLE, docInfo.getTitle()); }
            if (docInfo.getTrapped() != null) { directory.setString(PdfDirectory.TAG_TRAPPED, docInfo.getTrapped()); }

            // PD Document Page data
            directory.setFloat(PdfDirectory.TAG_WIDTH, pdDocument.getPage(0).getCropBox().getWidth());
            directory.setFloat(PdfDirectory.TAG_HEIGHT, pdDocument.getPage(0).getCropBox().getHeight());
            directory.setInt(PdfDirectory.TAG_PAGE_COUNT, pdDocument.getNumberOfPages());

            // PD Document XMP data
            XmpReader xmpReader = new XmpReader();
            if (pdDocument.getDocumentCatalog().getMetadata() != null) {
                xmpReader.extract(pdDocument.getDocumentCatalog().getMetadata().toByteArray(), metadata);
            }

            // Close PDF
            pdDocument.close();
        } catch (IOException e) {
            directory.addError("ERROR: IOException thrown during Pdf extraction - " + e.getMessage());
        }
    }
}
