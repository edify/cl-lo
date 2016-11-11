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
 * Time: 4:24 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity(name = 'clc_learning_objective')
@Document(collection = 'clc_learning_objective')
@EqualsAndHashCode(callSuper = true, excludes = 'learningObjectiveList')
@ToString(includeSuper = true, includeNames = true, excludes = 'learningObjectiveList')
public class LearningObjective extends BaseEntity {

    protected LearningObjective() {}

    //LearningObjective's name
    @NotNull
    @Field('name')
    @NotBlank
    @Column(name = 'name')
    private String name

    //LearningObjective's description
    @NotNull
    @Field('description')
    @NotBlank
    @Column(name = 'description')
    private String description

    //Learning Objective's List of relationships with others Learning Objectives
    @DBRef
    @Field('learningObjectiveList')
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = 'clc_l_objective_x_l_objective_list')
    private List<LearningObjective> learningObjectiveList = []


    void CopyValues(LearningObjective lo) {
        this.setName(lo.getName())
        this.setLearningObjectiveList(lo.getLearningObjectiveList())
        this.setDescription(lo.getDescription())
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    List<LearningObjective> getLearningObjectiveList() {
        return learningObjectiveList
    }

    void setLearningObjectiveList(List<LearningObjective> learningObjectiveList) {
        this.learningObjectiveList = learningObjectiveList
    }

    String getDescription() {
        return description
    }

    void setDescription(String description) {
        this.description = description
    }
}

