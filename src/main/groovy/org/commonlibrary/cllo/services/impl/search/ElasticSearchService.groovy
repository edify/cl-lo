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


 package org.commonlibrary.cllo.services.impl.search

import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.repositories.LearningObjectRepository
import org.commonlibrary.cllo.services.SearchService
import org.commonlibrary.cllo.util.CoreException
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

/**
 * Created by diugalde on 28/09/16.
 */

@Service
@Profile('SRCH_ES')
class ElasticSearchService implements SearchService {

    @Value('${elastic_search.general.lo_index}')
    String loIndexName

    @Value('${elastic_search.general.lo_type}')
    String loType

    @Value('${elastic_search.more_like_this.min_doc_freq}')
    int minDocFreq

    @Value('${elastic_search.more_like_this.min_term_freq}')
    int minTermFreq

    @Value('${elastic_search.more_like_this.minimum_should_match}')
    String minimumShouldMatch

    @Autowired
    TransportClient elasticSearchClient

    @Autowired
    private LearningObjectRepository learningObjectRepository

    @Autowired
    private MessageSource messageSource


    @Override
    List<LearningObject> search(String query, List<String> inclusions, List<String> exclusions, int searchFrom, int searchSize, Locale locale) {
        try {
            def exclusionsQuery = exclusions.join(' ')
            def inclusionsQuery = inclusions.join(' ')

            def querySplit = query.split('\\+')

            def filterQuery = getFilterQuery(querySplit.tail(), locale)

            // Concatenate inclusion terms to the original query.
            query = "${inclusionsQuery} ${querySplit[0]}"

            def queryType = """
                "query_string" : {
                    "query" : "${query}"
                }
            """

            if (query.trim() == '') {
                queryType = """  "match_all": {}  """
            }

            def jsonQuery = """
                {
                    "fields": [],
                    "from": ${searchFrom},
                    "size": ${searchSize},
                    "query": {
                        "bool": {
                            "must" : [
                                {
                                    ${queryType}
                                }
                            ],
                            "must_not" :
                            [
                                {
                                    "query_string" :
                                    {
                                        "query" : "${exclusionsQuery}"
                                    }
                                }
                            ],
                            ${filterQuery}
                        }
                    }
                }
            """

            SearchResponse response = elasticSearchClient.prepareSearch(loIndexName).setTypes(loType)
                                        .setSource(jsonQuery).execute().actionGet()

            def result = response.getHits().collect() {
                return learningObjectRepository.findById(it.id)
            }

            return result
        } catch(Exception e) {
            String[] args = []
            String m = e.getMessage() ?: messageSource.getMessage("search.m1", args, locale)
            throw new CoreException(m, e)
        }
    }

    @Override
    List<String> suggestAltTerms(String query, Locale locale) {
        try {
            def jsonQuery = """
                {
                    "size": 0,
                    "query": {
                        "query_string" : {
                            "query" : "${query}"
                        }
                    },
                    "suggest" : {
                        "lo-suggestion" : {
                            "text" : "${query}",
                            "term" : {
                                "field" : "file.content",
                                "sort": "score",
                                "suggest_mode": "always"
                            }
                        }
                    }
                }
            """

            SearchResponse response = elasticSearchClient.prepareSearch(loIndexName).setTypes(loType)
                    .setSource(jsonQuery).execute().actionGet()

            def result = []
            def numberOfSuggestions = 0

            response.suggest.getSuggestion('lo-suggestion').each() {
                if (it.options.size() > 0 && it.options[0].score >= 0.7) {
                    result << it.options[0].text.toString()
                    numberOfSuggestions++
                } else {
                    result << it.text.toString()
                }
            }

            return numberOfSuggestions > 0 ? result : []
        } catch(Exception e) {
            String[] args = []
            String m = messageSource.getMessage("search.m1", args, locale)
            throw new CoreException(m, e)
        }
    }

    @Override
    List<LearningObject> searchMoreLikeThis(String id, int searchFrom, int searchSize, Locale locale) {
        try {
            def jsonQuery = """
                {
                    "fields": [],
                    "from": ${searchFrom},
                    "size": ${searchSize},
                    "query": {
                        "more_like_this": {
                            "fields" : [
                                "file.content", "title", "description", "metadata.keywords", "metadata.author",
                                "metadata.subject", "learningObjectiveList.description"
                            ],
                            "like" : [
                                {
                                    "_index" : "${loIndexName}",
                                    "_type" : "${loType}",
                                    "_id" : "${id}"
                                }
                            ],
                            "min_doc_freq" : ${minDocFreq},
                            "min_term_freq" : ${minTermFreq},
                            "minimum_should_match" : "${minimumShouldMatch}"
                        }
                    }
                }
            """

            SearchResponse response = elasticSearchClient.prepareSearch(loIndexName).setTypes(loType)
                    .setSource(jsonQuery).execute().actionGet()

            def result = response.getHits().collect() {
                return learningObjectRepository.findById(it.id)
            }

            return result
        } catch(Exception e) {
            String[] args = []
            String m = messageSource.getMessage("search.m1", args, locale)
            throw new CoreException(m, e)
        }
    }

    /**
     *
     * @param filterList - List with strings: ['format:IMAGE', 'type:TEST']
     * @return string - Filter query with the form: "filter": [...]
     */
    private def getFilterQuery(filterList, Locale locale) {
        try {
            def filters = filterList.collect() {
                def filterSplit = it.split(':')
                def field = filterSplit[0]
                def value = filterSplit[1]
                return """  { "match": { "${field}": "${value}" } }  """
            }
            return """  "filter": [${filters.join(',')}]  """
        } catch (Exception e) {
            String[] args = []
            String m = messageSource.getMessage("search.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

}
