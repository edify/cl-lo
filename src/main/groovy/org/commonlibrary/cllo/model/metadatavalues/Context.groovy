package org.commonlibrary.cllo.model.metadatavalues

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/4/13
 * Time: 9:32 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Context {

    ANY('clc.metadata.context.any'),
    PRIMARY_EDUCATION('clc.metadata.context.primaryEducation'),
    SECONDARY_EDUCATION('clc.metadata.context.secondaryEducation'),
    HIGHER_EDUCATION('clc.metadata.context.higherEducation'),
    UNIVERSITY_FIRST_CYCLE('clc.metadata.context.universityFirstCycle'),
    UNIVERSITY_SECOND_CYCLE('clc.metadata.context.UniversitySecondCycle'),
    UNIVERSITY_POSTGRADE('clc.metadata.context.universityPostgrade'),
    TECHNICAL_SCHOOL_FIRST_CYCLE('clc.metadata.context.technicalSchoolFirstCycle'),
    TECHNICAL_SCHOOL_SECOND_CYCLE('clc.metadata.context.technicalSchoolSecondCycle'),
    PROFESSIONAL_FORMATION('clc.metadata.context.professionalFormation'),
    CONTINUOUS_FORMATION('clc.metadata.context.continuousFormation'),
    VOCATIONAL_TRAINING('clc.metadata.context.vocationalTraining')

    String i18nCode

    private Context(String i18nCode) {
        this.i18nCode = i18nCode;
    }

}
