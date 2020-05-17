package uk.gov.hmcts.reform.em.npa.service.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.hmcts.reform.em.npa.service.AnnotationSetDTOToPDAnnotationMapper;
import uk.gov.hmcts.reform.em.npa.service.PdfAnnotator;
import uk.gov.hmcts.reform.em.npa.service.dto.external.annotation.AnnotationSetDTO;
import uk.gov.hmcts.reform.em.npa.service.exception.DocumentTaskProcessingException;

import java.io.File;
import java.io.IOException;

@Service
@Transactional
public class PdfAnnotatorImpl implements PdfAnnotator {

    private final AnnotationSetDTOToPDAnnotationMapper annotationSetDTOToPDAnnotationMapper;

    public PdfAnnotatorImpl(AnnotationSetDTOToPDAnnotationMapper annotationSetDTOToPDAnnotationMapper) {
        this.annotationSetDTOToPDAnnotationMapper = annotationSetDTOToPDAnnotationMapper;
    }

    @Override
    public File annotatePdf(File file, AnnotationSetDTO annotationSetDTO) throws DocumentTaskProcessingException {

        try (PDDocument document = PDDocument.load(file)) {

            annotationSetDTOToPDAnnotationMapper.toNativeAnnotationsPerPage(document, annotationSetDTO.getAnnotations());

            final String annotatedPathFile = file.getAbsolutePath().replaceFirst("\\.pdf", "-annotated.pdf");

            document.save(annotatedPathFile);

            return new File(annotatedPathFile);

        } catch (IOException e) {
            throw new DocumentTaskProcessingException("Could not load the file " + file.getName() + ". Error: " + e.getMessage(), e);
        }


    }
}
