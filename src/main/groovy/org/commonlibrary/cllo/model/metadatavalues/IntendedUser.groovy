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
