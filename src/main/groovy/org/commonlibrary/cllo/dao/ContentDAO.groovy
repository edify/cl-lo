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
