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


 package org.commonlibrary.cllo.model.metadatavalues

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/4/13
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Type {

    ANY('clc.metadata.LearningResourceType.any'),
    EXERCISE('clc.metadata.LearningResourceType.exercise'),
    SIMULATION('clc.metadata.LearningResourceType.simulation'),
    QUESTIONNAIRE('clc.metadata.LearningResourceType.questionnaire'),
    DIAGRAM('clc.metadata.LearningResourceType.diagram'),
    FIGURE('clc.metadata.LearningResourceType.figure'),
    GRAPH('clc.metadata.LearningResourceType.graph'),
    INDEX('clc.metadata.LearningResourceType.index'),
    SLIDE('clc.metadata.LearningResourceType.slide'),
    TABLE('clc.metadata.LearningResourceType.table'),
    NARRATIVE_TEXT('clc.metadata.LearningResourceType.narrativeText'),
    EXAM('clc.metadata.LearningResourceType.exam'),
    EXPERIMENT('clc.metadata.LearningResourceType.experiment'),
    PROBLEM_STATEMENT('clc.metadata.LearningResourceType.problemStatement'),
    SELF_ASSESSMENT('clc.metadata.LearningResourceType.selfAssessment');

    String i18nCode

    private Type(String i18nCode) {
        this.i18nCode = i18nCode
    }

}
