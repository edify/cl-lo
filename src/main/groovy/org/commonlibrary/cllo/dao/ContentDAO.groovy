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


 package org.commonlibrary.cllo.dao


/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 2/19/14
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */

public interface ContentDAO {

    Object findByReferenceId(String referenceId)

    Object findVersionByReferenceId(String referenceId, long versionNumber)

    long getNumberOfVersions(String referenceId)

    List<String> getAllVersions(String referenceId)

    String rollbackToVersion(String referenceId, long versionNumber)

    InputStream getInputStreamByContentReferenceId(String contentReferenceId)

    InputStream getInputStreamByContentReferenceId(String contentReferenceId, long versionNumber)

    String storeContent(InputStream is, String filename, String primaryType, String secondaryType)

    void deleteByReferenceId(String referenceId)

    String getMimeType(String referenceId)

    String getFileName(String referenceId)

    String getMD5(String referenceId)

    String getMimeType(String referenceId, long versionNumber)

    String getFileName(String referenceId, long versionNumber)

    String getMD5(String referenceId, long versionNumber)

    String getTargetUrl(String referenceId)

}
