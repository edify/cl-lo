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
