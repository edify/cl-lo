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
