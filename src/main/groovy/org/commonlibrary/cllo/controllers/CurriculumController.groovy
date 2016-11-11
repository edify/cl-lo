package org.commonlibrary.cllo.controllers

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.mangofactory.swagger.annotations.ApiIgnore
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import org.commonlibrary.cllo.model.Curriculum
import org.commonlibrary.cllo.model.Folder
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.services.CurriculumService
import org.commonlibrary.cllo.support.ApiException
import org.commonlibrary.cllo.support.ErrorResponse
import org.commonlibrary.cllo.util.CoreException
import org.slf4j.Logger
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

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
@Controller
@RequestMapping('${api.base.url}/curricula')
@Api(value = 'curricula', description = 'Operations about Curricula')
class CurriculumController {

    @Autowired
    private CurriculumService curriculumService

    @Autowired
    MessageSource messageSource

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ApiOperation(value = 'Find curriculum by ID', notes = 'Returns a specific curriculum by id')
    public @ResponseBody Curriculum findById(
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return  curriculumService.findById(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = 'Find all curricula in a page', notes = 'Returns a page of curricula based on the parameters')
    public @ResponseBody Page<Curriculum> findAll(
            @ApiParam(value = 'Initial value for the results')
            @RequestParam(value = "from", required = false, defaultValue = '0') int from,
            @ApiParam(value = 'Amount of results to retrieve starting from the initial value')
            @RequestParam(value = "size", required = false, defaultValue = '10') int size,
            @ApiParam(value = 'If this parameter is set to true it returns all the Learning Objects, if false then returns a page based in the from and size parameters')
            @RequestParam(value = "all", required = true) boolean all,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.findAll(from, size, all, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = 'Insert curriculum', notes = "Creates a new curriculum based on the JSON representation received")
    public @ResponseBody Curriculum insert(
            @ApiParam(value = 'JSON Representation of the Curriculum to save')
            @RequestBody String curriculumJSON,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.insert(curriculumJSON, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{id}', method = RequestMethod.PUT)
    @ApiOperation(value = 'Update curriculum by ID', notes = 'Updates a specific curriculum by its id and based on the JSON representation received')
    public @ResponseBody
    Curriculum update(
            @ApiParam(value = 'JSON Representation of the Curriculum to update')
            @RequestBody String curriculumJSON,
            @ApiParam(value = 'The id of the Curriculum that we want to update')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.update(curriculumJSON, id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{id}', method = RequestMethod.DELETE)
    @ApiOperation(value = 'Delete curriculum', notes = 'Deletes a specific curriculum by its ID')
    public @ResponseBody Curriculum delete(
            @ApiParam(value = 'The id of the Curriculum that we want to delete')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.delete(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idC}/folders/**/{idF}', method = RequestMethod.GET)
    @ApiOperation(value = 'Find curriculum\'s folder', notes = 'Returns a specific folder associated whit a specific curriculum. The wildcard "**" specifies the path in which this specific folder is contained')
    public @ResponseBody Folder findFolderById(
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String idC,
            @ApiParam(value = 'The id of the Folder that we\'re looking for (** represents the path of all the parent folders)')
            @PathVariable String idF,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.findFolderById(idC, idF, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @RequestMapping(value = '{idC}/folders/**/{idF}/folders', method = RequestMethod.GET)
    @ApiOperation(value = 'Find folders contained in a curriculum\'s folder', notes = 'Returns the list of all the folders contained in a specific folder associated whit a specific curriculum. The wildcard "**" specifies the path in which this specific folder is included')
    public @ResponseBody List<Folder> findFolders(
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String idC,
            @ApiParam(value = 'The id of the Folder from which we want the children folder list (** represents the path of all the parent folders)')
            @PathVariable String idF,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.findFolders(idC, idF, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idC}/folders/**/{idF}/learningObjects', method = RequestMethod.GET)
    @ApiOperation(value = 'Find learning objects in a specific curriculum\'s path', notes = 'Returns the list of all the folders contained in a specific folder associated whit a specific curriculum. The wildcard "**" specifies the path in which this specific folder is included')
    public @ResponseBody List<LearningObject> findLOs(
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String idC,
            @ApiParam(value = 'The id of the Folder from which we want the Learning Object list (** represents the path of all the parent folders)')
            @PathVariable String idF,
            @ApiIgnore
            Locale locale) throws ApiException{
        try {
            return curriculumService.findLOs(idC, idF, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idC}/folders/**/{idF}', method = RequestMethod.POST, consumes = 'application/json')
    @ApiOperation(value = 'Insert folder in a curriculum\'s path', notes = 'Creates a folder in a specific folder of a curriculum by its id and based on the JSON representation received')
    public @ResponseBody Folder insertFolder(
            @ApiParam(value = 'JSON Representation of the Folder that we want to create')
            @RequestBody String folderJSON,
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String idC,
            @ApiParam(value = 'The id of the Folder in which we want to to create the new one')
            @PathVariable String idF,
            @ApiIgnore
            Locale locale) throws ApiException{
        try {
            return curriculumService.insertFolder(folderJSON, idC, idF, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idC}/folders/**/{idF}', method = RequestMethod.PUT, consumes = 'application/json')
    @ApiOperation(value = 'Update folder in a curriculum\'s path', notes = 'Updates a folder of a specific curriculum by its id and based on the JSON representation received')
    public @ResponseBody Folder updateFolder(
            @ApiParam(value = 'JSON Representation of the Folder that we want to update')
            @RequestBody String folderJSON,
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String idC,
            @ApiParam(value = 'The id of the Folder that we want to update')
            @PathVariable String idF,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return curriculumService.updateFolder(folderJSON, idC, idF, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idC}/folders/**/{idF}', method = RequestMethod.DELETE)
    @ApiOperation(value = 'Delete folder from a specific curriculum\'s path', notes = 'Deletes a folder from a specific curriculum path')
    public @ResponseBody Folder deleteFolder(
            @ApiParam(value = 'The id of the Curriculum that we\'re looking for')
            @PathVariable String idC,
            @ApiParam(value = 'The id of the Folder that we want to delete')
            @PathVariable String idF,
            @ApiIgnore
            Locale locale) throws ApiException{
        try {
            return curriculumService.deleteFolder()
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @ExceptionHandler(ApiException.class)
    public @ResponseBody ErrorResponse handleApiError(HttpServletRequest req, HttpServletResponse response, ApiException apiException, Locale locale) {

        ErrorResponse er = new ErrorResponse()
        String [] error = apiException.cause.message.split('\\|')
        Logger logger = LoggerFactory.getLogger("org.commonlibrary.cllo.controllers.CurriculumController")

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
