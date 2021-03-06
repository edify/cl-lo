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
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.services.LearningObjectService
import org.commonlibrary.cllo.support.ApiException
import org.commonlibrary.cllo.support.ErrorResponse
import org.commonlibrary.cllo.util.CoreException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created by amasis on 1/7/15.
 */
@Controller
@RequestMapping('${api.base.url}/linkedlearningobjects')
@Api(value = 'linkedLearningObjects', description = 'Search by Learning Objectives')
class LinkedLearningObjectController {

    @Autowired
    LearningObjectService learningObjectService

    @Autowired
    MessageSource messageSource

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = 'Find the Learning Objects that contains a specific Learning Objective', notes = 'Returns a list of the Learning Objects that contains a specific Learning Objective')
    public @ResponseBody List<LearningObject> findByLearningObjective(
            @ApiParam(value = 'Initial value for the results')
            @RequestParam(value = "from", required = false, defaultValue = '0') int from,
            @ApiParam(value = 'Amount of results to retrieve starting from the initial value')
            @RequestParam(value = "size", required = false, defaultValue = '10') int size,
            @ApiParam(value = 'Name of the Learning objective that we want to be part of the retrieved learning objects')
            @RequestParam(value = "name", required = false, defaultValue = '') String name,
            @ApiParam(value = 'If this parameter is set to true it returns all the Learning Objects, if false then returns a page based in the from and size parameters')
            @RequestParam(value = "all", required = true) boolean all,
            @ApiIgnore
                    Locale locale) throws ApiException
    {
        try {
            return learningObjectService.findByLearningObjective(name, from, size, all, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @ExceptionHandler(ApiException.class)
    public @ResponseBody ErrorResponse handleApiError(HttpServletRequest req, HttpServletResponse response, ApiException apiException, Locale locale) {

        ErrorResponse er = new ErrorResponse()
        String [] error = apiException.cause.message.split('\\|')
        Logger logger = LoggerFactory.getLogger("org.commonlibrary.cllo.controllers.LinkedLearningObjectController")

        if (error.length == 3) {
            er.code = error[0]
            er.message = error[1]
            response.setStatus(Integer.parseInt(error[2]))
        } else {
            er.code = "1002"
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
