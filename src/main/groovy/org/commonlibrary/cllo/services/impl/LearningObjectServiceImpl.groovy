package org.commonlibrary.cllo.services.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.tika.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/3/14
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
class LearningObjectServiceImpl implements org.commonlibrary.cllo.services.LearningObjectService {

    @Autowired
    private org.commonlibrary.cllo.repositories.LearningObjectRepository learningObjectRepository

    @Autowired
    private org.commonlibrary.cllo.repositories.LearningObjectiveRepository learningObjectiveRepository

    @Autowired
    private org.commonlibrary.cllo.repositories.ContentRepository contentRepository

    @Autowired
    private org.commonlibrary.cllo.dao.ContentDAO contentDAO

    @Autowired
    private org.commonlibrary.cllo.services.QueueIndexService queueIndexService

    @Autowired
    private MessageSource messageSource

    public  org.commonlibrary.cllo.model.LearningObject findById(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(id)
            if(null == lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    List<org.commonlibrary.cllo.model.LearningObject> findByLearningObjective(String name, int from, int size, boolean all, Locale locale) {
        try {
            List<org.commonlibrary.cllo.model.LearningObjective> lois = learningObjectiveRepository.findByName(name)
            if(!lois){
                String[] args = [ name ]
                String m = messageSource.getMessage("loi.m5", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            List<org.commonlibrary.cllo.model.LearningObject> los = learningObjectRepository.findAll()
            List<org.commonlibrary.cllo.model.LearningObject> res = []

            def loiId
            lois.collect() {
                loiId = it.getId()
                // If the learning object matches with one of the learning objectives,
                // it will be filtered out from 'los' to avoid duplicates.
                los = los.findAll() { def lo ->
                    return matchLOs(lo, loiId) ? {res.add(lo); false}() : true
                }
            }

            if(all){
                return res
            }
            else{
                List<org.commonlibrary.cllo.model.LearningObject> pag = []

                int to = from + size

                if(from <= res.size()){
                    if(to > res.size()){
                        to = res.size()
                    }
                    pag = res.subList(from, to)
                }
                return pag
            }

        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("loi.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public Page<org.commonlibrary.cllo.model.LearningObject> findAll(int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            int page = from/size
            if(page < 0){
                page = 0
            }
            int amount = size

            if (all) {
                amount = learningObjectRepository.count()
                Page<org.commonlibrary.cllo.model.LearningObject> los = learningObjectRepository.findAll(true, new PageRequest(page, amount, new Sort(Sort.Direction.ASC, "title")))
                return los
            } else {

                if(amount <= 0 || page > Math.ceil((double)(learningObjectRepository.count()/amount))){
                    String[] args = [ ]
                    String m = messageSource.getMessage("lo.m3", args, locale)
                    throw new org.commonlibrary.cllo.util.CoreException(m, null)
                }

                Page<org.commonlibrary.cllo.model.LearningObject> los = learningObjectRepository.findAll(true, new PageRequest(page, amount, new Sort(Sort.Direction.DESC, "modificationDate")))
                return los
            }
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.LearningObject insert(String learningObjectJSON, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObject lo = new org.commonlibrary.cllo.model.LearningObject()
            ObjectMapper mapper = new ObjectMapper()
            org.commonlibrary.cllo.model.LearningObject loC = mapper.readValue(learningObjectJSON, org.commonlibrary.cllo.model.LearningObject.class)
            lo.CopyValues(loC)
            lo.creationDate = new Date()
            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            queueIndexService.addLearningObject(lo, locale)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }


    public org.commonlibrary.cllo.model.LearningObject update(String learningObjectJSON, String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException {
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(id)
            def oldExternalUrl = lo.externalUrl

            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            ObjectMapper mapper = new ObjectMapper()
            org.commonlibrary.cllo.model.LearningObject loC = mapper.readValue(learningObjectJSON, org.commonlibrary.cllo.model.LearningObject.class);
            lo.CopyValues(loC)

            if(lo.getContents()){
                org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(lo.getContents().getId())
                lo.setContents(contents)
            }

            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            lo = learningObjectRepository.findById(id)

            def externalUrlChanged = oldExternalUrl != lo.externalUrl
            queueIndexService.updateLearningObject(lo, externalUrlChanged, locale)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.LearningObject delete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(id)

            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            if (lo.getContents()) {
                org.commonlibrary.cllo.model.Contents c = contentRepository.findById(lo.getContents().getId())

                if (lo.getFormat().name().toLowerCase() != 'url') {
                    String elId = "${lo.getId()}/${lo.getId()}_file"
                    contentDAO.deleteByReferenceId(elId)
                }

                contentRepository.delete(c)
            }

            lo = learningObjectRepository.findById(lo.getId())
            learningObjectRepository.delete(lo)

            queueIndexService.removeLearningObject(lo.getId(), locale)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.LearningObject softDelete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(id)

            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            if (lo.getContents()) {
                org.commonlibrary.cllo.model.Contents c = contentRepository.findById(lo.getContents().getId())
                contentRepository.delete(c)
            }

            lo = learningObjectRepository.findById(lo.getId())
            learningObjectRepository.delete(lo)

            queueIndexService.removeLearningObject(lo.getId(), locale)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.Contents findContentsById(String idLO, String idContents, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            return contents
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.Contents findAllContents(String idLO, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            if(!lo.getContents()){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m5", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            return lo.getContents()
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.Contents updateContents(String contentsJSON, String idLO, String idContents, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            ObjectMapper mapper = new ObjectMapper()
            org.commonlibrary.cllo.model.Contents contentsC = mapper.readValue(contentsJSON, org.commonlibrary.cllo.model.Contents.class)
            contents.CopyValues(contentsC)
            contents.modificationDate = new Date()

            contentRepository.save(contents)

            lo.setContents(contents)
            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            lo = learningObjectRepository.findById(idLO)

            queueIndexService.updateLearningObject(lo, false, locale)

            return contents
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.Contents insertContents(String contentsJSON, String idLO, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = new org.commonlibrary.cllo.model.Contents()
            ObjectMapper mapper = new ObjectMapper()
            org.commonlibrary.cllo.model.Contents contentsC = mapper.readValue(contentsJSON, org.commonlibrary.cllo.model.Contents.class)
            contents.CopyValues(contentsC)
            contents.creationDate = new Date()
            contents.modificationDate = new Date()

            contentRepository.save(contents)

            lo.setContents(contents)
            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            lo = learningObjectRepository.findById(idLO)

            queueIndexService.updateLearningObject(lo, false, locale)

            return contents
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.model.Contents deleteContents(String idLO, String idContents, Locale locale) throws org.commonlibrary.cllo.util.CoreException {
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            contentRepository.delete(contents)

            lo.setContents(null)
            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            lo = learningObjectRepository.findById(idLO)

            queueIndexService.updateLearningObject(lo, false, locale)

            return contents
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public HttpEntity<byte[]> findFileById(String idLO, String idContents, String fileId, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String elId = refPath+fileId

            String contentType = contentDAO.getMimeType(elId)

            InputStream is = contentDAO.getInputStreamByContentReferenceId(elId)

            if(!is){
                String[] args = [ fileId ]
                String m = messageSource.getMessage("lo.m6", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
            byte[] byteArray = IOUtils.toByteArray(is)
            is.close()
            String pT = contentType.split("/").getAt(0)
            String sT = contentType.split("/").getAt(1)
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType(pT, sT))
            header.setContentLength(byteArray.length)
            return new HttpEntity<byte[]>(byteArray, header)
        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }

    }

    public String findInputStreamById(String idLO, String idContents, String fileId, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String elId = refPath+fileId

            InputStream is = contentDAO.getInputStreamByContentReferenceId(elId)

            if(!is){
                String[] args = [ fileId ]
                String m = messageSource.getMessage("lo.m6", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String decoded = new String(IOUtils.toByteArray(is),'UTF-8')
            is.close()

            return decoded

            // The following line was removed because it was causing problems when the content is displayed in the cl-ui.
            // It may be needed when using Canvas.
            //return decoded.replaceAll("(\r\n|\n\r|\r|\n)", "<br>")

        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }

    }

    public List<org.commonlibrary.cllo.util.VersionResponse> findVersions(String idLO, String idContents, String fileId, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String elId = refPath+fileId
            InputStream is = contentDAO.getInputStreamByContentReferenceId(elId)

            if(!is){
                String[] args = [ fileId ]
                String m = messageSource.getMessage("lo.m6", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
            is.close()

            List<org.commonlibrary.cllo.util.VersionResponse> versionResponses = []

            List<String> versions = contentDAO.getAllVersions(elId)
            for(v in versions){
                List<String> parts = v.split(',')
                org.commonlibrary.cllo.util.VersionResponse vr = new org.commonlibrary.cllo.util.VersionResponse()
                vr.setVersion(parts.get(0))
                vr.setDate(parts.get(1))
                versionResponses.add(vr)
            }

            return versionResponses
        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public HttpEntity<byte[]> findVersionById(String idLO, String idContents,  String fileId, Long version, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo) {
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String elId = refPath+fileId

            String contentType = contentDAO.getMimeType(elId, version)

            InputStream is = contentDAO.getInputStreamByContentReferenceId(elId, version)

            if(!is){
                String[] args = [ fileId ]
                String m = messageSource.getMessage("lo.m6", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            byte[] byteArray = IOUtils.toByteArray(is)
            is.close()

            String pT = contentType.split("/").getAt(0)
            String sT = contentType.split("/").getAt(1)

            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType(pT, sT))
            header.setContentLength(byteArray.length)

            return new HttpEntity<byte[]>(byteArray, header)
        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.util.FileResponse rollBackToVersionById(String idLO, String idContents, Long version, String baseUrl, String refPath, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String [] fileUrlSplit =  contents.getUrl().split('/')
            String prevFileId = fileUrlSplit.getAt(fileUrlSplit.size()-1)
            String [] idRef = prevFileId.split('\\?')

            String [] ref = idRef.getAt(1).split('=')

            String elId = ref.getAt(1) + "/" + idRef.getAt(0)

            InputStream is = contentDAO.getInputStreamByContentReferenceId(elId)

            if(!is){
                String[] args = [ elId ]
                String m = messageSource.getMessage("lo.m6", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
            is.close()

            if(1 > version || version > contentDAO.getNumberOfVersions(elId)){
                String[] args = [ version ]
                String m = messageSource.getMessage("lo.m7", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String fileId = contentDAO.rollbackToVersion(elId,version)

            String filename = fileId.split("/")[1]

            org.commonlibrary.cllo.util.FileResponse fr = new org.commonlibrary.cllo.util.FileResponse()
            fr.url = "/learningObjects/" + idLO + "/contents/" + idContents + "/file/" + filename + "?refPath=" + refPath
            fr.mimeType = contentDAO.getMimeType(elId)
            fr.md5 = contentDAO.getMD5(elId)

            contents.setUrl(fr.getUrl())
            contents.setMimeType(fr.getMimeType())
            contents.setMd5(fr.getMd5())
            contents.modificationDate = new Date()

            contentRepository.save(contents)

            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            lo = learningObjectRepository.findById(idLO)

            queueIndexService.updateLearningObject(lo, true, locale)

            return fr
        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.util.FileResponse insertFile(
                                    String idLO,
                                    String idContents,
                                    String filename,
                                    String primaryType,
                                    String secondaryType,
                                    MultipartFile multipartFile,
                                    String refPath,
                                    Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {

            if (!validFilename(filename)) {
                String[] args = []
                String m = messageSource.getMessage("lo.m9", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            if (!multipartFile.isEmpty()) {

                org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

                if(!lo){
                    String[] args = [ idLO ]
                    String m = messageSource.getMessage("lo.m1", args, locale)
                    throw new org.commonlibrary.cllo.util.CoreException(m, null)
                }

                org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

                if(!contents){
                    String[] args = [ idContents, idLO ]
                    String m = messageSource.getMessage("lo.m4", args, locale)
                    throw new org.commonlibrary.cllo.util.CoreException(m, null)
                }

                String elId = refPath + filename

                String fileId = contentDAO.storeContent(multipartFile.getInputStream(),elId,primaryType,secondaryType)

                org.commonlibrary.cllo.util.FileResponse fr = new org.commonlibrary.cllo.util.FileResponse()
                fr.url = "/learningObjects/" + idLO + "/contents/" + idContents + "/file/" + filename + "?refPath=" + refPath
                fr.mimeType = "${primaryType}/${secondaryType}"
                fr.md5 = contentDAO.getMD5(fileId)

                contents.setUrl(fr.getUrl())
                contents.setMimeType(fr.getMimeType())
                contents.setMd5(fr.getMd5())
                contents.modificationDate = new Date()

                contentRepository.save(contents)

                lo.modificationDate = new Date()

                learningObjectRepository.save(lo)

                lo = learningObjectRepository.findById(idLO)

                queueIndexService.updateLearningObject(lo, true, locale)

                return fr
            } else {
                String[] args = [ ]
                String m = messageSource.getMessage("lo.m8", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public org.commonlibrary.cllo.util.FileResponse insertFile(
                                    String idLO,
                                    String idContents,
                                    String content,
                                    String filename,
                                    String primaryType,
                                    String secondaryType,
                                    String refPath ,
                                    Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {

            if (!validFilename(filename)) {
                String[] args = []
                String m = messageSource.getMessage("lo.m9", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(idLO)

            if(!lo){
                String[] args = [ idLO ]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(idContents)

            if(!contents){
                String[] args = [ idContents, idLO ]
                String m = messageSource.getMessage("lo.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            String elId = refPath + filename

            InputStream is = new ByteArrayInputStream(content.getBytes());
            String fileId = contentDAO.storeContent(is,elId,primaryType,secondaryType)

            org.commonlibrary.cllo.util.FileResponse fr = new org.commonlibrary.cllo.util.FileResponse()
            fr.url =  "/learningObjects/" + idLO + "/contents/" + idContents + "/file/" + filename + "?refPath=" + refPath
            fr.mimeType = "${primaryType}/${secondaryType}"
            fr.md5 = contentDAO.getMD5(elId)

            contents.setUrl(fr.getUrl())
            contents.setMimeType(fr.getMimeType())
            contents.setMd5(fr.getMd5())
            contents.modificationDate = new Date()

            contentRepository.save(contents)

            lo.modificationDate = new Date()

            learningObjectRepository.save(lo)

            lo = learningObjectRepository.findById(idLO)

            queueIndexService.updateLearningObject(lo, true, locale)

            return fr
        }
        catch (Exception e){
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    @Override
    org.commonlibrary.cllo.util.FileResponse storeContent(String loId,
                                                          String filename,
                                                          String mimeType,
                                                          String md5,
                                                          InputStream content,
                                                          String baseUrl,
                                                          Locale locale) {
        try {

            if (!validFilename(filename)) {
                String[] args = []
                String m = messageSource.getMessage("lo.m9", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.LearningObject lo = learningObjectRepository.findById(loId)

            if (!lo) {
                String[] args = [loId]
                String m = messageSource.getMessage("lo.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            org.commonlibrary.cllo.model.Contents contents = contentRepository.findById(lo.contents?.id)
            if (!contents) {
                contents = new org.commonlibrary.cllo.model.Contents()
                contents.mimeType = mimeType
                contents.md5 = md5
                contents.creationDate = new Date()
                contents.modificationDate = new Date()
                contentRepository.save(contents)

                lo.setContents(contents)
                lo.modificationDate = new Date()
                learningObjectRepository.save(lo)
            }
            def types = mimeType.tokenize('/')

            String refPath = "${loId}/"
            String elId = refPath + filename

            String fileId = contentDAO.storeContent(content, elId, types[0], types[1])

            org.commonlibrary.cllo.util.FileResponse fr = new org.commonlibrary.cllo.util.FileResponse()
            fr.url =  "/learningObjects/" + loId + "/contents/" + contents.id + "/file/" + filename + "?refPath=" + refPath
            fr.mimeType = mimeType
            fr.md5 = contentDAO.getMD5(fileId)

            contents.setUrl(fr.getUrl())
            contents.setMimeType(fr.getMimeType())
            contents.setMd5(fr.getMd5())
            contents.modificationDate = new Date()

            contentRepository.save(contents)

            lo.modificationDate = new Date()
            learningObjectRepository.save(lo)
            lo = learningObjectRepository.findById(loId)

            queueIndexService.updateLearningObject(lo, true, locale)

            return fr
        }
        catch (Exception e){
                String[] args = [  ]
                String m = e.getMessage() ?: messageSource.getMessage("lo.m2", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    private boolean matchLOs(org.commonlibrary.cllo.model.LearningObject lo, String loiId){
        for(loi in lo.getLearningObjectiveList()){
            if(loiId == loi.getId()){
                return true
            }
        }
        return false
    }

    private boolean validFilename(String filename) { filename.matches("([^\\\\/?%*:|\"<>])+") }

}
