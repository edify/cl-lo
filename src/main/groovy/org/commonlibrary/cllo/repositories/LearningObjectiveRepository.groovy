package org.commonlibrary.cllo.repositories
/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/6/13
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */
public interface LearningObjectiveRepository{

    public org.commonlibrary.cllo.model.LearningObjective findById(String id)

    public List<org.commonlibrary.cllo.model.LearningObjective> findByName(String name)

}
