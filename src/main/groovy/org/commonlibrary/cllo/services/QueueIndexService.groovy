package org.commonlibrary.cllo.services
/**
 * Created by diugalde on 22/09/16.
 */
interface QueueIndexService {

    def addLearningObject(org.commonlibrary.cllo.model.LearningObject lo, Locale locale)

    def removeLearningObject(String loId, Locale locale)

    def updateLearningObject(org.commonlibrary.cllo.model.LearningObject lo, Boolean updateFile, Locale locale)

}
