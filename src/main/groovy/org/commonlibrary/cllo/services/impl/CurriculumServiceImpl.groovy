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


 package org.commonlibrary.cllo.services.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.commonlibrary.cllo.model.Curriculum
import org.commonlibrary.cllo.model.Folder
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.model.LearningObjective
import org.commonlibrary.cllo.repositories.CurriculumRepository
import org.commonlibrary.cllo.repositories.FolderRepository
import org.commonlibrary.cllo.repositories.LearningObjectRepository
import org.commonlibrary.cllo.repositories.LearningObjectiveRepository
import org.commonlibrary.cllo.services.CurriculumService
import org.commonlibrary.cllo.util.CoreException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/7/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
@Service
@Transactional
class CurriculumServiceImpl implements CurriculumService {

    @Autowired
    private CurriculumRepository curriculumRepository

    @Autowired
    private LearningObjectRepository learningObjectRepository

    @Autowired
    private FolderRepository folderRepository

    @Autowired
    private LearningObjectiveRepository learningObjectiveRepository

    @Autowired
    private MessageSource messageSource

    public Curriculum findById(String id, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(id)

            if(!curriculum){
                String[] args = [ id ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            return curriculum
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Page<Curriculum> findAll(int from, int size, boolean all, Locale locale) throws CoreException{
        try {
            int page = from/size
            if(page < 0){
                page = 0
            }
            int amount = size

            if (all) {
                amount = curriculumRepository.count()
                return curriculumRepository.findAll(true, new PageRequest(page, amount == 0 ? 1 : amount, new Sort(Sort.Direction.DESC, "title")))
            } else {
                if(amount <= 0 || page > Math.ceil((double)(curriculumRepository.count()/amount))){
                    String[] args = [ ]
                    String m = messageSource.getMessage("curriculum.m3", args, locale)
                    throw new CoreException(m, null)
                }

                Page<Curriculum> curriculumList = curriculumRepository.findAll(true, new PageRequest(page, amount, new Sort(Sort.Direction.DESC, "title")))
                return curriculumList
            }
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    /**
     * This method is currently not working due to changes made in the learningObjectiveRepository.findByName operation. This will be fixed in cl-curricula.
     * See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
     */
    List<Curriculum> findByLearningObjective(String name, int from, int size, boolean all, Locale locale) {
        /*
        try {
            LearningObjective lo = learningObjectiveRepository.findByName(name)
            if(!lo){
                String[] args = [ name ]
                String m = messageSource.getMessage("loi.m5", args, locale)
                throw new CoreException(m, null)
            }

            String id = lo.getId()
            List<LearningObject> learningObjectList = findLearningObjectsUsages(id, locale)

            List<Curriculum> curriculumList = curriculumRepository.findAll()

            List<Curriculum> matchList = []

            for(c in curriculumList){
                Folder f = c.getRoot()
                if(f && matchCurricula(f,learningObjectList)){
                    matchList.add(c)
                }
            }

            if(all) {
                return matchList
            }else {
                List<LearningObject> pag = []

                int to = from + size

                if(from <= matchList.size()){
                    if(to > matchList.size()){
                        to = matchList.size()
                    }
                    pag = matchList.subList(from, to)
                }

                return pag
            }
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = messageSource.getMessage("loi.m2", args, locale)
            throw new CoreException(m, e)
        }*/
        return []
    }

    public Curriculum insert(String curriculumJSON, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = new Curriculum()
            ObjectMapper mapper = new ObjectMapper()
            Curriculum curriculumC = mapper.readValue(curriculumJSON, Curriculum.class)
            curriculum.CopyValues(curriculumC)

            if(!curriculum.getRoot()){
                String[] args = []
                String m = messageSource.getMessage("curriculum.m4", args, locale)
                throw new CoreException(m, null)
            }

            Folder folder = new Folder()

            List<String> fPath = []

            if(checkCycles(curriculum.getRoot(),fPath)){
                String[] args = []
                String m = messageSource.getMessage("curriculum.m7", args, locale)
                throw new CoreException(m, null)
            }

            folder.CopyValues(curriculum.getRoot())

            List<Folder> fl = saveFolders(folder)
            folder.setFolderList(fl)

            List<LearningObject> lol = saveLearningObjects(folder)
            folder.setLearningObjectList(lol)

            folder.creationDate = new Date()
            folder.modificationDate = new Date()

            folderRepository.save(folder)

            folder = folderRepository.findById(folder.getId())

            curriculum.setRoot(folder)

            curriculum.creationDate = new Date()
            curriculum.modificationDate = new Date()
            curriculumRepository.save(curriculum)

            return curriculum
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Curriculum update(String curriculumJSON, String id, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(id)

            if(!curriculum){
                String[] args = [ id ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            ObjectMapper mapper = new ObjectMapper()
            Curriculum curriculumC = mapper.readValue(curriculumJSON, Curriculum.class);
            curriculum.CopyValues(curriculumC)

            if(!curriculum.getRoot()){
                String[] args = [ id ]
                String m = messageSource.getMessage("curriculum.m4", args, locale)
                throw new CoreException(m, null)
            }

            Folder folder = folderRepository.findById(curriculum.root.getId())

            if(!folder){
                String[] args = [ curriculum.root.getId(), id ]
                String m = messageSource.getMessage("curriculum.m5", args, locale)
                throw new CoreException(m, null)
            }

            List<String> fPath = []
            if(checkCycles(curriculum.getRoot(),fPath)){
                String[] args = []
                String m = messageSource.getMessage("curriculum.m7", args, locale)
                throw new CoreException(m, null)
            }

            folder.CopyValues(curriculum.getRoot())
            folder.setFolderList(saveFolders(folder))
            folder.setLearningObjectList(saveLearningObjects(folder))
            folder.modificationDate = new Date()

            folderRepository.save(folder)

            folder = folderRepository.findById(folder.getId())
            curriculum.setRoot(folder)

            curriculum.modificationDate = new Date()

            curriculumRepository.save(curriculum)

            return curriculum
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Curriculum delete(String id, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(id)

            if(!curriculum){
                String[] args = [ id ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            if(curriculum.getRoot()){
                deleteFolders(curriculum.getRoot())
            }

            curriculumRepository.delete(curriculum)

            return curriculum
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Folder findFolderById(String idC, String idF, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(idC)

            if(!curriculum){
                String[] args = [ idC ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            Folder folder = folderRepository.findById(idF)

            if(!folder){
                String[] args = [ idF, idC ]
                String m = messageSource.getMessage("curriculum.m6", args, locale)
                throw new CoreException(m, null)
            }

            return folder
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public List<Folder> findFolders(String idC, String idF, Locale locale) throws CoreException{
        try {
            Folder folder = findFolderById(idC, idF, locale)
            return folder.getFolderList()
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public List<LearningObject> findLOs(String idC, String idF, Locale locale) throws CoreException{
        try {
            Folder folder = findFolderById(idC, idF, locale)
            return folder.getLearningObjectList()
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Folder updateFolder(String folderJSON, String idC, String idF, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(idC)

            if(!curriculum){
                String[] args = [ idC ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            ObjectMapper mapper = new ObjectMapper()
            Folder folderC = mapper.readValue(folderJSON, Folder.class)

            List<String> fPath = []
            if(checkCycles(folderC,fPath)){
                String[] args = []
                String m = messageSource.getMessage("curriculum.m7", args, locale)
                throw new CoreException(m, null)
            }

            Folder folder = folderRepository.findById(folderC.id)
            if (!folder) {
                throw new CoreException(messageSource.getMessage("curriculum.m6", (String[])[idF, idC], locale), null)
            }

            folder.CopyValues(folderC)
            folder.setFolderList(saveFolders(folder))
            folder.setLearningObjectList(saveLearningObjects(folder))
            folder.modificationDate = new Date()

            folderRepository.save(folder)

            return folder
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Folder insertFolder(String folderJSON, String idC, String idF, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(idC)

            if(!curriculum){
                String[] args = [ idC ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            Folder folder = new Folder()
            ObjectMapper mapper = new ObjectMapper()
            Folder folderC = mapper.readValue(folderJSON, Folder.class);

            List<String> fPath = []
            if(checkCycles(folderC,fPath)){
                String[] args = []
                String m = messageSource.getMessage("curriculum.m7", args, locale)
                throw new CoreException(m, null)
            }

            folder.CopyValues(folderC)
            folder.setFolderList(saveFolders(folder))
            folder.setLearningObjectList(saveLearningObjects(folder))
            folder.creationDate = new Date()
            folder.modificationDate = new Date()

            folderRepository.save(folder)


            Folder parent = folderRepository.findById(idF)

            List<Folder> folderList = parent.getFolderList()
            folderList.add(folder)
            parent.setFolderList(folderList)

            folderRepository.save(parent)

            return folder
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    public Folder deleteFolder(String idC, String idF, Locale locale) throws CoreException{
        try {
            Curriculum curriculum = curriculumRepository.findById(idC)

            if(!curriculum){
                String[] args = [ idC ]
                String m = messageSource.getMessage("curriculum.m1", args, locale)
                throw new CoreException(m, null)
            }

            Folder folder = folderRepository.findById(idF)

            if(!folder){
                String[] args = [ idF, idC ]
                String m = messageSource.getMessage("curriculum.m6", args, locale)
                throw new CoreException(m, null)
            }

            folderRepository.delete(folder)
            return folder
        }
        catch (Exception e) {
            String[] args = [ ]
            String m = e.getMessage() ?: messageSource.getMessage("curriculum.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    private List<LearningObject> saveLearningObjects(Folder folder) throws Exception{

        List<LearningObject> lol = []
        try {
            for (lo in folder.getLearningObjectList()) {
                LearningObject loN = learningObjectRepository.findById(lo.getId())
                lol.add(loN)
            }
            return lol
        }
        catch (Exception e) {
            throw e
        }
    }

    private List<Folder> saveFolders(Folder folder) throws Exception{

        List<Folder> fl = []
        try {
            Folder fT
            for (f in folder.getFolderList()) {
                if (!f.getId()) {
                    fT = new Folder()
                    fT.CopyValues(f)
                    fT.setFolderList(saveFolders(fT))
                    fT.setLearningObjectList(saveLearningObjects(fT))
                    fT.creationDate = new Date()
                    fT.modificationDate = new Date()
                    folderRepository.save(fT)
                } else {
                    fT = folderRepository.findById(f.getId())
                    fT.CopyValues(f)
                    fT.setFolderList(saveFolders(fT))
                    fT.setLearningObjectList(saveLearningObjects(fT))
                    fT.modificationDate = new Date()
                    folderRepository.save(fT)
                }
                fl.add(fT)
            }
            return fl
        }
        catch (Exception e) {
            throw e
        }
    }

    private void deleteFolders(Folder folder) throws Exception{
        try {
            Folder fol = folderRepository.findById(folder.getId())
            if(fol) {
                for (f in folder.getFolderList()) {
                    Folder fT
                    fT = folderRepository.findById(f.getId())
                    deleteFolders(fT)
                    folderRepository.delete(fT)
                }
                folderRepository.delete(fol)
            }
        }
        catch (Exception e) {
            throw e
        }
    }

    private boolean checkCycles(Folder f, List<String> fPath){
        if(f.getId()){
            fPath.add(f.getId())
        }
        boolean res = false
        for(fChild in f.getFolderList()){
            if(fPath.contains(fChild.getId())){
                return true
            }
            else{
                res = checkCycles(fChild,fPath)
                if(res){
                    return res
                }
            }
        }
        return res
    }

    List<LearningObject> findLearningObjectsUsages(String id, Locale locale) {
        try {
            LearningObjective lo = learningObjectiveRepository.findById(id)
            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("loi.m1", args, locale)
                throw new CoreException(m, null)
            }

            List<LearningObject> los = learningObjectRepository.findAll()
            List<LearningObject> res = []

            for(l in los){
                if(matchLOs(l, id)){
                    res.add(l)
                }
            }
            return res
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = messageSource.getMessage("loi.m2", args, locale)
            throw new CoreException(m, e)
        }
    }

    private boolean matchLOs(LearningObject lo, String loiId){
        for(loi in lo.getLearningObjectiveList()){
            if(loiId == loi.getId()){
                return true
            }
        }
        return false
    }

    private boolean matchCurricula(Folder folder, List<LearningObject> los) throws Exception{
        try {
            boolean res = false

            for(cLo in folder.getLearningObjectList()){
                for(l in los){
                    if(l.getId() == cLo.getId()){
                        return true
                    }
                }
            }
            for(f in folder.getFolderList()){
                res = matchCurricula(f,los)
                if(res){
                    return true
                }
            }
            return false
        }
        catch (Exception e) {
            throw e
        }
    }

}
