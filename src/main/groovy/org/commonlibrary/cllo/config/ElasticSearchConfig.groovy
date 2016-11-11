package org.commonlibrary.cllo.config

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Created by diegomunguia on 27/02/15.
 */


@Configuration
@Profile('SRCH_ES')
class ElasticSearchConfig {

    @Value('${elastic_search.general.binary.host}')
    String host

    @Value('${elastic_search.general.binary.port}')
    int port

    @Value('${elastic_search.general.lo_index}')
    String loIndexName

    @Value('${elastic_search.general.lo_type}')
    String loType

    TransportClient elasticSearchClient

    @Bean
    TransportClient ESTransportClient() {
        elasticSearchClient = TransportClient.builder().build()
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port))
        initLearningObjectIndex()
        return elasticSearchClient
    }

    private void initLearningObjectIndex() {
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
                                        "term_vector": "yes",
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

}
