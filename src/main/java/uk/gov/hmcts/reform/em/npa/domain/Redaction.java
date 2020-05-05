package uk.gov.hmcts.reform.em.npa.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * A Redaction.
 */
@Entity
@Table(name = "redaction")
@Getter
@Setter
public class Redaction implements Serializable {

    @Id
    @Column(name = "redaction_id")
    private UUID redactionId;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    private String createdBy;

    @Column(name = "document_id", nullable = false)
    private UUID documentId;

    @Column(name = "page_num", nullable = false)
    private Integer page;

    @OneToMany(mappedBy = "redaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rectangle> rectangles = new HashSet<>();

}