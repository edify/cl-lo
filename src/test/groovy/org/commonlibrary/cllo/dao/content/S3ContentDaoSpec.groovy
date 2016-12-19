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


 package org.commonlibrary.cllo.dao.content

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.*
import org.commonlibrary.cllo.dao.impl.S3ContentDAO
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Shared
import spock.lang.Specification

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by diugalde on 04/08/16.
 */
class S3ContentDaoSpec extends Specification {

    @Shared
    S3ContentDAO s3ContentDAO = new S3ContentDAO()

    @Shared
    AmazonS3 s3Client

    @Shared
    String bucketName

    def setup() {
        // DAO autowired fields will be mocks. Mocks' default behavior is to return null.
        s3Client = Mock()
        ReflectionTestUtils.setField(s3ContentDAO, "s3Client", s3Client)
        bucketName = "edify-cl-dev"
        ReflectionTestUtils.setField(s3ContentDAO, "bucketName", bucketName)
        def expirationHours = 2
        ReflectionTestUtils.setField(s3ContentDAO, "expirationHours", expirationHours)
    }

    def "Finding content by its reference id"() {
        given:
        String refId = "@sdfs#&s/2a"

        when:
        s3ContentDAO.findByReferenceId(refId)

        then:
        1 * s3Client.getObject(_ as GetObjectRequest)
    }

    def "Finding version by reference id"() {
        given:
        VersionListing versionListing = Mock()
        s3Client.listVersions(_ as String, _ as String)  >> versionListing
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary), Mock(S3VersionSummary)]
        String refId = "@sdfs#&s/2a"
        Long version = 1

        when:
        s3ContentDAO.findVersionByReferenceId(refId, version)

        then:
        s3ContentDAO.getVersionedObject(refId, version) >> Mock(S3Object)
    }

    def "Getting number of versions"() {
        given:
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary), Mock(S3VersionSummary)]
        String refId = "@sdfs#&s/2a"

        when:
        long res = s3ContentDAO.getNumberOfVersions(refId)

        then:

        1 * s3Client.listVersions(_ as String, _ as String) >> versionListing
        res == 2
    }


    def "Getting all versions"() {
        given:
        String refId = "@sdfs#&s/2a"
        DateFormat sdf = new SimpleDateFormat("dd-mm-yy");
        S3VersionSummary s3VersionSummary = Mock()
        s3VersionSummary.getLastModified() >> sdf.parse("26-12-16")
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [s3VersionSummary]

        when:
        List<String> res = s3ContentDAO.getAllVersions(refId)

        then:

        // Retrieve object summary versions.
        1 * s3Client.listVersions(_ as String, _ as String) >> versionListing
        String expectedVersion = "v1,${sdf.parse("26-12-16")}"
        
        res[0] == expectedVersion
    }

    def "Rolling back to some version"() {
        given:
        String refId = "@sdfs#&s/2a"
        Long version = 1
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary)]
        S3Object s3Object = Mock()
        s3Object.getObjectContent() >> Mock(S3ObjectInputStream)
        s3Object.getObjectMetadata() >> Mock(ObjectMetadata)

        when:
        String res = s3ContentDAO.rollbackToVersion(refId, version)

        then:
        // Retrieve desired object version.
        s3Client.listVersions(bucketName, refId) >> versionListing
        s3Client.getObject(_ as GetObjectRequest) >> s3Object
        // Store old version object.
        1 * s3Client.putObject(_ as String, _ as String, _ as InputStream, _ as ObjectMetadata)

        res == "@sdfs#&s/2a"
    }

    def "Getting input stream by content reference id"() {
        given:
        S3Object s3Object = Mock()
        String refId = "@sdfs#&s/2a"
        Long version = 1
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary)]
        s3Client.listVersions(bucketName, refId) >> versionListing

        when: "Passing version as parameter"
        s3ContentDAO.getInputStreamByContentReferenceId(refId, version)

        then:
        s3Client.getObject(_ as GetObjectRequest) >> s3Object
        1 * s3Object.getObjectContent()

        when:
        s3ContentDAO.getInputStreamByContentReferenceId(refId)

        then:
        1 * s3Client.getObject(_ as GetObjectRequest) >> s3Object
        1 * s3Object.getObjectContent()
    }

    def "Storing content"() {
        setup:
        S3Object s3Object = Mock()

        when:
        String res = s3ContentDAO.storeContent(Mock(InputStream), "filename", "primaryType", "secondaryType")

        then:
        // Save object.
        1 * s3Client.putObject(_ as String, _ as String, _ as InputStream, _ as ObjectMetadata)

        res == "filename"
    }

    def "Deleting by reference id"() {
        given:
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary)]
        String refId = "@sdfs#&s/2a"

        when:
        s3ContentDAO.deleteByReferenceId(refId)

        then:
        // Iterate through all object versions and delete them.
        s3Client.listVersions(bucketName, refId) >> versionListing
        (0.._) * s3Client.deleteVersion(bucketName, refId, _ as String)
    }

    def "Getting object mime type"() {
        given:
        S3Object s3Object = Mock()
        s3Object.getObjectMetadata() >> Mock(ObjectMetadata)
        String refId = "@sdfs#&s/2a"
        Long version = 1
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary)]
        s3Client.listVersions(bucketName, refId) >> versionListing

        when: "Passing version as parameter"
        s3ContentDAO.getMimeType(refId, version)

        then:
        s3Client.getObject(_ as GetObjectRequest) >> s3Object
        1 * s3Object.getObjectMetadata().getContentType()

        when:
        s3ContentDAO.getMimeType(refId)

        then:
        1 * s3Client.getObject(_ as GetObjectRequest) >> s3Object
        1 * s3Object.getObjectMetadata().getContentType()
    }

    def "Getting object md5"() {
        given:
        S3Object s3Object = Mock()
        s3Object.getObjectMetadata() >> Mock(ObjectMetadata)
        String refId = "@sdfs#&s/2a"
        Long version = 1
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary)]
        s3Client.listVersions(bucketName, refId) >> versionListing

        when: "Passing version as parameter"
        s3ContentDAO.getMD5(refId, version)

        then:
        s3Client.getObject(_ as GetObjectRequest) >> s3Object
        1 * s3Object.getObjectMetadata().getContentMD5()

        when:
        s3ContentDAO.getMD5(refId)

        then:
        1 * s3Client.getObject(_ as GetObjectRequest) >> s3Object
        1 * s3Object.getObjectMetadata().getContentMD5()
    }

    def "Getting object filename"() {
        given:
        S3Object s3Object = Mock()
        String refId = "@sdfs#&s/2a"
        Long version = 1

        when: "Passing version as parameter"
        String res1 = s3ContentDAO.getFileName(refId, version)

        then:
        res1 == "@sdfs#&s/2a"

        when:
        String res2 = s3ContentDAO.getFileName(refId)

        then:
        res2 == "@sdfs#&s/2a"
    }

    def "Getting s3 object using reference id and version"() {
        setup:
        S3Object s3Object = Mock()
        VersionListing versionListing = Mock()
        versionListing.getVersionSummaries() >> [Mock(S3VersionSummary)]

        and:
        String refId = "@sdfs#&s/2a"
        long version = 1

        when:
        S3Object res = s3ContentDAO.getVersionedObject(refId, version)

        then:
        // The object versions are retrieved.
        s3Client.listVersions(bucketName, refId) >> versionListing
        // Retrieve the objet itself.
        1 * s3Client.getObject(_ as GetObjectRequest) >> s3Object
        res == s3Object
    }

    def "Getting Target URL for s3 object"() {
        setup:
        String refId = "@sdfs#&s/2a"

        when:
        String url = s3ContentDAO.getTargetUrl(refId)

        then:
        // The generatePresignedUrl method is invoked.
        1 * s3Client.generatePresignedUrl(_ as GeneratePresignedUrlRequest) >> 'http://www.signedawsurl.com'.toURL()
        url == 'http://www.signedawsurl.com'
    }

}
