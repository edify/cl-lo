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
import org.springframework.data.mongodb.core.mapping.Field

import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.FetchType

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 11/15/13
 * Time: 1:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Embeddable
@EqualsAndHashCode(excludes = 'status, language, endUser, difficulty, context')
@ToString(includeNames = true, excludes = 'status, language, endUser, difficulty, context')
class Metadata {

    protected Metadata() {}

    //KeyWords (Tags) describing the object
    @Field('keywords')
    @Column(name = 'keywords')
    private String keywords

    //Coverage of the object
    @Field('coverage')
    @Column(name = 'coverage')
    private String coverage

    //Context of the object

    @Field('context')
    @Column(name = 'context')
    private org.commonlibrary.cllo.model.metadatavalues.Context context


    @Field('difficulty')
    @Column(name = 'difficulty')
    private org.commonlibrary.cllo.model.metadatavalues.Difficulty difficulty


    @Field('end_user')
    @Column(name = 'end_user')
    private org.commonlibrary.cllo.model.metadatavalues.IntendedUser endUser


    @Field('interactivity_degree')
    @Column(name = 'interactivity_degree')
    private org.commonlibrary.cllo.model.metadatavalues.InteractivityDegree interactivityDegree


    @Field('language')
    @Column(name = 'language')
    private org.commonlibrary.cllo.model.metadatavalues.Language language


    @Field('status')
    @Column(name = 'status')
    private org.commonlibrary.cllo.model.metadatavalues.Status status

    @Field('author')
    @Column(name = 'author')
    private String author

    //TODO: se quita o se deja?
    @Field('topic')
    @Column(name = 'topic')
    private String topic

    @Field('isbn')
    @Column(name = 'isbn')
    private String isbn

    @Field('price')
    @Column(name = 'price')
    private double price

    //Extra metadata List
    @Field('extra_metadata')
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> extraMetadata = []


    String getKeywords() {
        return keywords
    }

    void setKeywords(String keywords) {
        this.keywords = keywords
    }

    String getCoverage() {
        return coverage
    }

    void setCoverage(String coverage) {
        this.coverage = coverage
    }

    org.commonlibrary.cllo.model.metadatavalues.Context getContext() {
        return context
    }

    void setContext(org.commonlibrary.cllo.model.metadatavalues.Context context) {
        this.context = context
    }

    org.commonlibrary.cllo.model.metadatavalues.Difficulty getDifficulty() {
        return difficulty
    }

    void setDifficulty(org.commonlibrary.cllo.model.metadatavalues.Difficulty difficulty) {
        this.difficulty = difficulty
    }

    org.commonlibrary.cllo.model.metadatavalues.IntendedUser getEndUser() {
        return endUser
    }

    void setEndUser(org.commonlibrary.cllo.model.metadatavalues.IntendedUser endUser) {
        this.endUser = endUser
    }

    org.commonlibrary.cllo.model.metadatavalues.InteractivityDegree getInteractivityDegree() {
        return interactivityDegree
    }

    void setInteractivityDegree(org.commonlibrary.cllo.model.metadatavalues.InteractivityDegree interactivityDegree) {
        this.interactivityDegree = interactivityDegree
    }

    org.commonlibrary.cllo.model.metadatavalues.Language getLanguage() {
        return language
    }

    void setLanguage(org.commonlibrary.cllo.model.metadatavalues.Language language) {
        this.language = language
    }

    org.commonlibrary.cllo.model.metadatavalues.Status getStatus() {
        return status
    }

    void setStatus(org.commonlibrary.cllo.model.metadatavalues.Status status) {
        this.status = status
    }

    String getAuthor() {
        return author
    }

    void setAuthor(String author) {
        this.author = author
    }

    String getTopic() {
        return topic
    }

    void setTopic(String topic) {
        this.topic = topic
    }

    String getIsbn() {
        return isbn
    }

    void setIsbn(String isbn) {
        this.isbn = isbn
    }

    double getPrice() {
        return price
    }

    void setPrice(double price) {
        this.price = price
    }

    List<String> getExtraMetadata() {
        return extraMetadata
    }

    void setExtraMetadata(List<String> extraMetadata) {
        this.extraMetadata = extraMetadata
    }
}
