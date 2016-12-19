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
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public enum Format {

    IMAGE('clc.metadata.format.image'),
    HTML('clc.metadata.format.html'),
    XML('clc.metadata.format.xml'),
    VIDEO('clc.metadata.format.video'),
    AUDIO('clc.metadata.format.audio'),
    PLAIN_TEXT('clc.metadata.format.plain_text'),
    JSON('clc.metadata.format.json'),
    URL('clc.metadata.format.url'),
    MULTIMEDIA('clc.metadata.format.multimedia'),
    PDF('clc.metadata.format.pdf'),
    EXCEL('clc.metadata.format.excel'),
    POWER_POINT('clc.metadata.format.power_point'),
    WORD('clc.metadata.format.word'),
    ODS('clc.metadata.format.ods'),
    ODP('clc.metadata.format.odp'),
    ODT('clc.metadata.format.odt')

    String i18nCode

    private Format(String i18nCode) {
        this.i18nCode = i18nCode
    }

}
