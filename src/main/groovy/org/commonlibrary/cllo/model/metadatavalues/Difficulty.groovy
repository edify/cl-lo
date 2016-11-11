package org.commonlibrary.cllo.model.metadatavalues

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/3/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Difficulty {

    ANY('clc.metadata.difficulty.any'),
    VERY_LOW('clc.metadata.difficulty.veryLow'),
    LOW('clc.metadata.difficulty.low'),
    MEDIUM('clc.metadata.difficulty.medium'),
    HIGH('clc.metadata.difficulty.high'),
    VERY_HIGH('clc.metadata.difficulty.veryHigh')

    String i18nCode

    private Difficulty(String i18nCode) {
        this.i18nCode = i18nCode
    }
}
