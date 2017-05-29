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

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.commonlibrary.cllo.model.metadatavalues.Format
import org.commonlibrary.cllo.model.metadatavalues.Type
import org.hibernate.validator.constraints.NotBlank
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 11/4/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name = 'clc_learning_object')
@Document(collection = 'clc_learning_object')
@EqualsAndHashCode(callSuper = true, excludes = 'contents, metadata, type')
@ToString(includeSuper = true, includeNames = true, excludes = 'contents, metadata, type')
class LearningObject extends BaseEntity {

    public LearningObject() {}

    //the name of the Learning Object
    @NotNull
    @Field('name')
    @NotBlank
    @Column(name = 'name')
    private String name

    //If this property is true, the learning object's content can be accessed without authentication.
    @NotNull
    @Field('is_public')
    @Column(name = 'is_public')
    private boolean isPublic = true

    //indicates if the contents of the Learning Object is compounded of multiple resources
    @Field('compound_content')
    @Column(name = 'compound_content')
    private boolean compoundContent

    //the subject of the Learning Object
    @Field('subject')
    @Column(name = 'subject')
    private String subject

    //the description of the Learning Object
    @Field('description')
    @Column(name = 'description')
    private String description

    //the title of the Learning Object
    @Field('title')
    @Column(name = 'title')
    private String title

    //the type of the Learning Object specified in a enumeration
    @Field('type')
    @Column(name = 'type')
    private Type type

    //the format of the Learning Object specified in a enumeration
    @Field('format')
    @Column(name = 'format')
    private Format format

    //the metadata of the Learning Object
    @Field('metadata')
    @Embedded
    private Metadata metadata

    //contents of the Learning Object
    @DBRef
    @Field('contents')
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = 'contents')
    private Contents contents

    @Field('enabled')
    @Column(name = 'enabled')
    private boolean enabled

    @Field('externalUrl')
    @Column(name = 'externalUrl')
    private String externalUrl

    //Learning Object's List of relationships with others Learning Objectives
    @DBRef
    @Field('learningObjectiveList')
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = 'clc_l_object_x_l_objective_list')
    private List<LearningObjective> learningObjectiveList = []

    void CopyValues(LearningObject lo) {
        this.setName(lo.getName())
        this.setIsPublic(lo.getIsPublic())
        this.setCompoundContent(lo.getCompoundContent())
        this.setSubject(lo.getSubject())
        this.setDescription(lo.getDescription())
        this.setTitle(lo.getTitle())
        this.setType(lo.getType())
        this.setFormat(lo.getFormat())
        this.setMetadata(lo.getMetadata())
        this.setContents(lo.getContents())
        this.setEnabled(lo.getEnabled())
        this.setLearningObjectiveList(lo.getLearningObjectiveList())
        this.setExternalUrl(lo.getExternalUrl())
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    boolean getIsPublic() {
        return isPublic
    }

    void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic
    }

    boolean getCompoundContent() {
        return compoundContent
    }

    void setCompoundContent(boolean compoundContent) {
        this.compoundContent = compoundContent
    }

    String getSubject() {
        return subject
    }

    void setSubject(String subject) {
        this.subject = subject
    }

    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    String getTitle() {
        return title
    }

    void setTitle(String title) {
        this.title = title
    }

    Type getType() {
        return type
    }

    void setType(Type type) {
        this.type = type
    }

    Format getFormat() {
        return format
    }

    void setFormat(Format format) {
        this.format = format
    }

    Metadata getMetadata() {
        return metadata
    }

    void setMetadata(Metadata metadata) {
        this.metadata = metadata
    }

    Contents getContents() {
        return contents
    }

    void setContents(Contents contents) {
        this.contents = contents
    }

    boolean getEnabled() {
        return enabled
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled
    }

    List<LearningObjective> getLearningObjectiveList() {
        return learningObjectiveList
    }

    void setLearningObjectiveList(List<LearningObjective> learningObjectiveList) {
        this.learningObjectiveList = learningObjectiveList
    }

    String getExternalUrl() {
        return externalUrl
    }

    void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl
    }
}
