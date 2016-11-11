package org.commonlibrary.cllo.auth.service

import com.lambdaworks.redis.api.StatefulRedisConnection
import com.lambdaworks.redis.api.sync.RedisCommands
import org.commonlibrary.clauth.SAuthc1Signer
import org.commonlibrary.clauth.services.impl.ApiKeyServiceImpl
import org.commonlibrary.cllo.auth.service.impl.SAuthc1ServiceImpl
import org.springframework.test.util.ReflectionTestUtils
import spock.lang.*

/**
 * Created by diugalde on 13/09/16.
 */
class AuthServiceSpec extends Specification {

    @Shared
    AuthService authService = new SAuthc1ServiceImpl()

    def setup() {
        // Mock redisConnection.
        def redisConnection = Mock(StatefulRedisConnection)
        def redisCommands = Mock(RedisCommands)
        redisCommands.hget(_, _) >> 'LN2qSArEGS3kI0CKGLNwM4T0neC9H6PALtb3yr9vGD3ep1ljviTsaEFD44Ti4TlT'
        redisConnection.sync() >> redisCommands

        // Inject apiKeyService attributes.
        def apiKeyService = new ApiKeyServiceImpl(redisConnection, 'holacomoestas')

        // Inject authService dependencies.
        def sauthc1Signer = new SAuthc1Signer()
        ReflectionTestUtils.setField(authService, 'sAuthc1Signer', sauthc1Signer)
        ReflectionTestUtils.setField(authService, 'apiKeyService', apiKeyService)
    }

    def "Authenticate incoming request with valid authorization headers"() {
        given:
        def authHeader = 'SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160913/SwXjqNYRao0FVnx/sauthc1_request, ' +
                         'sauthc1SignedHeaders=host;x-stormpath-date, ' +
                         'sauthc1Signature=b1b5498294187cb1e14fbf222f795edc7a37a4db8b2f018d6d3b85b2c1e81d69'

        def dateHeader = '20160913T170507Z'

        def headers = new HashMap()
        headers.put('authorization', authHeader)
        headers.put('x-stormpath-date', dateHeader)

        def method = 'get'
        def requestURL = 'http://localhost:8080/api/v1/linkedlearningobjects?all=true&from=0&size=1&name=Non%20existing%20name'
        def body = ''

        when:
        def res = authService.authenticate(headers, method, requestURL, body)

        then: "The result must be true"
        res == true
    }

    def "Reject incoming request that doesn't have a valid date header"() {
        given:
        def authHeader = 'SAuthc1 sauthc1Id=5c4d6a3bdc68ffb02e3ce309964ac558/20160913/SwXjqNYRao0FVnx/sauthc1_request, ' +
            'sauthc1SignedHeaders=host;x-stormpath-date, ' +
            'sauthc1Signature=b1b5498294187cb1e14fbf222f795edc7a37a4db8b2f018d6d3b85b2c1e81d69'

        def dateHeader = '20140701T111111Z'

        def headers = new HashMap()
        headers.put('x-stormpath-date', dateHeader)
        headers.put('authorization', authHeader)

        def method = 'get'
        def requestURL = 'http://localhost:8080/api/v1/linkedlearningobjects?all=true&from=0&size=1&name=Non%20existing%20name'
        def body = ''

        when: "The date header is wrong"
        def res = authService.authenticate(headers, method, requestURL, body)

        then: "The result must be false"
        res == false
    }


    def "Reject incoming request that doesn't have a valid apiKeyId"() {
        given:
        def authHeader = 'SAuthc1 sauthc1Id=InvalidApiKeyId/20160913/SwXjqNYRao0FVnx/sauthc1_request, ' +
            'sauthc1SignedHeaders=host;x-stormpath-date, ' +
            'sauthc1Signature=b1b5498294187cb1e14fbf222f795edc7a37a4db8b2f018d6d3b85b2c1e81d69'

        def dateHeader = '20160913T170507Z'

        def headers = new HashMap()
        headers.put('x-stormpath-date', dateHeader)
        headers.put('authorization', authHeader)

        def method = 'get'
        def requestURL = 'http://localhost:8080/api/v1/linkedlearningobjects?all=true&from=0&size=1&name=Non%20existing%20name'
        def body = ''

        when: "The apiKeyId does not exist"
        def res = authService.authenticate(headers, method, requestURL, body)

        then: "The result must be false"
        res == false
    }


}
