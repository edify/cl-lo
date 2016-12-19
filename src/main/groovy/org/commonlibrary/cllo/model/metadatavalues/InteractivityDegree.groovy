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
