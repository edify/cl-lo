/*
 * Copyright 2016 Edify Software Consulting.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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
