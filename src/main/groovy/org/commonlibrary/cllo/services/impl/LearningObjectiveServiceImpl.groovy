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
 * Date: 10/15/14
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
class LearningObjectiveServiceImpl implements org.commonlibrary.cllo.services.LearningObjectiveService{

    @Autowired
    private org.commonlibrary.cllo.repositories.LearningObjectiveRepository learningObjectiveRepository

    @Autowired
    private MessageSource messageSource

    public  org.commonlibrary.cllo.model.LearningObjective findById(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObjective lo = learningObjectiveRepository.findById(id)
            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("loi.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("loi.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }

    public Page<org.commonlibrary.cllo.model.LearningObjective> findAll(int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            int page = from/size
            if(page < 0){
                page = 0
            }
            int amount = size

            if (all) {
                amount = learningObjectiveRepository.count()
                Page<org.commonlibrary.cllo.model.LearningObjective> los = learningObjectiveRepository.findAll(new PageRequest(page, amount, new Sort(Sort.Direction.ASC, "name")))
                return los
            } else {

                if(amount <= 0 || page > Math.ceil((double)(learningObjectiveRepository.count()/amount))){
                    String[] args = [ ]
                    String m = messageSource.getMessage("loi.m3", args, locale)
                    throw new org.commonlibrary.cllo.util.CoreException(m, null)
                }

                Page<org.commonlibrary.cllo.model.LearningObjective> los = learningObjectiveRepository.findAll(new PageRequest(page, amount, new Sort(Sort.Direction.DESC, "modificationDate")))
                return los
            }
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("loi.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }


    public org.commonlibrary.cllo.model.LearningObjective insert(String learningObjectiveJSON, Locale locale) throws org.commonlibrary.cllo.util.CoreException{
        try {
            org.commonlibrary.cllo.model.LearningObjective lo = new org.commonlibrary.cllo.model.LearningObjective()
            ObjectMapper mapper = new ObjectMapper()
            org.commonlibrary.cllo.model.LearningObjective loC = mapper.readValue(learningObjectiveJSON, org.commonlibrary.cllo.model.LearningObjective.class)
            List<String> loPath = []
            if(checkCycles(loC,loPath)){
                String[] args = []
                String m = messageSource.getMessage("loi.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }
            lo.CopyValues(loC)
            lo.creationDate = new Date()
            lo.modificationDate = new Date()

            learningObjectiveRepository.save(lo)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("loi.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }


    public org.commonlibrary.cllo.model.LearningObjective update(String learningObjectiveJSON, String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException {
        try {
            org.commonlibrary.cllo.model.LearningObjective lo = learningObjectiveRepository.findById(id)

            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("loi.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            ObjectMapper mapper = new ObjectMapper()
            org.commonlibrary.cllo.model.LearningObjective loC = mapper.readValue(learningObjectiveJSON, org.commonlibrary.cllo.model.LearningObjective.class);


            List<String> loPath = []
            if(checkCycles(loC,loPath)){
                String[] args = []
                String m = messageSource.getMessage("loi.m4", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            lo.CopyValues(loC)

            lo.modificationDate = new Date()

            learningObjectiveRepository.save(lo)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("loi.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }


    public org.commonlibrary.cllo.model.LearningObjective delete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException{

        try {
            org.commonlibrary.cllo.model.LearningObjective lo = learningObjectiveRepository.findById(id)

            if(!lo){
                String[] args = [ id ]
                String m = messageSource.getMessage("loi.m1", args, locale)
                throw new org.commonlibrary.cllo.util.CoreException(m, null)
            }

            learningObjectiveRepository.delete(lo)

            return lo
        }
        catch (Exception e) {
            String[] args = [  ]
            String m = e.getMessage() ?: messageSource.getMessage("loi.m2", args, locale)
            throw new org.commonlibrary.cllo.util.CoreException(m, e)
        }
    }


    private boolean checkCycles(org.commonlibrary.cllo.model.LearningObjective lo, List<String> loPath){
        loPath.add(lo.getId())
        boolean res = false
        for(loChild in lo.getLearningObjectiveList()){
            if(loPath.contains(loChild.getId())){
                return true
            }
            else{
                res = checkCycles(loChild, loPath)
                if(res){
                    return res
                }
            }
        }
        return res
    }
}
