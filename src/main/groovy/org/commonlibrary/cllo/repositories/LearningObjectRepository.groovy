package org.commonlibrary.cllo.repositories
/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/11/13
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
public interface LearningObjectRepository{

    public org.commonlibrary.cllo.model.LearningObject findById(String id)

    public org.commonlibrary.cllo.model.LearningObject findByName(String name)

}
