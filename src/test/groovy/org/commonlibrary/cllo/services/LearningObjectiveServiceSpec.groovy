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

import org.commonlibrary.cllo.model.LearningObjective
import org.commonlibrary.cllo.repositories.LearningObjectiveRepository
import org.commonlibrary.cllo.repositories.impl.mongo.LearningObjectiveMongoRepository
import org.commonlibrary.cllo.services.impl.LearningObjectiveServiceImpl
import org.commonlibrary.cllo.util.CoreException
import org.springframework.context.MessageSource
import org.springframework.data.domain.PageRequest
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Shared
import spock.lang.Specification


/**
 * Created by diugalde on 04/08/16.
 */

class LearningObjectiveServiceSpec extends Specification {

    @Shared
    LearningObjectiveService learningObjectiveService = new LearningObjectiveServiceImpl()

    @Shared
    Locale locale = Locale.US

    @Shared
    LearningObjectiveRepository learningObjectiveRepository

    def setup() {
        // Service autowired fields will be mocks. Mocks' default behavior is to return null.
        MessageSource ms = Mock()
        ReflectionTestUtils.setField(learningObjectiveService, "messageSource", ms)
        learningObjectiveRepository = Mock(LearningObjectiveMongoRepository)
        ReflectionTestUtils.setField(learningObjectiveService, "learningObjectiveRepository", learningObjectiveRepository)
    }

    def "Inserting new learning objective with a well formed JSON string"() {
        given: "A well formed JSON Learning Objective"
        String loName = "Learning Objective Test Name"
        String loDescription = "Learning Objective Test Description"
        String loJSON = """
        {
            "name": "${loName}",
            "description": "${loDescription}"
        }
        """

        when:
        LearningObjective res = learningObjectiveService.insert(loJSON, locale)

        then: "The repository save method should be invoked exactly once."
            1 * learningObjectiveRepository.save(_ as LearningObjective)
            res.getName() == loName
            res.getDescription() == loDescription
    }


    def "Inserting new learning objective with empty name"() {
        given:
        String emptyNameJSON = """{"name":,"description":"description"}"""

        when:
        learningObjectiveService.insert(emptyNameJSON, locale)

        then:
        thrown(CoreException)
    }

    def "Inserting learning objective with cycles"() {
        given:
        String loJSON = """
        {
            "id": "ashf2394assq",
            "name": "loName1",
            "description": "loDescription1",
            "learningObjectiveList":[
                {
                    "id": "ashf2394bb67",
                    "name": "loName2",
                    "description": "loDescription2",
                    "learningObjectiveList":[
                        {
                           "id": "ashf2394assq",
                            "name": "loName1",
                            "description": "loDescription1",
                            "learningObjectiveList":[]
                        }
                    ]
                }
            ]
        }
        """

        when:
        learningObjectiveService.insert(loJSON, locale)

        then: "CoreException must be thrown because a cycle was found"
        thrown(CoreException)
    }

    def "Updating an existing learning objective with a well formed JSON string"() {
        given: "A well formed JSON Learning Objective is created"
        String loName = "Learning Objective Test UpdatedName"
        String loDescription = "Learning Objective Test UpdatedDescription"
        String loJSON = """
        {
            "name": "${loName}",
            "description": "${loDescription}"
        }
        """

        when:
        LearningObjective res = learningObjectiveService.update(loJSON, "testId", locale)

        then:
        1 * learningObjectiveRepository.findById(_ as String) >> new LearningObjective()
        1 * learningObjectiveRepository.save(_ as LearningObjective)
        res.getName() == loName
        res.getDescription() == loDescription
    }

    def "Updating non existing learning objective"() {
        given:
        String loJSON = """{"name":"updatedName", "description":"updatedDescription"}"""

        when:
        learningObjectiveService.update(loJSON, "unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Updating learning objective with cycles"() {
        given:
        String loJSON = """
        {
            "id": "ashf2394assq",
            "name": "loName1",
            "description": "loDescription1",
            "learningObjectiveList":[
                {
                    "id": "ashf2394bb67",
                    "name": "loName2",
                    "description": "loDescription2",
                    "learningObjectiveList":[
                        {
                           "id": "ashf2394assq",
                            "name": "loName1",
                            "description": "loDescription1",
                            "learningObjectiveList":[]
                        }
                    ]
                }
            ]
        }
        """

        when:
        learningObjectiveService.update(loJSON, "ashf2394assq", locale)

        then: "CoreException must be thrown because a cycle was found"
        thrown(CoreException)
    }

    def "Deleting existing learning objective"() {
        setup:
        LearningObjective learningObjective = new LearningObjective()

        when:
        LearningObjective res = learningObjectiveService.delete("testId", locale)

        then:
        1 * learningObjectiveRepository.findById(_ as String) >> learningObjective
        1 * learningObjectiveRepository.delete(_ as LearningObjective)
        res == learningObjective
    }

    def "Deleting non existing learning objective"() {
        when:
        learningObjectiveService.delete("unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding existing learning objective by its id"() {
        setup:
        LearningObjective learningObjective = new LearningObjective()

        when:
        LearningObjective res = learningObjectiveService.findById("testId", locale)

        then:
        1 * learningObjectiveRepository.findById(_ as String) >> learningObjective
        res == learningObjective
    }

    def "Trying to find non existing learning objective"() {
        when:
        learningObjectiveService.findById("unknownId", locale)

        then:
        thrown(CoreException)
    }

    def "Finding all learning objectives"() {
        given:
        int from = 0
        int size = 1
        boolean all = true

        when:
        learningObjectiveService.findAll(from, size, all, locale)

        then:
        1 * learningObjectiveRepository.count() >> 10
        1 * learningObjectiveRepository.findAll(_ as PageRequest)
    }

    def "Finding learning objectives with parameter 'size' <= 0"() {
        given:
        int from = 0
        int zeroSize = 0
        int negativeSize = -1
        boolean all = false

        when:
        learningObjectiveService.findAll(from, zeroSize, all, locale)
        learningObjectiveService.findAll(from, zeroSize, !all, locale)

        then:
        thrown(CoreException)

        when:
        learningObjectiveService.findAll(from, negativeSize, all, locale)
        learningObjectiveService.findAll(from, negativeSize, !all, locale)

        then:
        thrown(CoreException)
    }

    def "Find arbitrary number of learning objectives with correct parameters"() {
        given:
        int from = 0
        int size = 15
        boolean all = false

        when:
        learningObjectiveService.findAll(from, size, all, locale)

        then:
        1 * learningObjectiveRepository.count() >> 100
        1 * learningObjectiveRepository.findAll(_ as PageRequest)
    }
}
