package org.commonlibrary.cllo.services

import org.springframework.data.domain.Page

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/15/14
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
interface LearningObjectiveService {

    public  org.commonlibrary.cllo.model.LearningObjective findById(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public Page<org.commonlibrary.cllo.model.LearningObjective> findAll(int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObjective insert(String learningObjectJSON, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObjective update(String learningObjectJSON, String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.LearningObjective delete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

}
