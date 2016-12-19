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
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

import javax.persistence.*
import javax.validation.constraints.NotNull

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 11/5/13
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = 'clc_contents')
@EqualsAndHashCode(callSuper = true)
@ToString(includeSuper = true, includeNames = true)
@Document(collection = 'clc_contents')
class Contents extends BaseEntity {

    protected Contents() {}

    //Contents File's mime type
    @NotNull
    @Column(name = 'mime_type')
    @NotBlank
    @Field('mime_type')
    private String mimeType

    //Contents File's md5 crypt
    @NotNull
    @Column(name = 'md5')
    @NotBlank
    @Field('md5')
    private String md5

    //Contents's File URL
    @NotNull
    @Column(name = 'url')
    @NotBlank
    @Field('url')
    private String url

    //Contents's List of resources(other contents)
    @Field('other_resources')
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> resourcesURL = []


    void CopyValues(Contents content) {

        this.setMimeType(content.getMimeType())
        this.setMd5(content.getMd5())
        this.setUrl(content.getUrl())
        this.setResourcesURL(content.getResourcesURL())

    }

    //Contents's File return el input
    public InputStream inputStreamFromURL() {

        URL tempURL = new URL(url)
        InputStream is = tempURL.openStream()
        return is

    }

    String getMimeType() {
        return mimeType
    }

    void setMimeType(String mimeType) {
        this.mimeType = mimeType
    }

    String getMd5() {
        return md5
    }

    void setMd5(String md5) {
        this.md5 = md5
    }

    String getUrl() {
        return url
    }

    void setUrl(String url) {
        this.url = url
    }


    List<String> getResourcesURL() {
        return resourcesURL
    }

    void setResourcesURL(List<String> resourcesURL) {
        this.resourcesURL = resourcesURL
    }


}

