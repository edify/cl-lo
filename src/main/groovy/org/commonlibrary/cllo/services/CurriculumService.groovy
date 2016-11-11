package org.commonlibrary.cllo.services

import org.springframework.data.domain.Page

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/7/14
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
public interface CurriculumService {

    public org.commonlibrary.cllo.model.Curriculum findById(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public Page<org.commonlibrary.cllo.model.Curriculum> findAll(int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Curriculum insert(String curriculumJSON, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Curriculum update(String curriculumJSON, String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Curriculum delete(String id, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Folder findFolderById(String idC, String idF, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public List<org.commonlibrary.cllo.model.Folder> findFolders(String idC, String idF, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public List<org.commonlibrary.cllo.model.LearningObject> findLOs(String idC, String idF, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Folder updateFolder(String folderJSON, String idC, String idF, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Folder insertFolder(String folderJSON, String idC, String idF, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public org.commonlibrary.cllo.model.Folder deleteFolder(String idC, String idF, Locale locale) throws org.commonlibrary.cllo.util.CoreException

    public List<org.commonlibrary.cllo.model.Curriculum> findByLearningObjective(String name, int from, int size, boolean all, Locale locale) throws org.commonlibrary.cllo.util.CoreException

}
