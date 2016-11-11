package org.commonlibrary.cllo.controllers

import ch.qos.logback.classic.Logger
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.mangofactory.swagger.annotations.ApiIgnore
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import org.commonlibrary.cllo.model.Curriculum
import org.commonlibrary.cllo.services.CurriculumService
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

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
@Controller
@RequestMapping('${api.base.url}/linkedcurricula')
@Api(value = 'linkedCurricula', description = 'Search by Learning Objectives')
class LinkedCurriculaController {

    @Autowired
    CurriculumService curriculumService

    @Autowired
    MessageSource messageSource

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = 'Find the Curricula that contains a specific Learning Objective', notes = 'Returns a list of the Curricula that contains a specific Learning Objective')
    public @ResponseBody List<Curriculum> findCurriculaUsages(
            @ApiParam(value = 'Initial value for the results')
            @RequestParam(value = "from", required = false, defaultValue = '0') int from,
            @ApiParam(value = 'Amount of results to retrieve starting from the initial value')
            @RequestParam(value = "size", required = false, defaultValue = '10') int size,
            @ApiParam(value = 'Name of the Learning objective that we want to be part of the retrieved curricula')
            @RequestParam(value = "name", required = false, defaultValue = '') String name,
            @ApiParam(value = 'If this parameter is set to true it returns all the Learning Objects, if false then returns a page based in the from and size parameters')
            @RequestParam(value = "all", required = true) boolean all,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.findByLearningObjective(name, from, size, all, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @ExceptionHandler(ApiException.class)
    public @ResponseBody ErrorResponse handleApiError(HttpServletRequest req, HttpServletResponse response, ApiException apiException, Locale locale) {

        ErrorResponse er = new ErrorResponse()
        String [] error = apiException.cause.message.split('\\|')
        Logger logger = LoggerFactory.getLogger("org.commonlibrary.cllo.controllers.LinkedCurriculaController")

        er.code = error[0]
        er.message = error[1]
        response.setStatus(Integer.parseInt(error[2]))

        if(null == apiException.cause.cause){
            logger.warn(er.code + ': ' + er.message)
        }
        else{
            String errId = Hashing.sha256().newHasher().putString(UUID.randomUUID().toString(), Charsets.UTF_8).hash().toString()
            String[] args = [ errId ]
            String m = messageSource.getMessage("error.user_message", args, locale);
            er.description = m
            logger.error(er.code + ': ' + er.message + ' : '+ errId + ' : '+ apiException.cause.cause.stackTrace)
        }
        return er
    }

}
