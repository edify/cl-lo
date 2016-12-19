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
import org.apache.commons.collections.ListUtils
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
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
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

//Folder Entity, represents a Folder that can contain a set of other folders and a set of learning objects
@Deprecated
@Entity(name='clc_folder')
@Document(collection = 'clc_folder')
@EqualsAndHashCode(callSuper = true, excludes = 'folderList, learningObjectLists')
@ToString(includeSuper = true, includeNames = true, excludes = 'folderList, learningObjectList')
class Folder extends BaseEntity {

    protected Folder() {}

    //Folder's name
    @NotNull
    @Field('name')
    @NotBlank
    @Column(name = 'name')
    private String name

    //Folder's List of directly contained  sub-folders
    @DBRef
    @Field('folderList')
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @JoinTable(name = 'clc_folder_x_folder_list')
    @Fetch(FetchMode.SELECT)
    private List<Folder> folderList = []

    //Folder's List of directly contained Learning Objects c
    @DBRef()
    @Field('learningObjectList')
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(name = 'clc_folder_x_l_object_list')
    private List<LearningObject> learningObjectList = []

    void CopyValues(Folder folder) {

        this.setName(folder.getName())
        this.setFolderList(folder.getFolderList())
        this.setLearningObjectList(folder.getLearningObjectList())

    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    List<Folder> getFolderList() {
        return folderList
    }

    void setFolderList(List<Folder> folderList) {
        this.folderList = folderList
    }

    List<LearningObject> getLearningObjectList() {

        List<LearningObject> enabledList = []
        for (lo in learningObjectList) {
            if (lo.getEnabled()) {
                enabledList.add(lo)
            }
        }
        return enabledList
    }

    void setLearningObjectList(List<LearningObject> learningObjectList) {
        List<LearningObject> disabledList = []
        for (lo in this.learningObjectList) {
            if (!lo.getEnabled()) {
                disabledList.add(lo)
            }
        }

        List<LearningObject> unionList = ListUtils.union(disabledList, learningObjectList)
        this.learningObjectList = unionList
    }
}
