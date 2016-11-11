package org.commonlibrary.cllo.repositories
/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/11/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
public interface FolderRepository{

    public org.commonlibrary.cllo.model.Folder findById(String id)

    public org.commonlibrary.cllo.model.Folder findByName(String name)

}
