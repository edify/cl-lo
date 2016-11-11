package org.commonlibrary.cllo.services

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.commons.codec.binary.Base64
import org.apache.tika.io.IOUtils
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.repositories.LearningObjectRepository
import org.commonlibrary.cllo.services.impl.search.ElasticSearchService
import org.commonlibrary.cllo.util.CoreException
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.springframework.context.MessageSource
import org.springframework.test.util.ReflectionTestUtils
import org.yaml.snakeyaml.Yaml
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by diugalde on 03/10/16.
 */

class ElasticSearchServiceSpec extends Specification {

    @Shared
    ElasticSearchService elasticSearchService = new ElasticSearchService()

    @Shared
    Locale locale = Locale.US

    @Shared
    String loIndexName

    @Shared
    String loType

    @Shared
    int minDocFreq = 1

    @Shared
    int minTermFreq = 1

    @Shared
    String minimumShouldMatch = '30%'

    @Shared
    TransportClient elasticSearchClient

    @Shared
    LearningObjectRepository learningObjectRepository

    def setupSpec() {
        // Load ElasticSearch configuration.
        Yaml yml = new Yaml()
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream('elasticSearchConfig.yml')
        def elasticSearchConfig = yml.loadAs(is, HashMap)
        is.close()

        loIndexName = elasticSearchConfig['lo_index']
        loType = elasticSearchConfig['lo_type']

        // Set @Value attributes.
        ReflectionTestUtils.setField(elasticSearchService, 'loIndexName', loIndexName)
        ReflectionTestUtils.setField(elasticSearchService, 'loType', loType)
        ReflectionTestUtils.setField(elasticSearchService, 'minDocFreq', minDocFreq)
        ReflectionTestUtils.setField(elasticSearchService, 'minTermFreq', minTermFreq)
        ReflectionTestUtils.setField(elasticSearchService, 'minimumShouldMatch', minimumShouldMatch)

        // Service autowired fields will be mocks. Mocks' default behavior is to return null.
        MessageSource ms = Mock()
        ReflectionTestUtils.setField(elasticSearchService, 'messageSource', ms)

        learningObjectRepository = Mock()
        // findById method will return a LearningObject that will have the parameter as its id.
        learningObjectRepository.findById(_ as String) >> { String id ->
            def lo = new LearningObject()
            lo.id = id
            return lo
        }
        ReflectionTestUtils.setField(elasticSearchService, 'learningObjectRepository', learningObjectRepository)

        // Initialize elasticSearchClient and insert test data (one LearningObject for each file format).
        initElasticSearch(elasticSearchConfig['binary']['host'], elasticSearchConfig['binary']['port'])
        insertTestData()
        ReflectionTestUtils.setField(elasticSearchService, 'elasticSearchClient', elasticSearchClient)
    }

    def cleanupSpec() {
        // Delete the test index.
        elasticSearchClient.admin().indices().prepareDelete(loIndexName).get()
    }

    def "Searching learning objects using the filter feature"() {
        given:
        def query1 = '+format:WORD'
        def query2 = '+metadata.context:SECONDARY_EDUCATION+type:ANY'

        when: 'The results are filtered by format'
        def result1 = elasticSearchService.search(query1, [], [], 0, 10, locale).collect() { it.id }

        then:
        result1 == ['integrationtestword']

        when: 'The results are filtered by context and type'
        def result2 = elasticSearchService.search(query2, [], [], 0, 10, locale).collect() { it.id }

        then:
        result2 == ['integrationtestodt', 'integrationtestjson']
    }

    def "Searching learning objects using the inclusions feature"() {
        given:
        def query = '+metadata.context:SECONDARY_EDUCATION'
        def inclusions = ['potter']

        when:
        def result = elasticSearchService.search(query, inclusions, [], 0, 10, locale).collect() { it.id }

        then:
        result == ['integrationtestodt']
    }

    def "Searching learning objects using the exclusions feature"() {
        given:
        def query = '+metadata.context:SECONDARY_EDUCATION'
        def exclusions = ['harry', 'potter']

        when:
        def result = elasticSearchService.search(query, [], exclusions, 0, 10, locale).collect() { it.id }

        then:
        result == ['integrationtestjson']
    }

    def "Paginating search results"() {
        given:
        def query = '+metadata.context:SECONDARY_EDUCATION'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestodt']
    }

    def "Paginating search results with invalid parameters"() {
        given:
        def query = 'anyQuery'
        def fromParameter = -2

        when:
        elasticSearchService.search(query, [], [], fromParameter, 1, locale)

        then:
        thrown(CoreException)
    }

    def "Search content in a Word file"() {
        given:
        def query = 'Louvre Palace'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestword']
    }

    def "Search content in a HTML file"() {
        given:
        def query = 'Pérez Zeledón'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtesthtml']
    }

    def "Search content in a JSON file"() {
        given:
        def query = 'Cyclone cariolis effect'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestjson']
    }

    def "Search content in a ODT file"() {
        given:
        def query = 'hermione hogwarts potter'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestodt']
    }

    def "Search content in a PDF file"() {
        given:
        def query = 'baylor ICPC'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestpdf']
    }

    def "Search content in a PlainText file"() {
        given:
        def query = 'Graph-theoretic'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtesttxt']
    }

    def "Search content in a Excel file"() {
        given:
        def query = 'Gerard Piqué'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestexcel']
    }

    def "Search content in a XML file"() {
        given:
        def query = '662xc$5#ksd'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestxml']
    }

    def "Search content in a PowerPoint file"() {
        given:
        def query = 'Paoli Pike'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestpowerpoint']
    }

    def "Search content in a ODP file"() {
        given:
        def query = 'Tom Preston-Werner'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestodp']
    }

    def "Search content in a ODS file"() {
        given:
        def query = 'Dennis Ritchie Prolog'

        when:
        def result = elasticSearchService.search(query, [], [], 0, 1, locale).collect() { it.id }

        then:
        result == ['integrationtestods']
    }

    def "Suggesting alternative terms for a query"() {
        given:
        def query = 'Wikimedia grap teory'

        when:
        def result = elasticSearchService.suggestAltTerms(query, locale)

        then:
        result == ['wikipedia', 'graph', 'theory']

        when: "There are no suggestions"
        query = 'unknownstring sfdyu3'
        result = elasticSearchService.suggestAltTerms(query, locale)

        then:
        result == []

        when: "There are some terms without suggestions"
        query = 'Wikipedia graph theory'
        result = elasticSearchService.suggestAltTerms(query, locale)

        then:
        result == ['wikipedia', 'graphs', 'theory']
    }

    def "Searching similar objects"() {
        given:
        def id = 'integrationtestword'

        when: "The id exists"
        def result1 = elasticSearchService.searchMoreLikeThis(id, 0, 10, locale).collect() { it.id }

        then:
        result1 == ['integrationtesthtml', 'integrationtestpdf', 'integrationtestodt', 'integrationtestxml',
                    'integrationtesttxt', 'integrationtestpowerpoint', 'integrationtestjson', 'integrationtestodp']

        when: "The id does not exist"
        def result2 = elasticSearchService.searchMoreLikeThis('UnknownId', 0, 10, locale).collect() { it.id }

        then:
        result2 == []
    }

    private def initElasticSearch(String host, int port) {
        try {
            elasticSearchClient = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port))
            initLearningObjectIndex()
        } catch(Exception e) {
            def msg = """
            There was an error initializing ElasticSearch. Make sure the elasticSearchConfig.yml contains valid data
            and the Elastic Search instance is currently running.
            """
            throw new Exception(msg, e.cause)
        }
    }

    private def initLearningObjectIndex() {
        def indexExists = elasticSearchClient.admin().indices().prepareExists(loIndexName).execute().actionGet().isExists();

        if (!indexExists) {
            def createTypeJSON = """
                {
                    "${loType}": {
                        "properties": {
                            "file": {
                                "type": "attachment",
                                "fields": {
                                    "content": {
                                        "term_vector" : "yes",
                                        "store": true
                                    }
                                }
                            }
                        }
                    }
                }
            """

            elasticSearchClient.admin().indices().prepareCreate(loIndexName).addMapping(loType, createTypeJSON).get()
        }
    }

    private def loadTestData() {
        def contextLoader = Thread.currentThread().getContextClassLoader()

        def is = contextLoader.getResourceAsStream('testData.json')
        def jsonString = new String(IOUtils.toByteArray(is),'UTF-8')
        is.close()

        def data = new JsonSlurper().parseText(jsonString)

        def jsonList = data.collect() {
            def fileName = it.fileName
            def learningObject = it.learningObject
            def testFileIS = contextLoader.getResourceAsStream("testFiles/${fileName}")

            def base64 = new String(Base64.encodeBase64(testFileIS.getBytes()))
            testFileIS.close()
            learningObject.file = base64

            return learningObject
        }
        return jsonList
    }

    private def insertTestData() {
        def dataList = loadTestData()
        def jsonOutput = new JsonOutput()
        dataList.each() {
            def jsonLearningObject = jsonOutput.toJson(it)
            elasticSearchClient.prepareIndex(loIndexName, loType, it.id).setSource(jsonLearningObject).get()
        }
        // Refresh index.
        elasticSearchClient.admin().indices().prepareRefresh(loIndexName).get()
    }

}
