package org.commonlibrary.cllo.model.metadatavalues

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/4/13
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Status {

    ANY('clc.metadata.status.any'),
    FINAL('clc.metadata.status.final'),
    REVISED('clc.metadata.status.revised'),
    UNAVAILABLE('clc.metadata.status.unavailable'),
    DRAFT('clc.metadata.status.draft'),
    PUBLISHED('clc.metadata.status.published')

    String i18nCode

    private Status(String i18nCode) {
        this.i18nCode = i18nCode
    }

}
