package org.commonlibrary.cllo.dao.impl

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository

/**
 * Created by diugalde on 16/08/16.
 */
@Repository
@Profile("FS_S3")
class S3ContentDAO implements org.commonlibrary.cllo.dao.ContentDAO {

    @Value('${s3Storage.general.bucket_name}')
    private String bucketName

    @Value('${s3Storage.file.url.expiration_hours}')
    private int expirationHours

    @Autowired
    private AmazonS3 s3Client

    @Override
    S3Object findByReferenceId(String referenceId) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, referenceId))
        return object
    }

    @Override
    S3Object findVersionByReferenceId(String referenceId, long versionNumber) {
        S3Object object = getVersionedObject(referenceId, versionNumber)
        return object
    }

    @Override
    long getNumberOfVersions(String referenceId) {
        VersionListing versionListing = s3Client.listVersions(bucketName, referenceId)
        List<S3VersionSummary> versionSummaries = versionListing.getVersionSummaries()
        return versionSummaries.size()
    }

    @Override
    List<String> getAllVersions(String referenceId) {
        List<String> versions = []
        VersionListing versionListing = s3Client.listVersions(bucketName, referenceId)
        List<S3VersionSummary> versionSummaries = versionListing.getVersionSummaries()
        int size = versionSummaries.size() - 1
        String v

        for(int i = size; i >= 0; i--){
            Date lastModified = versionSummaries.get(i).getLastModified()
            v = 'v' + (size - i + 1)
            v = v + ',' + lastModified
            versions.add(v)
        }
        return versions
    }

    @Override
    String rollbackToVersion(String referenceId, long versionNumber) {
        S3Object oldObject = getVersionedObject(referenceId, versionNumber)
        InputStream is = oldObject.getObjectContent()
        ObjectMetadata objectMetadata = oldObject.getObjectMetadata()
        s3Client.putObject(bucketName, referenceId, is, objectMetadata)
        oldObject.close()
        return referenceId
    }

    @Override
    InputStream getInputStreamByContentReferenceId(String referenceId) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, referenceId));
        InputStream is = object.getObjectContent()
        return is
    }

    @Override
    InputStream getInputStreamByContentReferenceId(String referenceId, long versionNumber) {
        S3Object object = getVersionedObject(referenceId, versionNumber)
        InputStream is = object.getObjectContent()
        return is
    }

    @Override
    String storeContent(InputStream is, String filename, String primaryType, String secondaryType) {
        String contentType = "${primaryType}/${secondaryType}"

        ObjectMetadata objectMetadata = new ObjectMetadata()
        objectMetadata.setContentLength(is.available())
        objectMetadata.setContentType(contentType)

        s3Client.putObject(bucketName, filename, is, objectMetadata)

        return filename
    }

    @Override
    void deleteByReferenceId(String referenceId) {
        VersionListing versionListing = s3Client.listVersions(bucketName, referenceId)
        String versionId
        for (S3VersionSummary objectSummary : versionListing.getVersionSummaries()) {
            versionId = objectSummary.getVersionId()
            s3Client.deleteVersion(bucketName, referenceId, versionId)
        }
    }

    @Override
    String getMimeType(String referenceId) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, referenceId))
        String mimeType = object.getObjectMetadata().getContentType()
        object.close()
        return mimeType
    }

    @Override
    String getFileName(String referenceId) {
        return referenceId
    }

    @Override
    String getMD5(String referenceId) {
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, referenceId))
        String md5 = object.getObjectMetadata().getContentMD5()
        object.close()
        return md5
    }

    @Override
    String getMimeType(String referenceId, long versionNumber) {
        S3Object object = getVersionedObject(referenceId, versionNumber)
        String mimeType = object.getObjectMetadata().getContentType()
        object.close()
        return mimeType
    }

    @Override
    String getFileName(String referenceId, long versionNumber) {
        return referenceId
    }

    @Override
    String getMD5(String referenceId, long versionNumber) {
        S3Object object = getVersionedObject(referenceId, versionNumber)
        String md5 = object.getObjectMetadata().getContentMD5()
        object.close()
        return md5
    }

    @Override
    String getTargetUrl(String referenceId) {

        def expirationDate = getExpirationDate()

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, referenceId)
        generatePresignedUrlRequest.setMethod(HttpMethod.GET)
        generatePresignedUrlRequest.setExpiration(expirationDate)

        URL presignedUrl = s3Client.generatePresignedUrl(generatePresignedUrlRequest)

        return presignedUrl.toString()
    }

    S3Object getVersionedObject(String referenceId, long versionNumber) {
        VersionListing versionListing = s3Client.listVersions(bucketName, referenceId);
        List<S3VersionSummary> versionSummaries = versionListing.getVersionSummaries()
        int position = (versionNumber as int)
        int realPosition = versionSummaries.size() - position
        String versionId = versionSummaries.get(realPosition).getVersionId()
        S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, referenceId, versionId))
        return object
    }

    private Date getExpirationDate() {
        def date = new Date();
        long ms = date.getTime();
        ms += 1000 * 60 * 60 * expirationHours;
        date.setTime(ms);
        return date
    }

}
