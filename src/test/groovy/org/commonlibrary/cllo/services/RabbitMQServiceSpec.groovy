package org.commonlibrary.cllo.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import org.commonlibrary.cllo.dao.ContentDAO
import org.commonlibrary.cllo.dao.impl.S3ContentDAO
import org.commonlibrary.cllo.model.Contents
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.model.metadatavalues.Format
import org.commonlibrary.cllo.services.impl.RabbitMQService
import org.springframework.context.MessageSource
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by diugalde on 27/09/16.
 */


class RabbitMQServiceSpec extends Specification {

    @Shared
    QueueIndexService queueIndexService = new RabbitMQService()

    @Shared
    Locale locale = Locale.US

    @Shared
    ContentDAO contentDAO

    @Shared
    Channel queueChannel;

    @Shared
    Connection queueConnection;

    @Shared
    ConnectionFactory connectionFactory;

    @Shared
    String queueName


    def setup() {
        // Service autowired fields will be mocks. Mocks' default behavior is to return null.
        MessageSource ms = Mock()
        ReflectionTestUtils.setField(queueIndexService, "messageSource", ms)

        contentDAO = Mock(S3ContentDAO)
        ReflectionTestUtils.setField(queueIndexService, "contentDAO", contentDAO)

        queueChannel = Mock(Channel)

        queueConnection = Mock(Connection)

        connectionFactory = Mock(ConnectionFactory)

        ReflectionTestUtils.setField(queueIndexService, "connectionFactory", connectionFactory)

        queueName = "testQueue"
        ReflectionTestUtils.setField(queueIndexService, "queueName", queueName)

    }

    def "Enqueue 'add learningObject' msg"() {
        setup:
        LearningObject lo = new LearningObject()
        ObjectMapper mapper = new ObjectMapper()
        def msgJSON = mapper.writeValueAsString(['action': 'add', 'content': lo])

        when:
        queueIndexService.addLearningObject(lo, locale)

        then:
        1 * connectionFactory.newConnection()  >> queueConnection
        1 * queueConnection.createChannel() >> queueChannel
        1 * queueChannel.queueDeclare(queueName, true, false, false, null)
        1 * queueChannel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msgJSON.getBytes("UTF-8"))
        1 * queueConnection.close()

    }

    def "Enqueue 'remove learningObject' msg"() {
        setup:
        def loId = 'testId'
        ObjectMapper mapper = new ObjectMapper()
        def msgJSON = mapper.writeValueAsString(['action': 'remove', 'content': loId])

        when:
        queueIndexService.removeLearningObject(loId, locale)

        then:
        1 * connectionFactory.newConnection()  >> queueConnection
        1 * queueConnection.createChannel() >> queueChannel
        1 * queueChannel.queueDeclare(queueName, true, false, false, null)
        1 * queueChannel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msgJSON.getBytes("UTF-8"))
        1 * queueConnection.close()
    }

    def "Enqueue 'update learningObject' msg with the updateContents parameter = true"() {
        given:
        LearningObject lo1 = new LearningObject()
        lo1.format = Format.URL
        lo1.externalUrl = 'www.google.com'
        ObjectMapper mapper = new ObjectMapper()
        String msgJSON1 = mapper.writeValueAsString([
                'action': 'update',
                'content': [
                        'doc': lo1,
                        'fileUrl': lo1.externalUrl
                ]
        ])

        Contents contents = new Contents()
        contents.url = '/learningObjects/loId/contents/contentsId/file/fileName?refPath=refPath/'
        LearningObject lo2 = new LearningObject()
        lo2.setFormat(Format.PDF)
        lo2.setContents(contents)
        String msgJSON2 = mapper.writeValueAsString([
                'action': 'update',
                'content': [
                        'doc': lo2,
                        'fileUrl': 'S3SignedUrl'
                ]
        ])

        when: "The LearningObject has an externalUrl"
        queueIndexService.updateLearningObject(lo1, true, locale)

        then:
        1 * connectionFactory.newConnection()  >> queueConnection
        1 * queueConnection.createChannel() >> queueChannel
        1 * queueChannel.queueDeclare(queueName, true, false, false, null)
        1 * queueChannel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msgJSON1.getBytes("UTF-8"))
        1 * queueConnection.close()

        when: "The LearningObject's contents is a searchable file stored in S3"

        queueIndexService.updateLearningObject(lo2, true, locale)

        then:
        // A signedUrl is retrieved first.
        1 * contentDAO.getTargetUrl('refPath/fileName') >> 'S3SignedUrl'

        1 * connectionFactory.newConnection()  >> queueConnection
        1 * queueConnection.createChannel() >> queueChannel
        1 * queueChannel.queueDeclare(queueName, true, false, false, null)
        1 * queueChannel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msgJSON2.getBytes("UTF-8"))
        1 * queueConnection.close()
    }


    def "Enqueue 'update learningObject' msg with the updateContents parameter = false"() {
        given:
        LearningObject lo = new LearningObject()
        ObjectMapper mapper = new ObjectMapper()
        String msgJSON = mapper.writeValueAsString([
                'action': 'update',
                'content': [
                        'doc': lo,
                        'fileUrl': null
                ]
        ])

        when:
        queueIndexService.updateLearningObject(lo, false, locale)

        then:
        1 * connectionFactory.newConnection()  >> queueConnection
        1 * queueConnection.createChannel() >> queueChannel
        1 * queueChannel.queueDeclare(queueName, true, false, false, null)
        1 * queueChannel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msgJSON.getBytes("UTF-8"))
        1 * queueConnection.close()
    }
}
