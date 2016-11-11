package org.commonlibrary.cllo.config

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.iterable.S3Versions
import com.amazonaws.services.s3.model.BucketVersioningConfiguration
import com.amazonaws.services.s3.model.S3VersionSummary
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Created by diugalde on 16/08/16.
 */
@Configuration
@Profile('FS_S3')
class S3Config {

    @Value('${s3Storage.general.aws_access_key}')
    private String awsAccessKey

    @Value('${s3Storage.general.aws_secret_key}')
    private String awsSecretKey

    @Value('${s3Storage.general.bucket_name}')
    private String bucketName

    @Value('${s3Storage.general.empty_bucket}')
    private Boolean emptyBucket

    private AmazonS3 s3Client

    @Bean
    AmazonS3 s3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey)
        s3Client = new AmazonS3Client(credentials)
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        initBucket()

        return s3Client
    }


    private void initBucket() {

        if(!s3Client.doesBucketExist(bucketName)) {
            s3Client.createBucket(bucketName);
        }

        // Enable bucket versioning
        BucketVersioningConfiguration configuration = new BucketVersioningConfiguration().withStatus("Enabled");
        s3Client.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(bucketName, configuration));

        // If this property is set to true, the bucket will be cleaned (all object versions).
        if(emptyBucket) {
            clearBucket()
        }

    }

    private void clearBucket() {
        for (S3VersionSummary version : S3Versions.inBucket(s3Client, bucketName)) {
            String key = version.getKey();
            String versionId = version.getVersionId();
            s3Client.deleteVersion(bucketName, key, versionId);
        }
    }



}
