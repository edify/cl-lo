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


 package org.commonlibrary.cllo.services

import org.commonlibrary.cllo.model.Curriculum
import org.commonlibrary.cllo.model.Folder
import org.commonlibrary.cllo.model.LearningObject
import org.commonlibrary.cllo.util.CoreException
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

    public Curriculum findById(String id, Locale locale) throws CoreException

    public Page<Curriculum> findAll(int from, int size, boolean all, Locale locale) throws CoreException

    public Curriculum insert(String curriculumJSON, Locale locale) throws CoreException

    public Curriculum update(String curriculumJSON, String id, Locale locale) throws CoreException

    public Curriculum delete(String id, Locale locale) throws CoreException

    public Folder findFolderById(String idC, String idF, Locale locale) throws CoreException

    public List<Folder> findFolders(String idC, String idF, Locale locale) throws CoreException

    public List<LearningObject> findLOs(String idC, String idF, Locale locale) throws CoreException

    public Folder updateFolder(String folderJSON, String idC, String idF, Locale locale) throws CoreException

    public Folder insertFolder(String folderJSON, String idC, String idF, Locale locale) throws CoreException

    public Folder deleteFolder(String idC, String idF, Locale locale) throws CoreException

    public List<Curriculum> findByLearningObjective(String name, int from, int size, boolean all, Locale locale) throws CoreException

}
