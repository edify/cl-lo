package org.commonlibrary.cllo.repositories
/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/11/13
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
public interface CurriculumRepository {

    public org.commonlibrary.cllo.model.Curriculum findById(String id)

    public org.commonlibrary.cllo.model.Curriculum findByName(String name)

}
