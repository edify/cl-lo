package org.commonlibrary.cllo.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.MongoClient
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.model.LearningObjective
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.SimpleMongoDbFactory
import org.springframework.stereotype.Component

/**
 * Created by diugalde on 07/09/16.
 */

@Component
@Profile(['Integration_Tests'])
class TestDataBootstrap implements InitializingBean {

    @Value('${spring.data.mongodb.uri}')
    private String mongoURI

    private def mongoTemplate;

    @Override
    void afterPropertiesSet() throws Exception {
        initMongoDB()

        insertLearningObjectives()
        insertLearningObjects()
    }

    /**
     * Drop mongo test database in case it exists.
     */
    private def initMongoDB() {
        def dbName = mongoURI.tokenize('/')[-1]

        def mongoDBFactory = new SimpleMongoDbFactory(new MongoClient(), dbName);
        this.mongoTemplate = new MongoTemplate(mongoDBFactory);

        this.mongoTemplate.db.dropDatabase()
    }

    /**
     * Insert two learning objectives for testing purposes.
     */
    private def insertLearningObjectives() {
        def loJSON1 = '''
                {
                "name": "Learning Objective 1 - Integration Test",
                "description": "Learning Objective 1 - Integration Test Description",
                "learningObjectiveList": []
                }
        '''

        def loJSON2 = '''
                {
                "name": "Learning Objective 2 - Integration Test",
                "description": "Learning Objective 2 - Integration Test Description",
                "learningObjectiveList": []
                }
        '''

        def learningObjectiveList = [loJSON1, loJSON2]
        def ids = ['integrationtestsloi00001', 'integrationtestsloi00002']

        learningObjectiveList.eachWithIndex{ def loJSON, def i ->
            def lo = new LearningObjective()
            def mapper = new ObjectMapper()
            def loC = mapper.readValue(loJSON, LearningObjective.class)
            lo.CopyValues(loC)
            lo.setId(ids[i])
            this.mongoTemplate.save(lo)
        }
    }

    /**
     * Insert two learning objects for testing purposes.
     */
    private def insertLearningObjects() {
        def loJSON1 = '''
            {
                "name": "Learning Object 1 - Integration Test",
                "subject": "Learning Object 1 subject",
                "description": "Learning Object 1 description",
                "title": "Learning Object 1 title",
                "type": "EXERCISE",
                "format": "IMAGE",
                "metadata": {},
                "enabled": true,
                "learningObjectiveList": []
           }
        '''

        def loJSON2 = '''
            {
                "name": "Learning Object 2 - Integration Test",
                "subject": "Learning Object 2 subject",
                "description": "Learning Object 2 description",
                "title": "Learning Object 2 title",
                "type": "EXERCISE",
                "format": "IMAGE",
                "metadata": {},
                "enabled": true,
                "learningObjectiveList": [
                    {
                    "id": "integrationtestsloi00001",
                    "name": "Learning Objective 1 - Integration Test",
                    "description": "Learning Objective 1 - Integration Test Description",
                    "learningObjectiveList": []
                    }
                ]
           }
        '''

        def ids = ['integrationtestslo000001', 'integrationtestslo000002']
        def learningObjectList = [loJSON1, loJSON2]

        learningObjectList.eachWithIndex{ def loJSON, def i ->
            def lo = new LearningObject()
            def mapper = new ObjectMapper()
            def loC = mapper.readValue(loJSON, LearningObject.class)
            lo.CopyValues(loC)
            lo.setId(ids[i])
            this.mongoTemplate.save(lo)
        }
    }

}
