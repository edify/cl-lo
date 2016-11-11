package org.commonlibrary.cllo.controllers

import ch.qos.logback.classic.Logger
import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.mangofactory.swagger.annotations.ApiIgnore
import com.wordnik.swagger.annotations.Api
import com.wordnik.swagger.annotations.ApiOperation
import com.wordnik.swagger.annotations.ApiParam
import groovy.json.JsonSlurper
import org.apache.commons.codec.binary.Base64
import org.commonlibrary.cllo.model.Contents
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.services.LearningObjectService
import org.commonlibrary.cllo.support.ApiException
import org.commonlibrary.cllo.support.ErrorResponse
import org.commonlibrary.cllo.util.CoreException
import org.commonlibrary.cllo.util.FileResponse
import org.commonlibrary.cllo.util.VersionResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.http.HttpEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */

@RestController
@RequestMapping('${api.base.url}/learningObjects')
@Api(value="learning objects", description="Operations about Learning Objects")
class LearningObjectController {

    @Autowired
    MessageSource messageSource

    @Autowired
    HttpServletRequest request

    @Value('${api.base.url}')
    private String apiBaseUrl

    @Autowired
    LearningObjectService learningObjectService

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ApiOperation(value = 'Find learning object by ID', notes = 'Returns a specific learning object by id')
    public @ResponseBody LearningObject findById(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.findById(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = 'Find all learning objects in a page', notes = 'Returns a page of learning objects based on the parameters or all learning objects when "all" parameter is set to true')
    public @ResponseBody Page<LearningObject> findAll(
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
            return learningObjectService.findAll(from, size, all, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = 'Insert learning objective', notes = "Creates a new learning objective based on the JSON representation received")
    public @ResponseBody LearningObject insert(
            @ApiParam(value = 'JSON Representation of the Learning Object to save')
            @RequestBody String learningObjectJSON,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.insert(learningObjectJSON, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{id}', method = RequestMethod.PUT)
    @ApiOperation(value = 'Update learning object by ID', notes = 'Updates a specific learning object by its id and based on the JSON representation received')
    public @ResponseBody LearningObject update(
            @ApiParam(value = 'JSON Representation of the Curriculum to save')
            @RequestBody String learningObjectJSON,
            @ApiParam(value = 'The id of the Learning Object that we want to update')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.update(learningObjectJSON, id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @RequestMapping(value = '{id}', method = RequestMethod.DELETE)
    @ApiOperation(value = 'Hard delete learning object', notes = 'Deletes a specific learning object by its ID including the associated content')
    public @ResponseBody LearningObject delete(
            @ApiParam(value = 'The id of the Learning Object that we want to delete')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.delete(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }


    @RequestMapping(value = 'delete/soft/{id}', method = RequestMethod.DELETE)
    @ApiOperation(value = 'Soft delete learning object', notes = 'Deletes a specific learning object by its ID but keeping the associated content')
    public @ResponseBody LearningObject softDelete(
            @ApiParam(value = 'The id of the Learning Object that we want to delete')
            @PathVariable String id,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.softDelete(id, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}', method = RequestMethod.GET)
    @ApiOperation(value = 'Find learning object\'s content', notes = 'Returns the content associated whit a specific learning object')
    public @ResponseBody Contents findContentsById(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return  learningObjectService.findContentsById(idLO, idContents, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents', method = RequestMethod.GET)
    @ApiOperation(value = 'Find learning object\'s contents', notes = 'Returns the contents associated whit a specific learning object')
    public @ResponseBody Contents findAllContents(
            @ApiParam(value = 'The id of the Learning Object whose Content we\'re looking for')
            @PathVariable String idLO,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.findAllContents(idLO, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value='{idLO}/contents', method = RequestMethod.POST, consumes = 'application/json')
    @ApiOperation(value = 'Insert learning object\'s content by ID', notes = 'Creates the content for a specific learning object by its id and based on the JSON representation received')
    public @ResponseBody Contents insertContents(
            @ApiParam(value = 'JSON Representation of the Content to save')
            @RequestBody String contentsJSON,
            @ApiParam(value = 'The id of the Learning Object that will have the Content')
            @PathVariable String idLO,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.insertContents(contentsJSON, idLO, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}', method = RequestMethod.PUT, consumes = 'application/json')
    @ApiOperation(value = 'Update learning object\'s content by ID', notes = 'Updates the content of a specific learning object by its id and based on the JSON representation received')
    public @ResponseBody Contents updateContents(
            @ApiParam(value = 'JSON Representation of the Content to update')
            @RequestBody String contentsJSON,
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we want to update')
            @PathVariable String idContents,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.updateContents(contentsJSON, idLO, idContents, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}', method = RequestMethod.DELETE)
    @ApiOperation(value = 'Delete learning object\'s content by ID', notes = 'Deletes the content of a specific learning object by id')
    public @ResponseBody Contents deleteContents(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we want to delete')
            @PathVariable String idContents,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.deleteContents(idLO, idContents, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file/{fileId:.+}', method = RequestMethod.GET)
    @ApiOperation(value = 'Find file by id', notes = 'Returns a specific file')
    public HttpEntity<byte[]> findFileById(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'The id of the File that we\'re looking for')
            @PathVariable String fileId,
            @ApiParam(value = 'Path of the file when the content has attachments (usually is "learningObjectId/")')
            @RequestParam(value = "refPath", required = true) String refPath,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.findFileById(idLO, idContents, fileId, refPath, locale)
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file/{fileId:.+}/inputStream', method = RequestMethod.GET)
    @ApiOperation(value = 'Edit find file by ID', notes = 'Returns the InputStream representing a specific file')
    public String findInputStreamById(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'The id of the File that we\'re looking for')
            @PathVariable String fileId,
            @ApiParam(value = 'Path of the file when the content has attachments')
            @RequestParam(value = "refPath", required = true) String refPath,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.findInputStreamById(idLO, idContents, fileId, refPath, locale)
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file/{fileId:.+}/base64', method = RequestMethod.GET)
    @ApiOperation(value = 'Edit find file by ID', notes = 'Returns the InputStream representing a specific file')
    public Map<String, String> findFileBase64ById(
        @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
        @PathVariable String idLO,
        @ApiParam(value = 'The id of the Content that we\'re looking for')
        @PathVariable String idContents,
        @ApiParam(value = 'The id of the File that we\'re looking for')
        @PathVariable String fileId,
        @ApiParam(value = 'Path of the file when the content has attachments')
        @RequestParam(value = "refPath", required = true) String refPath,
        @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            def is = learningObjectService.findInputStreamById(idLO, idContents, fileId, refPath, locale)
            def base64 = new String(Base64.encodeBase64(is.getBytes()))
            return ['base64':base64]
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file/{fileId:.+}/versions', method = RequestMethod.GET)
    @ApiOperation(value = 'Find versions by ID', notes = 'returns a list of versions of a specific file')
    public List<VersionResponse> findVersions(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'The id of the File whose versions we\'re looking for')
            @PathVariable String fileId,
            @ApiParam(value = 'Path of the file when the content has attachments')
            @RequestParam(value = "refPath", required = true) String refPath,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            return learningObjectService.findVersions(idLO, idContents, fileId, refPath, locale)
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file/{fileId:.+}/versions/{version}', method = RequestMethod.GET)
    @ApiOperation(value = 'Find Version by Id', notes = 'Returns a specific version of the file based on the id and the number of version received')
    public HttpEntity<byte[]> findVersionById(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'The id of the File that we\'re looking for')
            @PathVariable String fileId,
            @ApiParam(value = 'Specific version we want to retrieve')
            @PathVariable Long version,
            @ApiParam(value = 'Path of the file when the content has attachments (usually is "learningObjectId/")')
            @RequestParam(value = "refPath", required = true) String refPath,
            @ApiIgnore
            Locale locale) throws ApiException{
        try {
            return learningObjectService.findVersionById(idLO, idContents, fileId, version, refPath, locale)
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file/version/rollback/{version}', method = RequestMethod.GET)
    @ApiOperation(value = 'Rollback to version', notes = 'Performs a rollback to a specific version of the file based on the id an the number of version received')
    public FileResponse rollBackToVersionById(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'Specific version we want to rollback to')
            @PathVariable Long version,
            @ApiParam(value = 'Path of the file when the content has attachments')
            @RequestParam(value = "refPath", required = true) String refPath,
            @ApiIgnore
            Locale locale) throws ApiException{
        try {
            return learningObjectService.rollBackToVersionById(idLO, idContents, version, getBaseURL()+apiBaseUrl, refPath, locale)
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file', method = RequestMethod.POST, consumes = "multipart/form-data")
    @ApiOperation(value = 'Insert file', notes = 'Creates a file from the multipart received')
    public FileResponse insertFile(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'Name of the new file of the Content')
            @RequestParam(value = "filename", required = true) String filename,
            @ApiParam(value = 'Primary type of the new file of the Content')
            @RequestParam(value = "primaryType", required = true) String primaryType,
            @ApiParam(value = 'Secondary type of the new file of the Content')
            @RequestParam(value = "secondaryType", required = true) String secondaryType,
            @ApiParam(value = 'Multipart file data')
            @RequestParam(value = "content", required = true) MultipartFile multipartFile,
            @ApiParam(value = 'Path of the file when the content has attachments')
            @RequestParam(value = "refPath", required = true) String refPath,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            if (!multipartFile.isEmpty()) {
                return learningObjectService.insertFile(idLO, idContents, filename, primaryType, secondaryType, multipartFile, refPath, locale)
            } else {
                return null
            }
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value = '{idLO}/contents/{idContents}/file', method = RequestMethod.POST, consumes = 'application/json')
    @ApiOperation(value = 'Insert file', notes = 'Creates a file from the content received as parameter')
    public FileResponse insertFile(
            @ApiParam(value = 'The id of the Learning Object that we\'re looking for')
            @PathVariable String idLO,
            @ApiParam(value = 'The id of the Content that we\'re looking for')
            @PathVariable String idContents,
            @ApiParam(value = 'JSON Representation of the file to save {"content": "", "filename": "", "primaryType": "", "secondaryType": "", "refPath":""}')
            @RequestBody String fileInfoJson,
            @ApiIgnore
            Locale locale) throws ApiException
    {
        try {
            def fileInfo = new JsonSlurper().parseText(fileInfoJson)
            return learningObjectService.insertFile(idLO, idContents, fileInfo.content, fileInfo.filename,
                                                    fileInfo.primaryType, fileInfo.secondaryType, fileInfo.refPath, locale)
        }
        catch (CoreException e){
            throw new ApiException('Api Exception', e)
        }
    }

    @RequestMapping(value='{loId}/file', method = RequestMethod.POST, consumes = 'application/json')
    @ApiOperation(value = 'POST learning object\'s contents by ID', notes = 'Creates the file content for a specific learning object by its id and based on the JSON representation received')
    public @ResponseBody FileResponse storeContent (
        @ApiParam(value = 'JSON Representation of the Content to save {"filename": "", "mimeType": "", "md5": "", "base64Content": ""}')
        @RequestBody String contentJson,
        @ApiParam(value = 'The id of the Learning Object that will have the Content')
        @PathVariable String loId,
        @ApiIgnore
            Locale locale) throws ApiException {

        try {
            def content = new JsonSlurper().parseText(contentJson)
            byte[] decoded = Base64.decodeBase64(content.base64Content);
            InputStream is = new ByteArrayInputStream(decoded);
            return learningObjectService.storeContent(loId, content.filename, content.mimeType, content.md5, is, getBaseURL() + apiBaseUrl, locale)
        }
        catch (CoreException e) {
            throw new ApiException('Api Exception', e)
        }
    }

    private String getBaseURL() {
        StringBuffer url = request.getRequestURL();
        return url.substring(0, url.length() - request.getRequestURI().length());
    }

    @ExceptionHandler(ApiException.class)
    public @ResponseBody ErrorResponse handleApiError(HttpServletRequest req, HttpServletResponse response, ApiException apiException, Locale locale) {

        ErrorResponse er = new ErrorResponse()
        String [] error = apiException.cause.message.split('\\|')
        Logger logger = LoggerFactory.getLogger("org.commonlibrary.cllo.controllers.LearningObjectController")

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

