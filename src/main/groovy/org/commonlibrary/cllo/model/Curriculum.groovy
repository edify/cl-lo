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
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
@Entity(name='clc_curriculum')
@Document(collection = 'clc_curriculum')
@EqualsAndHashCode(callSuper = true, excludes = 'root, metadata')
@ToString(includeSuper = true, includeNames = true, excludes = 'root, metadata')
class Curriculum extends BaseEntity {

    protected Curriculum() {}

    //Curriculum's name
    @NotNull
    @Field('name')
    @NotBlank
    @Column(name = 'name')
    private String name

    @NotNull
    @Field('title')
    @NotBlank
    @Column(name = 'title')
    private String title

    @Field('discipline')
    @Column(name = 'discipline')
    private String discipline

    //Curriculum's description
    @NotNull
    @Field('description')
    @NotBlank
    @Column(name = 'description')
    private String description

    //Curriculum's root folder | Each Curriculum should have a root folder
    @DBRef(lazy = false)
    @Field('root')
    @OneToOne(cascade = CascadeType.REMOVE,fetch = FetchType.EAGER)
    @JoinColumn(name = 'root')
    private org.commonlibrary.cllo.model.Folder root

    //Curriculum's metadata
    @Field('metadata')
    @Embedded
    private org.commonlibrary.cllo.model.Metadata metadata

    @Field('enabled')
    @Column(name = 'enabled')
    private boolean enabled

    @DBRef(lazy = false)
    @Field('learningObjectiveList')
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = 'clc_curriculum_x_l_objective_list')
    List<org.commonlibrary.cllo.model.LearningObjective> learningObjectiveList = []

    void CopyValues(Curriculum curriculum) {

        this.setName(curriculum.getName())
        this.setTitle(curriculum.getTitle())
        this.setDiscipline(curriculum.getDiscipline())
        this.setDescription(curriculum.getDescription())
        this.setRoot(curriculum.getRoot())
        this.setMetadata(curriculum.getMetadata())
        this.setEnabled(curriculum.getEnabled())
        this.learningObjectiveList = curriculum.learningObjectiveList.clone() as List

    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getTitle() {
        return title
    }

    void setTitle(String title) {
        this.title = title
    }

    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }

    String getDiscipline() {
        return discipline
    }

    void setDiscipline(String discipline) {
        this.discipline = discipline
    }

    org.commonlibrary.cllo.model.Folder getRoot() {
        return root
    }

    void setRoot(org.commonlibrary.cllo.model.Folder root) {
        this.root = root
    }

    org.commonlibrary.cllo.model.Metadata getMetadata() {
        return metadata
    }

    void setMetadata(org.commonlibrary.cllo.model.Metadata metadata) {
        this.metadata = metadata
    }

    boolean getEnabled() {
        return enabled
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled
    }
}
