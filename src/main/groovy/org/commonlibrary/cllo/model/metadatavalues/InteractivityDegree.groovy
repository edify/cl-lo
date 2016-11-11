package org.commonlibrary.cllo.model.metadatavalues

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/4/13
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
enum InteractivityDegree {

    ANY('clc.metadata.interactivityType.any'),
    ACTIVE('clc.metadata.interactivityType.active'),
    MIXED('clc.metadata.interactivityType.mixed'),
    UNDEFINED('clc.metadata.interactivityType.undefined'),
    EXPOSITIVE('clc.metadata.interactivityType.expositive'),
    INTERACTIVE('clc.metadata.interactivityType.interactive')

    String i18nCode

    private InteractivityDegree(String i18nCode) {
        this.i18nCode = i18nCode
    }

}
