package org.commonlibrary.cllo.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

import javax.persistence.*

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 11/11/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
class BaseEntity {

    @org.springframework.data.annotation.Id
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    String id

    //Base Entity's version | how many times the entity has been modified
    @org.springframework.data.annotation.Version
    @Version
    private Long version

    //Base Entity's creation date
    @CreatedDate
    Date creationDate

    //Base Entity's modification date |  last time it was modified
    @LastModifiedDate
    Date modificationDate

    @PrePersist
    void onCreate() {
        creationDate = creationDate ?: new Date()
    }

    @PreUpdate
    void onModify() {
        modificationDate = new Date()
    }

    @Transient
    boolean isNew() {
        return null == id;
    }
}
