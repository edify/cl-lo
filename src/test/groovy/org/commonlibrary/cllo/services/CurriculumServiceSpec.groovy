package org.commonlibrary.cllo.services

import org.commonlibrary.cllo.model.Curriculum
import org.commonlibrary.cllo.model.Folder
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.model.LearningObjective
import org.commonlibrary.cllo.repositories.CurriculumRepository
import org.commonlibrary.cllo.repositories.FolderRepository
import org.commonlibrary.cllo.repositories.LearningObjectRepository
import org.commonlibrary.cllo.repositories.LearningObjectiveRepository
import org.commonlibrary.cllo.repositories.impl.mongo.CurriculumMongoRepository
import org.commonlibrary.cllo.repositories.impl.mongo.FolderMongoRepository
import org.commonlibrary.cllo.repositories.impl.mongo.LearningObjectMongoRepository
import org.commonlibrary.cllo.repositories.impl.mongo.LearningObjectiveMongoRepository
import org.commonlibrary.cllo.services.impl.CurriculumServiceImpl
import org.commonlibrary.cllo.util.CoreException
import org.springframework.context.MessageSource
import org.springframework.data.domain.PageRequest
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by diugalde on 04/08/16.
 */
class CurriculumServiceSpec extends Specification {
    @Shared
    CurriculumService curriculumService = new CurriculumServiceImpl()

    @Shared
    CurriculumRepository curriculumRepository

    @Shared
    LearningObjectRepository learningObjectRepository

    @Shared
    FolderRepository folderRepository

    @Shared
    LearningObjectiveRepository learningObjectiveRepository

    @Shared
    Locale locale = Locale.US

    def setup() {
        // Service autowired fields will be mocks. Mocks' default behavior is to return null.
        curriculumRepository = Mock(CurriculumMongoRepository)
        ReflectionTestUtils.setField(curriculumService, "curriculumRepository", curriculumRepository)

        learningObjectRepository = Mock(LearningObjectMongoRepository)
        ReflectionTestUtils.setField(curriculumService, "learningObjectRepository", learningObjectRepository)

        folderRepository = Mock(FolderMongoRepository)
        ReflectionTestUtils.setField(curriculumService, "folderRepository", folderRepository)

        learningObjectiveRepository = Mock(LearningObjectiveMongoRepository)
        ReflectionTestUtils.setField(curriculumService, "learningObjectiveRepository", learningObjectiveRepository)

        MessageSource ms = Mock()
        ReflectionTestUtils.setField(curriculumService, "messageSource", ms)
    }

    def "Finding existing curriculum by its id"() {
        setup:
        Curriculum curriculum = new Curriculum()

        when:
        Curriculum res = curriculumService.findById("testId", locale)

        then:
        1 * curriculumRepository.findById(_ as String) >> curriculum
        res == curriculum
    }

    def "Trying to find non existing curriculum"() {
        when:
        curriculumService.findById("unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding all curricula"() {
        given:
        int from = 0
        int size = 1
        boolean all = true

        when:
        curriculumService.findAll(from, size, all, locale)

        then:
        1 * curriculumRepository.count() >> 10
        1 * curriculumRepository.findAll(true, _ as PageRequest)
    }

    def "Finding curricula with parameter 'size' <= 0"() {
        given:
        int from = 0
        int zeroSize = 0
        int negativeSize = -1
        boolean all = false

        when:
        curriculumService.findAll(from, zeroSize, all, locale)
        curriculumService.findAll(from, zeroSize, !all, locale)

        then:
        thrown(CoreException)

        when:
        curriculumService.findAll(from, negativeSize, all, locale)
        curriculumService.findAll(from, negativeSize, !all, locale)

        then:
        thrown(CoreException)
    }

    def "Find arbitrary number of curricula with correct parameters"() {
        given:
        int from = 0
        int size = 15
        boolean all = false

        when:
        curriculumService.findAll(from, size, all, locale)

        then:
        1 * curriculumRepository.count() >> 100
        1 * curriculumRepository.findAll(true, _ as PageRequest)
    }

    //
    //

    /**
     * This test case will be ignored because it's failing due to changes made in the LO Service. This will be fixed in cl-curricula.
     * See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
     */
    @Ignore
    def "Finding all curricula by learning objective"() {
        setup:
        LearningObjective learningObjective = new LearningObjective()
        learningObjective.setId("sda&ahj23@")
        learningObjective.setName("Learning Objective name")
        List<LearningObject> learningObjectList = getTestLearningObjectList()
        List<Curriculum> curriculumList = getTestCurriculaList()

        and:
        String learningObjectiveName = "Learning Objective name"
        int from = 0
        int size = 10
        boolean all = true

        when:
        List<Curriculum> res = curriculumService.findByLearningObjective(learningObjectiveName, from, size, all, locale)

        then:
        1 * learningObjectiveRepository.findByName(_) >> learningObjective
        1 * learningObjectiveRepository.findById(_) >> learningObjective
        // Retrieve all learning objects. These learning objects will be filtered with the desired learning object.
        1 * learningObjectRepository.findAll() >> learningObjectList
        // Retrieve all curricula.
        1 * curriculumRepository.findAll() >> curriculumList
        // After the filter, the result should have just 1 curriculum.
        res.size() == 1
        res[0] == curriculumList[0]
    }


    /**
     * This test case will be ignored because it's failing due to changes made in the LO Service. This will be fixed in cl-curricula.
     * See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
     */
    @Ignore
    def "Finding all curricula by non existing learning objective"() {
        given:
        String learningObjectiveName = "Unknown Learning Objective name"
        int from = 0
        int size = 1
        boolean all = true

        when:
        curriculumService.findByLearningObjective(learningObjectiveName, from, size, all, locale)
        curriculumRepository.findByName(_) >> new Curriculum()
        curriculumService.findByLearningObjective(learningObjectiveName, from, size, all, locale)

        then:
        thrown(CoreException)
    }

    /**
     * This test case will be ignored because it's failing due to changes made in the LO Service. This will be fixed in cl-curricula.
     * See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
     */
    @Ignore
    def "Finding an arbitrary number of curricula by learning objective"() {
        setup:
        LearningObjective learningObjective = new LearningObjective()
        learningObjective.setId("sda&ahj23@")
        learningObjective.setName("Learning Objective name")
        List<LearningObject> learningObjectList = getTestLearningObjectList()
        List<Curriculum> curriculumList = getTestCurriculaList()

        and:
        String learningObjectiveName = "Learning Objective name"
        int from = 0
        int size = 10
        boolean all = false

        when:
        List<Curriculum> res = curriculumService.findByLearningObjective(learningObjectiveName, from, size, all, locale)

        then:
        1 * learningObjectiveRepository.findByName(_) >> learningObjective
        1 * learningObjectiveRepository.findById(_) >> learningObjective
        // Retrieve all learning objects. These learning objects will be filtered with the desired learning object.
        1 * learningObjectRepository.findAll() >> learningObjectList
        // Retrieve all curricula.
        1 * curriculumRepository.findAll() >> curriculumList
        // After the filter, the result should have just 1 curriculum.
        res.size() == 1
        res[0] == curriculumList[0]
    }

    def "Inserting new curriculum with a well formed JSON string"() {
        given: "A well formed JSON Curriculum"
        String curriculumName = "Curriculum Test Name"
        String curriculumTitle = "Learning Object Test Title"
        String curriculumDescription = "Learning Object Test Description"
        String curriculumJSON = """
        {
            "name": "${curriculumName}",
            "title": "${curriculumTitle}",
            "description": "${curriculumDescription}",
            "root" : {
                "id": "folderId",
                "folderList": [{"id": "folderId2"}, {"name": "folderName2"}],
                "learningObjectList": [{"id": "1", "name": "Learning Object Video", "enabled": true}]
            }
         }
        """

        when:
        Curriculum res = curriculumService.insert(curriculumJSON, locale)

        then:
        // The root folder and its subfolder list should be saved first.
        (1.._) * folderRepository.findById(_) >> new Folder()
        (1.._) * folderRepository.save(_)
        // The curriculum is saved in the repository.
        1 * curriculumRepository.save(_ as Curriculum)

        res.getName() == curriculumName
        res.getDescription() == curriculumDescription
        res.getTitle() == curriculumTitle
    }

    def "Inserting curriculum with wrong JSON"() {
        given:
        String emptyNameJSON = """{"name":,"description":"description", "title":"title", "root" : {"id": "folderId"}}"""

        when: "The name is empty"
        curriculumService.insert(emptyNameJSON, locale)

        then:
        thrown(CoreException)

        when: "The curriculum does not have a root folder"
        String nullRootJSON = """{"name":"name","description":"description", "title":"title"}"""
        curriculumService.insert(nullRootJSON, locale)

        then:
        thrown(CoreException)
    }

    def "Updating curriculum with a well formed JSON string"() {
        given: "A well formed JSON Curriculum"
        String curriculumName = "Curriculum Test Name"
        String curriculumTitle = "Learning Object Test Title"
        String curriculumDescription = "Learning Object Test Description"
        String curriculumJSON = """
        {
            "name": "${curriculumName}",
            "title": "${curriculumTitle}",
            "description": "${curriculumDescription}",
            "root" : {"id": "folderId"}
         }
        """
        Folder folder = new Folder()
        Curriculum curriculum = new Curriculum()
        curriculum.setName(curriculumName)
        curriculum.setDescription(curriculumDescription)
        curriculum.setTitle(curriculumTitle)

        when:
        Curriculum res = curriculumService.update(curriculumJSON, "curriculumId", locale)

        then:
        // The curriculum is retrieved.
        1 * curriculumRepository.findById(_) >> curriculum
        // Curriculum's root folder is retrieved.
        1 * folderRepository.findById(_) >> folder
        // The root folder should be saved first.
        1 * folderRepository.save(_)
        // The folder is retrieved and then the curriculum's root is set.
        1 * folderRepository.findById(_)
        // The curriculum is saved in the repository.
        1 * curriculumRepository.save(_ as Curriculum)

        res.getName() == curriculumName
        res.getDescription() == curriculumDescription
        res.getTitle() == curriculumTitle
    }

    def "Updating non existing curriculum"() {
        given:
        String curriculumJSON = """{"name":,"description":"description", "title":"title", "root" : {"id": "folderId"}}"""

        when:
        curriculumService.update(curriculumJSON, "unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Updating curriculum with non existing root folder"() {
        setup: "The root folder is not specified in the JSON"
        curriculumRepository.findById(_) >> new Curriculum()
        String curriculumJSON = """{"name":"name","description":"description", "title":"title"}"""

        when:
        curriculumService.update(curriculumJSON, "curriculumId", locale)

        then:
        thrown(CoreException)

        when: "The repository does not contain the folder"
        curriculumJSON = """{"name":"name","description":"description", "title":"title", "root": {"id":"folderId"}}"""
        curriculumService.update(curriculumJSON, "curriculumId", locale)

        then:
        thrown(CoreException)
    }

    def "Deleting curriculum"() {
        setup:
        Folder folderRoot = new Folder()
        folderRoot.setId("folderRootId")
        folderRoot.setFolderList([new Folder()])
        Curriculum curriculum = new Curriculum()
        curriculum.setRoot(folderRoot)

        when:
        curriculumService.delete("curriculumId", locale)

        then:
        // The curriculum is retrieved.
        1 * curriculumRepository.findById(_) >> curriculum
        // The root folder is retrieved.
        1 * folderRepository.findById(_) >> folderRoot
        // One or more folders linked with the curriculum will be deleted.
        (1.._) * folderRepository.findById(_) >> new Folder()
        (1.._) * folderRepository.delete(_)
        // The curriculum is deleted.
        1 * curriculumRepository.delete(_ as Curriculum)
    }

    def "Deleting non existing curriculum"() {
        when:
        curriculumService.delete("curriculumId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding existing folder by its id"() {
        setup:
        Curriculum curriculum = new Curriculum()
        Folder folder = new Folder()

        when:
        Folder res = curriculumService.findFolderById("curriculumId", "folderId", locale)

        then:
        // Retrieve curriculum.
        1 * curriculumRepository.findById(_ as String) >> curriculum
        // Retrieve folder.
        1 * folderRepository.findById(_ as String) >> folder

        res == folder
    }

    def "Trying to find folder with wrong ids"() {
        when: "The curriculum does not exist"
        curriculumService.findFolderById("unknownCurriculumId", "folderId", locale)

        then:
        thrown(CoreException)

        when: "The folder does not exist"
        curriculumRepository.findById(_) >> new Curriculum()
        curriculumService.findFolderById("curriculumId", "unknownFolderId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding subfolder list"() {
        setup:
        Curriculum curriculum = new Curriculum()
        Folder folder = new Folder()

        when:
        List<Folder> res = curriculumService.findFolders("curriculumId", "folderId", locale)

        then:
        // Retrieve curriculum.
        1 * curriculumRepository.findById(_ as String) >> curriculum
        // Retrieve folder.
        1 * folderRepository.findById(_ as String) >> folder

        res == folder.getFolderList()
    }

    def "Trying to find subfolder list with wrong ids"() {
        when: "The curriculum does not exist"
        curriculumService.findFolders("unknownCurriculumId", "folderId", locale)

        then:
        thrown(CoreException)

        when: "The folder does not exist"
        curriculumRepository.findById(_) >> new Curriculum()
        curriculumService.findFolders("curriculumId", "unknownFolderId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding folder learning objects"() {
        setup:
        Curriculum curriculum = new Curriculum()
        Folder folder = new Folder()

        when:
        List<LearningObject> res = curriculumService.findLOs("curriculumId", "folderId", locale)

        then:
        // Retrieve curriculum.
        1 * curriculumRepository.findById(_ as String) >> curriculum
        // Retrieve folder.
        1 * folderRepository.findById(_ as String) >> folder

        res == folder.getLearningObjectList()
    }

    def "Trying to find folder learning objectives with wrong ids"() {
        when: "The curriculum does not exist"
        curriculumService.findLOs("unknownCurriculumId", "folderId", locale)

        then:
        thrown(CoreException)

        when: "The folder does not exist"
        curriculumRepository.findById(_) >> new Curriculum()
        curriculumService.findLOs("curriculumId", "unknownFolderId", locale)

        then:
        thrown(CoreException)
    }

    def "Inserting new folder with a well formed JSON string"() {
        given: "A well formed JSON Folder"
        String folderName = "Curriculum Test Name"
        String folderTitle = "Learning Object Test Title"
        String folderDescription = "Learning Object Test Description"
        String folderJSON = """
        {
            "name": "folderName",
            "folderList": [],
            "learningObjectList": []
        }
        """

        when:
        Folder res = curriculumService.insertFolder(folderJSON, "curriculumId", "folderId", locale)

        then:
        1 * curriculumRepository.findById(_) >> new Curriculum()
        // The folder is saved.
        1 * folderRepository.save(_)
        // The folder's parent is retrieved.
        1 * folderRepository.findById(_) >> new Folder()
        // The parent is updated and saved.
        1 * folderRepository.save(_)

        res.getName() == "folderName"
    }

    def "Inserting folder with wrong JSON"() {
        given:
        String emptyNameJSON = """
        {
            "name": ,
            "folderList": [],
            "learningObjectList": []
        }
        """

        when: "The name is empty"
        curriculumService.insertFolder(emptyNameJSON, "curriculumId", "folderId", locale)

        then:
        thrown(CoreException)
    }

    def "Updating folder with a well formed JSON string"() {
        given: "A well formed JSON Folder"
        String folderJSON = """
        {
            "name": "updatedFolderName",
            "folderList": [],
            "learningObjectList": []
        }
        """

        when:
        Folder res = curriculumService.updateFolder(folderJSON, "curriculumId", "folderId", locale)

        then:
        // The curriculum is retrieved.
        1 * curriculumRepository.findById(_) >> new Curriculum()
        // Updated folder is retrieved.
        1 * folderRepository.findById(_) >> new Folder()
        // Updated folder is saved.
        1 * folderRepository.save(_)

        res.getName() == "updatedFolderName"
    }

    def "Updating folder with wrong ids"() {
        given:
        String folderJSON = """
        {
            "name": "updatedFolderName",
            "folderList": [],
            "learningObjectList": []
        }
        """

        when: "The curriculum does not exist"
        curriculumService.updateFolder(folderJSON, "unknownCurriculumId", "folderId", locale)

        then:
        thrown(CoreException)

        when: "The folder does not exist"
        curriculumRepository.findById(_) >> new Curriculum()
        curriculumService.updateFolder(folderJSON, "curriculumId", "unknownFolderId", locale)

        then:
        thrown(CoreException)
    }

    def "Deleting folder"() {
        setup:
        Folder folder = new Folder()
        folder.setId("folderRootId")
        Curriculum curriculum = new Curriculum()

        when:
        curriculumService.deleteFolder("curriculumId", "folderId", locale)

        then:
        1 * curriculumRepository.findById(_) >> curriculum
        1 * folderRepository.findById(_) >> folder
        1 * folderRepository.delete(_)
    }

    def "Deleting folder with wrong ids"() {
        when: "The curriculum does not exist"
        curriculumService.deleteFolder("unknownCurriculumId", "folderId", locale)

        then:
        thrown(CoreException)

        when: "The folder does not exist"
        curriculumRepository.findById(_) >> new Curriculum()
        curriculumService.deleteFolder( "curriculumId", "unknownFolderId", locale)

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
        learningObject1.setId("learningObject1Id")
        LearningObject  learningObject2 = new LearningObject()
        learningObject2.setLearningObjectiveList([learningObjective2])
        learningObject2.setId("learningObject2Id")
        LearningObject  learningObject3 = new LearningObject()
        learningObject3.setLearningObjectiveList([learningObjective1])
        learningObject3.setId("learningObject3Id")

        return [learningObject1, learningObject2, learningObject3]
    }

    def getTestCurriculaList() {
        LearningObject learningObject = new LearningObject()
        learningObject.setEnabled(true)
        learningObject.setId("learningObject1Id")

        Folder emptyFolder = new Folder()
        emptyFolder.setId("emptyFolderId")

        Folder f1 = new Folder()
        f1.setLearningObjectList([learningObject])
        f1.setFolderList([emptyFolder])
        f1.setId("folder1Id")

        Curriculum curr1 = new Curriculum()
        curr1.setRoot(f1)

        Curriculum curr2 = new Curriculum()
        curr2.setRoot(emptyFolder)

        return [curr1, curr2]
    }
}
