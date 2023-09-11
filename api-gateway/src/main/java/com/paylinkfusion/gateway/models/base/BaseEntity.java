package com.paylinkfusion.gateway.models.base;


import static com.paylinkfusion.gateway.helpers.ServerUtil.LOCAL_ADDRESS;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "created_ip")
    private String createdIp;

    @Transient
    private boolean newRecord = false;

    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @Column(name = "last_modified_ip")
    private String lastModifiedIp;

    @PreUpdate
    public void preUpdate() {
        this.setLastModifiedIp(LOCAL_ADDRESS);
    }

    @PrePersist
    public void prePersist() {
        this.setCreatedIp(LOCAL_ADDRESS);
    }

}