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

import org.commonlibrary.cllo.dao.ContentDAO
import org.commonlibrary.cllo.dao.impl.S3ContentDAO
import org.commonlibrary.cllo.model.Contents
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.model.LearningObjective
import org.commonlibrary.cllo.model.metadatavalues.Format
import org.commonlibrary.cllo.repositories.ContentRepository
import org.commonlibrary.cllo.repositories.LearningObjectRepository
import org.commonlibrary.cllo.repositories.LearningObjectiveRepository
import org.commonlibrary.cllo.repositories.impl.mongo.ContentMongoRepository
import org.commonlibrary.cllo.repositories.impl.mongo.LearningObjectMongoRepository
import org.commonlibrary.cllo.repositories.impl.mongo.LearningObjectiveMongoRepository
import org.commonlibrary.cllo.services.impl.LearningObjectServiceImpl
import org.commonlibrary.cllo.util.CoreException
import org.commonlibrary.cllo.util.VersionResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpEntity
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by diugalde on 04/08/16.
 */
class LearningObjectServiceSpec extends Specification {

    @Shared
    LearningObjectService learningObjectService = new LearningObjectServiceImpl()

    @Shared
    Locale locale = Locale.US

    @Shared
    LearningObjectiveRepository learningObjectiveRepository

    @Shared
    LearningObjectRepository learningObjectRepository

    @Shared
    ContentRepository contentRepository

    @Shared
    ContentDAO contentDAO

    @Shared
    QueueIndexService queueIndexService

    def setup() {
        // Service autowired fields will be mocks. Mocks' default behavior is to return null.
        MessageSource ms = Mock()
        ReflectionTestUtils.setField(learningObjectService, "messageSource", ms)

        learningObjectiveRepository = Mock(LearningObjectiveMongoRepository)
        ReflectionTestUtils.setField(learningObjectService, "learningObjectiveRepository", learningObjectiveRepository)

        learningObjectRepository = Mock(LearningObjectMongoRepository)
        ReflectionTestUtils.setField(learningObjectService, "learningObjectRepository", learningObjectRepository)

        contentRepository = Mock(ContentMongoRepository)
        ReflectionTestUtils.setField(learningObjectService, "contentRepository", contentRepository)

        contentDAO = Mock(S3ContentDAO)
        ReflectionTestUtils.setField(learningObjectService, "contentDAO", contentDAO)

        queueIndexService = Mock(org.commonlibrary.cllo.services.QueueIndexService)
        ReflectionTestUtils.setField(learningObjectService, "queueIndexService", queueIndexService)
    }

    def "Finding existing learning object by its id"() {
        setup:
        LearningObject learningObject = new LearningObject()

        when:
        LearningObject res = learningObjectService.findById("testId", locale)

        then:
        1 * learningObjectRepository.findById(_ as String) >> learningObject
        res == learningObject
    }

    def "Trying to find non existing learning object"() {
        when:
        learningObjectService.findById("unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding all learning objects by learning objective"() {
        setup:
        List<LearningObjective> learningObjectives = [new LearningObjective()]
        learningObjectives[0].setId("sda&ahj23@")
        learningObjectives[0].setName("Learning Objective name")
        List<LearningObject> learningObjectList = getTestLearningObjectList()

        and:
        String learningObjectiveName = "Learning Objective name"
        int from = 0
        int size = 10
        boolean all = true

        when:
        List<LearningObject> res = learningObjectService.findByLearningObjective(learningObjectiveName, from, size, all, locale)

        then:
        1 * learningObjectiveRepository.findByName(_) >> learningObjectives
        1 * learningObjectRepository.findAll() >> learningObjectList
        // After the filter, the result should have just 2 learningObjects.
        res.size() == 2
        res[0] == learningObjectList[0]
        res[1] == learningObjectList[2]
    }

    def "Finding all learning objects by non existing learning objective"() {
        given:
        String learningObjectiveName = "Unknown Learning Objective name"
        int from = 0
        int size = 1
        boolean all = true

        when:
        learningObjectService.findByLearningObjective(learningObjectiveName, from, size, all, locale)

        then:
        thrown(CoreException)
    }

    def "Finding an arbitrary number of learning objects by learning objective"() {
        setup:
        List<LearningObjective> learningObjectives = [new LearningObjective()]
        learningObjectives[0].setId("sda&ahj23@")
        learningObjectives[0].setName("Learning Objective name")
        List<LearningObject> learningObjectList = getTestLearningObjectList()

        and:
        String learningObjectiveName = "Learning Objective name"
        int from = 0
        int size = 1
        boolean all = false

        when:
        List<LearningObject> res = learningObjectService.findByLearningObjective(learningObjectiveName, from, size, all, locale)

        then:
        1 * learningObjectiveRepository.findByName(_) >> learningObjectives
        1 * learningObjectRepository.findAll() >> learningObjectList
        // The size parameter is set to 1, there are 2 learningObjects that match, but just one is returned.
        res.size() == 1
        res[0] == learningObjectList[0]
    }

    def "Finding all learning objects"() {
        given:
        int from = 0
        int size = 1
        boolean all = true

        when:
        learningObjectService.findAll(from, size, all, locale)

        then:
        1 * learningObjectRepository.count() >> 10
        1 * learningObjectRepository.findAll(_, _)
    }

    def "Finding learning objects with parameter 'size' <= 0"() {
        given:
        int from = 0
        int zeroSize = 0
        int negativeSize = -1
        boolean all = false

        when:
        learningObjectService.findAll(from, zeroSize, all, locale)
        learningObjectService.findAll(from, zeroSize, !all, locale)

        then:
        thrown(CoreException)

        when:
        learningObjectService.findAll(from, negativeSize, all, locale)
        learningObjectService.findAll(from, negativeSize, !all, locale)

        then:
        thrown(CoreException)
    }

    def "Find arbitrary number of learning objects with correct parameters"() {
        given:
        int from = 0
        int size = 15
        boolean all = false

        when:
        learningObjectService.findAll(from, size, all, locale)

        then:
        1 * learningObjectRepository.count() >> 100
        1 * learningObjectRepository.findAll(_, _)
    }

    def "Inserting new learning object with a well formed JSON string"() {
        given: "A well formed JSON Learning Object"
        String loName = "Learning Object Test Name"
        String loTitle = "Learning Object Test Title"
        String loDescription = "Learning Object Test Description"
        String loJSON = """
        {
            "name": "${loName}",
            "title": "${loTitle}",
            "description": "${loDescription}"
         }
        """

        when:
        LearningObject res = learningObjectService.insert(loJSON, locale)

        then:
        // The learning object should be saved and indexed.
        1 * learningObjectRepository.save(_ as LearningObject)
        1 * queueIndexService.addLearningObject(_ as LearningObject, _ as Locale)

        res.getName() == loName
        res.getDescription() == loDescription
        res.getTitle() == loTitle
    }

    def "Inserting new learning object with empty name"() {
        given:
        String emptyNameJSON = """{"name":,"description":"description", "title":"title"}"""

        when:
        learningObjectService.insert(emptyNameJSON, locale)

        then:
        thrown(CoreException)
    }

    def "Inserting new learning object with invalid name"() {
        given:
        String loJSON = """{"name":"LearningObject/name","description":"description", "title":"title"}"""

        when:
        learningObjectService.insert(loJSON, locale)

        then:
        thrown(CoreException)
    }

    def "Updating an existing learning object with a well formed JSON string"() {
        given: "A well formed JSON Learning Object is created"
        def oldLearningObject = new LearningObject()
        oldLearningObject.name = "Learning Object Test Name"
        String loName = "Learning Object Test Name"
        String loTitle = "Learning Object Test Title"
        String loDescription = "Learning Object Test Description"
        String loJSON = """
        {
            "name": "${loName}",
            "title": "${loTitle}",
            "description": "${loDescription}",
            "contents": {}
         }
        """

        when:
        LearningObject res = learningObjectService.update(loJSON, "testId", locale)

        then:
        2 * learningObjectRepository.findById(_ as String) >> oldLearningObject
        // The old content must be kept.
        1 * contentRepository.findById(_)
        // The learning object should be saved and indexed.
        1 * learningObjectRepository.save(_ as LearningObject)
        1 * queueIndexService.updateLearningObject(_ as LearningObject, false, _ as Locale)

        res.getName() == loName
        res.getDescription() == loDescription
        res.getTitle() == loTitle
    }

    def "Updating non existing learning object"() {
        given:
        String loJSON = """{"name":"updatedName", "description":"updatedDescription", "title":"updatedTitle"}"""

        when:
        learningObjectService.update(loJSON, "unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Updating learning object's name throws an exception"() {
        given: "A well formed JSON Learning Object is created"
        def oldLearningObject = new LearningObject()
        oldLearningObject.name = "Old name"
        String loName = "Learning Object Test Name"
        String loTitle = "Learning Object Test Title"
        String loDescription = "Learning Object Test Description"
        String loJSON = """
        {
            "name": "${loName}",
            "title": "${loTitle}",
            "description": "${loDescription}",
            "contents": {}
         }
        """

        when:
        learningObjectService.update(loJSON, "testId", locale)

        then:
        1 * learningObjectRepository.findById(_ as String) >> oldLearningObject
        thrown(CoreException)
    }

    def "Deleting existing learning object with no content"() {
        setup:
        LearningObject learningObject = new LearningObject()
        learningObject.setId("anyId")

        when:
        LearningObject res = learningObjectService.delete("anyId", locale)

        then:
        2 * learningObjectRepository.findById(_) >> learningObject
        // Learning object is deleted from repository and from index.
        1 * learningObjectRepository.delete(_ as LearningObject)
        1 * queueIndexService.removeLearningObject(_ as String, _ as Locale)
        res == learningObject
    }

    def "Deleting existing learning object with content"() {
        setup:
        LearningObject learningObject = new LearningObject()
        learningObject.setId("anyId")
        learningObject.setContents(Mock(Contents))
        learningObject.getContents().getUrl() >> "/sd#@sd3sa"

        when: "The learning object content is an url"
        learningObject.setFormat(Format.URL)
        LearningObject res1 = learningObjectService.delete("anyId", locale)

        then:
        // Retrieves learningObject and its content from repositories.
        2 * learningObjectRepository.findById(_) >> learningObject
        1 * contentRepository.findById(_) >> Mock(Contents)
        // Delete object's content and the object itself from repositories.
        1 * contentRepository.delete(_ as Contents)
        1 * learningObjectRepository.delete(_ as LearningObject)
        //Remove object from index.
        1 * queueIndexService.removeLearningObject(_ as String, _ as Locale)

        res1 == learningObject

        when: "The learning object content is not an url"
        learningObject.setFormat(Format.PDF)
        LearningObject res2 = learningObjectService.delete("anyId", locale)

        then:
        // Retrieves learningObject and its content from repositories.
        2 * learningObjectRepository.findById(_) >> learningObject
        1 * contentRepository.findById(_) >> Mock(Contents)
        // Its reference must be deleted too.
        1 * contentDAO.deleteByReferenceId(_ as String)
        // Delete object's content and the object itself from repositories.
        1 * contentRepository.delete(_ as Contents)
        1 * learningObjectRepository.delete(_ as LearningObject)
        //Remove object from index.
        1 * queueIndexService.removeLearningObject(_ as String, _ as Locale)

        res2 == learningObject
    }

    def "Deleting non existing learning object"() {
        when:
        learningObjectService.delete("unknownId", locale)

        then:
        thrown(CoreException)

        when:
        learningObjectService.softDelete("unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Soft deleting existing learning object"() {
        setup:
        LearningObject learningObject = new LearningObject()
        learningObject.setId("anyId")
        // This mock's method will return a Contents Mock.
        contentRepository.findById(_) >> Mock(Contents)

        when: "The learning object content is null"
        LearningObject res1 = learningObjectService.softDelete("anyId", locale)

        then:
        2 * learningObjectRepository.findById(_) >> learningObject
        // The learning object is removed from repository and index .
        1 * learningObjectRepository.delete(_ as LearningObject)
        1 * queueIndexService.removeLearningObject(_ as String, _ as Locale)

        res1 == learningObject

        when: "The learning object content is not null"
        learningObject.setContents(Mock(Contents))
        LearningObject res2 = learningObjectService.softDelete("anyId", locale)

        then:
        2 * learningObjectRepository.findById(_) >> learningObject
        // The content is removed from repository, by its reference is kept (main difference with regular delete).
        1 * contentRepository.delete(_ as Contents)
        // The learning object is removed from repository and index .
        1 * learningObjectRepository.delete(_ as LearningObject)
        1 * queueIndexService.removeLearningObject(_ as String, _ as Locale)

        res2 == learningObject
    }

    def "Finding contents by id"() {
        setup:
        Contents contents = new Contents()

        when:
        Contents res = learningObjectService.findContentsById("loTestId", "contentsId", locale)

        then:
        // Retrieves learning object.
        1 * learningObjectRepository.findById(_ as String) >> new LearningObject()
        // Retrieves learning object content from repository.
        1 * contentRepository.findById(_ as String) >> contents
        res == contents
    }

    def "Trying to find content with non existing ids"() {
        when: "The learning object does not exist"
        learningObjectService.findContentsById("loUnknownId", "contentsId", locale)

        then:
        thrown(CoreException)

        when: "The content does not exist"
        learningObjectRepository.findById(_ as String) >> new LearningObject()
        learningObjectService.findContentsById("loId", "unknownContentsId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding all learning object contents"() {
        setup:
        LearningObject learningObject = new LearningObject()
        Contents mockContents = Mock()
        learningObjectRepository.findById(_) >> learningObject

        when: "The learning object does not have contents"
        learningObjectService.findAllContents("loId", locale)

        then:
        thrown(CoreException)

        when: "The learning object has contents"
        learningObject.setContents(mockContents)
        Contents res = learningObjectService.findAllContents("loId", locale)

        then:
        res == mockContents
    }

    def "Trying to find non existing learning object contents"() {
        when: "The learning object does not exist"
        learningObjectService.findAllContents("unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Updating learning object contents"() {
        setup:
        LearningObject learningObject = new LearningObject()

        and:
        String contentsJSON = """
        {
            "mimeType": "",
            "md5": "",
            "url": "/sdsfsfs3",
            "resourcesURL": []
        }
        """

        when: "The contents of the current learning object are null"
        learningObjectService.updateContents(contentsJSON, "loId", "contentsId", locale)

        then:
        thrown(CoreException)

        when: "The contents of the current learning object are not null"
        learningObjectService.updateContents(contentsJSON, "loId", "contentsId", locale)

        then:
        // Learning object and its content is retrieved.
        2 * learningObjectRepository.findById(_) >> learningObject
        1 * contentRepository.findById(_) >> Mock(Contents)
        // The new content is saved in repositories and the updated learning object indexed.
        1 * contentRepository.save(_ as Contents)
        1 * learningObjectRepository.save(_ as LearningObject)
        1 * queueIndexService.updateLearningObject(_ as LearningObject, false, _ as Locale)
    }

    def "Trying to update contents of a non existing learning object"() {
        given:
        String contentsJSON = """
        {
            "mimeType": "",
            "md5": "",
            "url": "/sdsfsfs3",
            "resourcesURL": []
        }
        """

        when: "The learning object does not exist"
        learningObjectService.updateContents(contentsJSON, "unknownId", "contentsId", locale)

        then:
        thrown(CoreException)
    }

    def "Inserting learning object contents"() {
        setup:
        LearningObject learningObject = new LearningObject()

        and:
        String contentsJSON = """
        {
            "mimeType": "",
            "md5": "",
            "url": "/sdsfsfs3",
            "resourcesURL": []
        }
        """

        when:
        learningObjectService.insertContents(contentsJSON, "loId", locale)

        then:
        // Retrieves learning object.
        2 * learningObjectRepository.findById(_) >> learningObject
        // Save new content in repositories, and update index.
        1 * contentRepository.save(_ as Contents)
        1 * learningObjectRepository.save(_ as LearningObject)
        1 * queueIndexService.updateLearningObject(_ as LearningObject, false, _ as Locale)
    }

    def "Trying to insert contents for a non existing learning object"() {
        given:
        String contentsJSON = """
        {
            "mimeType": "",
            "md5": "",
            "url": "/sdsfsfs3",
            "resourcesURL": []
        }
        """

        when: "The learning object does not exist"
        learningObjectService.insertContents(contentsJSON, "unknownLOId", locale)

        then:
        thrown(CoreException)
    }

    def "Deleting learning object contents"() {
        setup:
        LearningObject learningObject = new LearningObject()

        and:
        String contentsJSON = """
        {
            "mimeType": "",
            "md5": "",
            "url": "/sdsfsfs3",
            "resourcesURL": []
        }
        """

        when: "The contents of the current learning object are null"
        learningObjectRepository.findById(_) >> learningObject
        learningObjectService.deleteContents("loId", "contentsId", locale)

        then:
        thrown(CoreException)

        when: "The contents of the current learning object are not null"
        contentRepository.findById(_) >> Mock(Contents)
        learningObjectService.deleteContents("loId", "contentsId", locale)

        then:
        // Retrieves learning object.
        2 * learningObjectRepository.findById(_) >> learningObject
        // Delete content from repository.
        1 * contentRepository.delete(_ as Contents)
        // Update learning object and index.
        1 * learningObjectRepository.save(_ as LearningObject)
        1 * queueIndexService.updateLearningObject(_ as LearningObject, false, _ as Locale)
    }

    def "Trying to delete non existing learning object"() {
        when:
        learningObjectService.deleteContents("unknownId", "contentsId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding file by id"() {
        setup:
        InputStream exampleIS = new ByteArrayInputStream("test".getBytes())

        when:
        learningObjectService.findFileById("loId", "contentsId", "fileId", "refPath", locale)

        then:
        // Retrieve learning object and its content.
        1 * learningObjectRepository.findById(_) >> new LearningObject()
        1 * contentRepository.findById(_ as String) >> new Contents()
        // Get inputStream from DAO and its mime info.
        1 * contentDAO.getMimeType(_ as String) >> "MimeType/MimeType"
        1 * contentDAO.getInputStreamByContentReferenceId(_ as String) >> exampleIS
    }

    def "Trying to find file for non existing learning object"() {
        when:
        learningObjectService.findFileById("unknownId", "contentsId", "fileId", "refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Finding input stream by id"() {
        setup:
        InputStream exampleIS = new ByteArrayInputStream("test".getBytes())

        when:
        String res = learningObjectService.findInputStreamById("loId", "contentsId", "fileId", "refPath", locale)

        then:
        // Retrieve learning object and its content.
        1 * learningObjectRepository.findById(_) >> new LearningObject()
        1 * contentRepository.findById(_ as String) >> new Contents()
        // Get inputStream from DAO.
        1 * contentDAO.getInputStreamByContentReferenceId(_ as String) >> exampleIS

        res == "test"

    }

    def "Trying to find input stream for non existing learning object"() {
        when:
        learningObjectService.findInputStreamById("unknownId", "contentsId", "fileId", "refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Finding file versions"() {
        when:
        List<VersionResponse> res = learningObjectService.findVersions("loId", "contentsId", "fileId", "refPath", locale)

        then:
        // Retrieve learning object and its content.
        1 * learningObjectRepository.findById(_) >> new LearningObject()
        1 * contentRepository.findById(_ as String) >> new Contents()
        // Retrieve inputStream and all versions from DAO.
        1 * contentDAO.getInputStreamByContentReferenceId(_ as String) >> new ByteArrayInputStream("test".getBytes())
        1 * contentDAO.getAllVersions(_ as String) >> ["1,23-04-16"]

        res.size() == 1
        VersionResponse vr1 = res[0]
        vr1.getDate() == "23-04-16"
        vr1.getVersion() == "1"
    }

    def "Trying to find all file versions for non existing learning object"() {
        when:
        learningObjectService.findVersions("unknownLoId", "contentsId", "fileId", "refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Finding version by id"() {
        when:
        HttpEntity<byte[]> res = learningObjectService.findVersionById("loId", "contentsId", "fileId", 1, "refPath", locale)

        then:
        // Retrieve learning object and its content.
        1 * learningObjectRepository.findById(_) >> new LearningObject()
        1 * contentRepository.findById(_ as String) >> new Contents()
        1 * contentDAO.getInputStreamByContentReferenceId(_, _) >> new ByteArrayInputStream("test".getBytes())
        1 * contentDAO.getMimeType(_ , _) >> "MimeType/MimeType"

        res != null
    }

    def "Trying to find version for non existing learning object"() {
        when:
        learningObjectService.findVersionById("unknownId", "contentsId", "fileId", 1, "refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Rolling back to specific version by id"() {
        setup:
        String url = "http://localhost:8080/api/v1/learningObjects/57a90c00345a289077824d02/contents/" +
                "57a90c00345a289077824d03/file/57a90c00345a289077824d02_file?refPath=57a90c00345a289077824d02/"
        learningObjectRepository.findById(_) >> new LearningObject()
        Contents contents = new Contents()
        contents.setUrl(url)
        contentRepository.findById(_ as String) >> contents
        contentDAO.getInputStreamByContentReferenceId(_ as String) >> new ByteArrayInputStream("test".getBytes())
        contentDAO.getNumberOfVersions(_) >> 2

        when: "The version argument is < 1"
        learningObjectService.rollBackToVersionById("loId", "contentsId", 0, "http://localhost:8080/api/v1", "refPath", locale)

        then:
        thrown(CoreException)

        when:
        learningObjectService.rollBackToVersionById("loId", "contentsId", 1, "http://localhost:8080/api/v1", "refPath", locale)

        then:
        // Retrieve learning object and its content.
        2 * learningObjectRepository.findById(_) >> new LearningObject()
        1 * contentRepository.findById(_ as String) >> contents
        1 * contentDAO.getInputStreamByContentReferenceId(_ as String) >> new ByteArrayInputStream("test".getBytes())
        // Rollback content to the desired version.
        1 * contentDAO.rollbackToVersion("57a90c00345a289077824d02/57a90c00345a289077824d02_file", 1) >> "57ad7f7d0bb8d690747246e4/57ad7f7d0bb8d690747246e4_file"
        // Get content information (mime and md5)
        1 * contentDAO.getMimeType(_)
        1 * contentDAO.getMD5(_)
        // Save the learning object with the rollbacked content.
        1 * contentRepository.save(_ as Contents)
        1 * learningObjectRepository.save(_ as LearningObject)
        // Index the updated learning object.
        1 * queueIndexService.updateLearningObject(_ as LearningObject, true, _ as Locale)
    }

    def "Trying to rollback non existing learning object"() {
        when:
        learningObjectService.rollBackToVersionById("loId", "contentsId", 1, "http://localhost:8080/api/v1", "refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Inserting empty multipart file"() {
        setup:
        MultipartFile mpf = Mock()
        mpf.isEmpty() >> true

        when:
        learningObjectService.insertFile("loId", "contentsId", "filename", "primaryType",
                                        "secondaryType", mpf, "refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Inserting file for non existing learning object"() {
        setup:
        MultipartFile mpf = Mock()
        mpf.isEmpty() >> true

        when: "Insert content (using multipart file) method is invoked"
        learningObjectService.insertFile("unknownId", "contentsId", "filename", "primaryType",
                                        "secondaryType", mpf, "refPath", locale)

        then:
        thrown(CoreException)

        when: "Insert content (as string) method is invoked"
        learningObjectService.insertFile("unknownId", "contentsId", "content", "filename", "primaryType",
                "secondaryType","refPath", locale)

        then:
        thrown(CoreException)
    }

    def "Inserting file using ref and multipart file"() {
        setup:
        MultipartFile mpf = Mock()
        mpf.isEmpty() >> false

        when:
        learningObjectService.insertFile("loId", "contentsId", "filename", "primaryType",
                "secondaryType", mpf, "refPath", locale)
        learningObjectService.insertFile("loId", "contentsId", "content", "filename", "primaryType",
                "secondaryType","refPath", locale)

        then:
        // Retrieve learning object and its content.
        4 * learningObjectRepository.findById(_) >> new LearningObject()
        2 * contentRepository.findById(_) >> new Contents()
        // Store the new content in the file system.
        2 * contentDAO.storeContent(_, _, _, _) >> "fileId"
        // Save content in repository.
        2 * contentRepository.save(_)
        // Update learning object and the index.
        2 * learningObjectRepository.save(_)
        2 * queueIndexService.updateLearningObject(_ as LearningObject, true, _ as Locale)
    }

    def "Storing learning object contents"() {
        when:
        learningObjectService.storeContent("loId", "filename", "mimeType", "md5", new ByteArrayInputStream("test".getBytes()),
                                            "http://localhost:8080/api/v1", locale)

        then:
        2 * learningObjectRepository.findById(_ as String) >> new LearningObject()
        1 * contentRepository.save(_ as Contents)
        1 * learningObjectRepository.save(_ as LearningObject)

        // Store the new content in the file system.
        1 * contentDAO.storeContent(_, _, _, _) >> "fileId"
        1 * contentDAO.getMD5(_ as String)
        // Save content in repository.
        1 * contentRepository.save(_ as Contents)
        // Update learning object and the index.
        1 * learningObjectRepository.save(_ as LearningObject)
        1 * queueIndexService.updateLearningObject(_ as LearningObject, true, _ as Locale)
    }

    def "Trying to store content for non existing learning object"() {
        when:
        learningObjectService.storeContent("unkonwnId", "filename", "mimeType", "md5", new ByteArrayInputStream("test".getBytes()),
                "http://localhost:8080/api/v1", locale)
        then:
        thrown(CoreException)
    }

    // Helper methods

    def getTestLearningObjectList() {
        LearningObjective learningObjective1 = new LearningObjective()
        learningObjective1.setId("sda&ahj23@")
        LearningObjective learningObjective2 = new LearningObjective()
        learningObjective2.setId("/fsshghg4#")

        LearningObject learningObject1 = new LearningObject()
        learningObject1.setLearningObjectiveList([learningObjective1, learningObjective2])
        LearningObject  learningObject2 = new LearningObject()
        learningObject2.setLearningObjectiveList([learningObjective2])
        LearningObject  learningObject3 = new LearningObject()
        learningObject3.setLearningObjectiveList([learningObjective1])

        return [learningObject1, learningObject2, learningObject3]
    }
}
