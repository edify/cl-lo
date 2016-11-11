package org.commonlibrary.cllo.services

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

    public  org.commonlibrary.cllo.model.LearningObject findById(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public List<org.commonlibrary.cllo.model.LearningObject> findByLearningObjective(String name, int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public Page<org.commonlibrary.cllo.model.LearningObject> findAll(int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObject insert(String learningObjectJSON, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObject update(String learningObjectJSON, String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObject delete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObject softDelete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Contents findContentsById(String idLO, String idContents, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Contents findAllContents(String idLO, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Contents updateContents(String contentsJSON, String idLO, String idContents, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Contents insertContents(String contentsJSON, String idLO, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Contents deleteContents(String idLO, String idContents, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public HttpEntity<byte[]> findFileById(String idLO, String idContents, String fileId, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public String findInputStreamById(String idLO, String idContents, String fileId, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public List<org.commonlibrary.cllo.util.VersionResponse> findVersions(String idLO, String idContents, String fileId, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public HttpEntity<byte[]> findVersionById(String idLO, String idContents,  String fileId, Long version, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.util.FileResponse rollBackToVersionById(String idLO, String idContents, Long version, String baseUrl, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.util.FileResponse insertFile(String idLO, String idContents, String filename, String primaryType, String secondaryType, MultipartFile multipartFile, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.util.FileResponse insertFile(String idLO, String idContents, String content, String filename, String primaryType, String secondaryType, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.util.FileResponse storeContent(String loId, String filename, String mimeType, String md5, InputStream content, String baseUrl, Locale locale)
}
