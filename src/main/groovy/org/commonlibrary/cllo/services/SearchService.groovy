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

import org.commonlibrary.cllo.model.LearningObject

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 11/6/13
 * Time: 9:45 AM
 * To change this template use File | Settings | File Templates.
 */

interface SearchService {

    List<LearningObject> search(String query, List<String> inclusions, List<String> exclusions, int searchFrom, int searchSize, Locale locale)

    List<String> suggestAltTerms(String query, Locale locale)

    List<LearningObject> searchMoreLikeThis(String id, int searchFrom, int searchSize, Locale locale)

}
