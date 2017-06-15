package com.drew.metadata.pdf;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Metadata;
import com.drew.metadata.xmp.XmpReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.common.PDMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Payton Garland
 */
public class PdfReader {
    public void extract(@NotNull final InputStream inputStream, @NotNull final Metadata metadata)
    {
        PdfDirectory directory = new PdfDirectory();
        metadata.addDirectory(directory);

        try {
            PDDocument pdDocument = PDDocument.load(inputStream);
            PDDocumentCatalog pdDocumentCatalog = pdDocument.getDocumentCatalog();
            PDMetadata pdMetadata = pdDocumentCatalog.getMetadata();

            PDDocumentInformation pdDocumentInformation = pdDocument.getDocumentInformation();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

            directory.setString(PdfDirectory.TAG_AUTHOR, pdDocumentInformation.getAuthor() == null ? "" : pdDocumentInformation.getAuthor());
            directory.setString(PdfDirectory.TAG_CREATION_DATE, pdDocumentInformation.getCreationDate() == null ? "" : format.format(pdDocumentInformation.getCreationDate().getTime()));
            directory.setString(PdfDirectory.TAG_CREATOR, pdDocumentInformation.getCreator() == null ? "" : pdDocumentInformation.getCreator());
            directory.setString(PdfDirectory.TAG_KEYWORDS, pdDocumentInformation.getCreator() == null ? "" : pdDocumentInformation.getCreator());
            directory.setString(PdfDirectory.TAG_MOD_DATE, pdDocumentInformation.getModificationDate() == null ? "" : format.format(pdDocumentInformation.getModificationDate().getTime()));
            directory.setString(PdfDirectory.TAG_PRODUCER, pdDocumentInformation.getProducer() == null ? "" : pdDocumentInformation.getProducer());
            directory.setString(PdfDirectory.TAG_SUBJECT, pdDocumentInformation.getSubject() == null ? "" : pdDocumentInformation.getSubject());
            directory.setString(PdfDirectory.TAG_TITLE, pdDocumentInformation.getTitle() == null ? "" : pdDocumentInformation.getTitle());
            directory.setString(PdfDirectory.TAG_TRAPPED, pdDocumentInformation.getTrapped() == null ? "" : pdDocumentInformation.getTrapped());

            XmpReader xmpReader = new XmpReader();
            xmpReader.extract(pdMetadata.getByteArray(), metadata);

            pdDocument.close();
        } catch (IOException e) {
            directory.addError("ERROR: IOException thrown during Pdf extraction.");
        }
    }
}
