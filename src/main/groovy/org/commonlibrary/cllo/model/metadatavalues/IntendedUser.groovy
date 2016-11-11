package org.commonlibrary.cllo.model.metadatavalues

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/4/13
 * Time: 9:21 AM
 * To change this template use File | Settings | File Templates.
 */
enum IntendedUser {

    ANY('clc.endUser.any'),
    AUTHOR('clc.endUser.author'),
    TEACHERS('clc.endUser.teachers'),
    LEARNERS('clc.endUser.learners')

    String i18nCode

    private IntendedUser(String i18nCode) {
        this.i18nCode = i18nCode
    }

}
