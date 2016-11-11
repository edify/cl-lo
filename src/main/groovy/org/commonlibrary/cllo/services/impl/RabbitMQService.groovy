package org.commonlibrary.cllo.services.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.MessageProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Created by diugalde on 22/09/16.
 */

@Service
@Profile('INDEX_RMQ')
class RabbitMQService implements org.commonlibrary.cllo.services.QueueIndexService {

    @Value('${rabbitmq.general.lo_queue}')
    String queueName

    @Autowired
    ConnectionFactory connectionFactory

    @Autowired
    org.commonlibrary.cllo.dao.ContentDAO contentDAO

    @Autowired
    private MessageSource messageSource

    @Override
    def addLearningObject(org.commonlibrary.cllo.model.LearningObject lo, Locale locale) {
        try {
            ObjectMapper mapper = new ObjectMapper()
            def msgJSON = mapper.writeValueAsString(['action': 'add', 'content': lo])
            enqueueMsg(msgJSON)
        } catch(Exception e) {
            String[] args = ['LearningObject', lo.getId(), e.getMessage()]
            String m = messageSource.getMessage("index.m1", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    @Override
    def removeLearningObject(String loId, Locale locale) {
        try {
            ObjectMapper mapper = new ObjectMapper()
            def msgJSON = mapper.writeValueAsString(['action': 'remove', 'content': loId])
            enqueueMsg(msgJSON)
        } catch(Exception e) {
            String[] args = ['LearningObject', loId, e.getMessage()]
            String m = messageSource.getMessage("index.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    @Override
    def updateLearningObject(org.commonlibrary.cllo.model.LearningObject lo, Boolean updateFile, Locale locale) {
        try {
            def fileUrl = null
            if (updateFile) {
                def fileFormat = lo.format?.name()?.toLowerCase()

                def ignoredFormats = ['video', 'audio', 'image', 'multimedia', 'url']

                if (fileFormat && !ignoredFormats.contains(fileFormat) && lo.contents?.url) {
                    def referenceId = getFileReferenceId(lo.contents.url)
                    fileUrl = contentDAO.getTargetUrl(referenceId)
                } else if (fileFormat == 'url') {
                    fileUrl = lo.externalUrl
                }
            }
            ObjectMapper mapper = new ObjectMapper()
            String msgJSON = mapper.writeValueAsString([
                    'action': 'update',
                    'content': [
                            'doc': lo,
                            'fileUrl': fileUrl
                    ]
            ])
            enqueueMsg(msgJSON)
        } catch(Exception e) {
            String[] args = ['LearningObject', lo.getId(), e.getMessage()]
            String m = messageSource.getMessage("index.m3", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    private def enqueueMsg(String msg) {
        try {
            Connection connection = connectionFactory.newConnection()
            Channel channel = connection.createChannel()
            channel.queueDeclare(queueName, true, false, false, null)

            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("UTF-8"))
            connection.close()
        } catch(Exception e) {
            throw e
        }
    }

    private def getFileReferenceId(String url) {
        def idFile
        if (url.contains('?')) {
            String [] urlParamSplit = url.split("\\?")
            String filename = urlParamSplit[0].tokenize('/')[-1]
            String [] refParamSplit = urlParamSplit[1].split('=')

            if (refParamSplit.length > 1) {
                String refPathValue = refParamSplit[1]
                idFile = refPathValue + filename
            } else {
                idFile = filename
            }
        } else {
            idFile = url.tokenize('/')[-1]
        }
        return idFile
    }
}
