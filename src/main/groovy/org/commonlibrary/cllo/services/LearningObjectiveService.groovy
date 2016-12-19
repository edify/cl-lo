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


 package org.commonlibrary.cllo.services

import org.springframework.data.domain.Page

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/15/14
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
interface LearningObjectiveService {

    public  org.commonlibrary.cllo.model.LearningObjective findById(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public Page<org.commonlibrary.cllo.model.LearningObjective> findAll(int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObjective insert(String learningObjectJSON, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObjective update(String learningObjectJSON, String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObjective delete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

}
