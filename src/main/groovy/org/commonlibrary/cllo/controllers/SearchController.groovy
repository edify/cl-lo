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


 package org.commonlibrary.cllo.controllers

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.mangofactory.swagger.annotations.ApiIgnore
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import org.commonlibrary.cllo.model.BaseEntity
import org.commonlibrary.cllo.services.SearchService
import org.commonlibrary.cllo.support.ApiException
import org.commonlibrary.cllo.support.ErrorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 9/10/14
 * Time: 9:43 AM
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping('${api.base.url}/search')
@Api(value = 'search', description = 'Search operations')
class SearchController {

    @Autowired
    SearchService searchService

    @Autowired
    MessageSource messageSource

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = 'Search by query', notes = 'Performs a search over the data and returns a set of objects that matches the query')
    public @ResponseBody List<BaseEntity> search(
        @ApiParam(value = 'The query to do the search')
        @RequestParam(value = "query", required = true) String query,
        @ApiParam(value = 'Initial value for the results')
        @RequestParam(value = "from", required = true) int from,
        @ApiParam(value = 'Amount of results to retrieve starting from the initial value')
        @RequestParam(value = "size", required = true) int size,
        @ApiParam(value = 'Words to exclude from the results (comma separated)')
        @RequestParam(value = "exclusions", defaultValue = "") String exclusions,
        @ApiParam(value = 'Words that are more relevant for the search (comma separated)')
        @RequestParam(value = "inclusions", defaultValue = "") String inclusions,
        @ApiParam(value = 'Kind of object over which the search will be performed, either LearningObject or Curriculum')
        @RequestParam(value = "entityType", defaultValue = "LearningObject", required = true) String entityType,
        @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            def inclusionList = inclusions.split(',') as List
            def exclusionList = exclusions.split(',') as List

            def results = []

            if (entityType == 'LearningObject') {
                results = searchService.search(query, inclusionList, exclusionList, from, size, locale)
            }

            return results
        }
        catch (Exception e){
            throw new ApiException('Api Exception', e)
        }
    }


    @RequestMapping(value = "/altTerms", method = RequestMethod.GET)
    @ApiOperation(value = "Find suggestions", notes = "Returns a list of term suggestions for a specific query")
    public @ResponseBody List<String> suggestAltTerms(
        @ApiParam(value = 'The query to retrieve suggestions for')
        @RequestParam(value = "query", required = true) String query,
        @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return searchService.suggestAltTerms(query, locale)
        }
        catch (Exception e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = "/moreLikeThis/{id}", method=RequestMethod.GET)
    @ApiOperation(value = "Search more like this (result) by id", notes = 'Performs a search over the data looking for objects similar to a specific entity by its id')
    public @ResponseBody List<BaseEntity> searchMoreLikeThis(
        @ApiParam(value = 'The id of the object which you want more similar results')
        @PathVariable String id,
        @ApiParam(value = 'Initial value for the results')
        @RequestParam(value = "from", required = true) int from,
        @ApiParam(value = 'Amount of results to retrieve starting from the initial value')
        @RequestParam(value = "size", required = true) int size,
        @ApiParam(value = 'Kind of object over which the search will be performed, either LearningObject or Curriculum')
        @RequestParam(value = "entityType", defaultValue = "", required = true) String entityType,
        @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            def results = []

            if (entityType == 'LearningObject') {
                results = searchService.searchMoreLikeThis(id, from, size, locale)
            }

            return results
        }
        catch (Exception e){
            throw new ApiException('Api Exception', e)
        }
    }

    @ExceptionHandler(ApiException.class)
    public @ResponseBody ErrorResponse handleApiError(HttpServletRequest req, HttpServletResponse response, ApiException apiException, Locale locale) {

        ErrorResponse er = new ErrorResponse()
        String [] error = apiException.cause.message.split('\\|')
        Logger logger = LoggerFactory.getLogger("org.commonlibrary.cllo.controllers.SearchController")

        if (error.length == 3) {
            er.code = error[0]
            er.message = error[1]
            response.setStatus(Integer.parseInt(error[2]))
        } else {
            er.code = "4001"
            def causeMsg = apiException.cause.message ? apiException.cause.message : ""
            er.message = """Internal Server Error. ${causeMsg}"""
            response.setStatus(500)
        }

        if (!apiException.cause.cause) {
            logger.info(er.code + ': ' + er.message)
        } else {
            String errId = Hashing.sha256().newHasher().putString(UUID.randomUUID().toString(), Charsets.UTF_8).hash().toString()
            String[] args = [ errId ]
            String m = messageSource.getMessage("error.user_message", args, locale);
            er.description = m
            logger.info(er.code + ': ' + er.message + ' : '+ errId + ' : \nStackTrace:\n'+ apiException.getFormattedStackTrace())
        }
        return er
    }

}
