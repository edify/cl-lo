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

import org.commonlibrary.cllo.model.Contents
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.util.CoreException
import org.commonlibrary.cllo.util.FileResponse
import org.commonlibrary.cllo.util.VersionResponse
import org.springframework.data.domain.Page
import org.springframework.http.HttpEntity
import org.springframework.web.multipart.MultipartFile

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/6/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LearningObjectService {

    public  LearningObject findById(String id, Locale locale) throws CoreException

    public List<LearningObject> findByLearningObjective(String name, int from, int size, boolean all, Locale locale) throws CoreException

    public Page<LearningObject> findAll(int from, int size, boolean all, Locale locale) throws CoreException

    public LearningObject insert(String learningObjectJSON, Locale locale) throws CoreException

    public LearningObject update(String learningObjectJSON, String id, Locale locale) throws CoreException

    public LearningObject delete(String id, Locale locale) throws CoreException

    public LearningObject softDelete(String id, Locale locale) throws CoreException

    public Contents findContentsById(String idLO, String idContents, Locale locale) throws CoreException

    public Contents findAllContents(String idLO, Locale locale) throws CoreException

    public Contents updateContents(String contentsJSON, String idLO, String idContents, Locale locale) throws CoreException

    public Contents insertContents(String contentsJSON, String idLO, Locale locale) throws CoreException

    public Contents deleteContents(String idLO, String idContents, Locale locale) throws CoreException

    public HttpEntity<byte[]> findFileById(String idLO, String idContents, String fileId, String refPath, Locale locale) throws CoreException

    public String findInputStreamById(String idLO, String idContents, String fileId, String refPath, Locale locale) throws CoreException

    public List<VersionResponse> findVersions(String idLO, String idContents, String fileId, String refPath, Locale locale) throws CoreException

    public HttpEntity<byte[]> findVersionById(String idLO, String idContents,  String fileId, Long version, String refPath, Locale locale) throws CoreException

    public FileResponse rollBackToVersionById(String idLO, String idContents, Long version, String baseUrl, String refPath, Locale locale) throws CoreException

    public FileResponse insertFile(String idLO, String idContents, String filename, String primaryType, String secondaryType, MultipartFile multipartFile, String refPath, Locale locale) throws CoreException

    public FileResponse insertFile(String idLO, String idContents, String content, String filename, String primaryType, String secondaryType, String refPath, Locale locale) throws CoreException

    public FileResponse storeContent(String loId, String filename, String mimeType, String md5, InputStream content, String baseUrl, Locale locale)
}
