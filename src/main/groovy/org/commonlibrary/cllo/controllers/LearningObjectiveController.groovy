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

import ch.qos.logback.classic.Logger
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.mangofactory.swagger.annotations.ApiIgnore
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import org.commonlibrary.cllo.model.LearningObjective
import org.commonlibrary.cllo.services.LearningObjectiveService
import org.commonlibrary.cllo.support.ApiException
import org.commonlibrary.cllo.support.ErrorResponse
import org.commonlibrary.cllo.util.CoreException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/24/14
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping('${api.base.url}/learningObjectives')
@Api(value = 'learningObjectives', description = 'Operations about Learning Objectives')
class LearningObjectiveController {

    @Autowired
    MessageSource messageSource

    @Autowired
    LearningObjectiveService learningObjectiveService

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ApiOperation(value = 'Find learning objective by ID', notes = 'Returns a specific learning objective by id')
    public @ResponseBody LearningObjective findById(
            @ApiParam(value = 'The id of the Learning Objective that we\'re looking for')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectiveService.findById(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = 'Find all learning objects in a page', notes = 'Returns a page of learning objectives based on the parameters or all learning objects when "all" parameter is set to true')
    public @ResponseBody Page<LearningObjective> findAll(
            @ApiParam(value = 'Initial value for the results')
            @RequestParam(value = "from", required = false, defaultValue = '0') int from,
            @ApiParam(value = 'Amount of results to retrieve starting from the initial value')
            @RequestParam(value = "size", required = false, defaultValue = '10') int size,
            @ApiParam(value = 'If this parameter is set to true it returns all the Learning Objects')
            @RequestParam(value = "all", required = true) boolean all,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectiveService.findAll(from, size, all, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = 'Insert learning objective', notes = "Creates a new learning objective based on the JSON representation received")
    public @ResponseBody LearningObjective insert(
            @ApiParam(value = 'JSON Representation of the Learning Objective to save')
            @RequestBody String learningObjectJSON,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectiveService.insert(learningObjectJSON, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{id}', method = RequestMethod.PUT)
    @ApiOperation(value = 'Update learning objective by ID', notes = 'Updates a specific learning objective by its id and based on the JSON representation received')
    public @ResponseBody LearningObjective update(
            @ApiParam(value = 'JSON Representation of the Curriculum to save')
            @RequestBody String learningObjectJSON,
            @ApiParam(value = 'The id of the Learning Objective that we want to update')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectiveService.update(learningObjectJSON, id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{id}', method = RequestMethod.DELETE)
    @ApiOperation(value = 'Hard delete learning objective', notes = 'Deletes a specific learning objective by its ID including the associated content')
    public @ResponseBody LearningObjective delete(
            @ApiParam(value = 'The id of the Learning Objective that we want to delete')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectiveService.delete(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @ExceptionHandler(ApiException.class)
    public @ResponseBody ErrorResponse handleApiError(HttpServletRequest req, HttpServletResponse response, ApiException apiException, Locale locale) {
        ErrorResponse er = new ErrorResponse()
        String [] error = apiException.cause.message.split('\\|')
        Logger logger = LoggerFactory.getLogger("org.commonlibrary.cllo.controllers.LearningObjectController")

        if (error.length == 3) {
            er.code = error[0]
            er.message = error[1]
            response.setStatus(Integer.parseInt(error[2]))
        } else {
            er.code = "3002"
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
