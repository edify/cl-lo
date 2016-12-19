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


 package org.commonlibrary.cllo.repositories.impl.mongo

import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

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
@Repository
@Profile('BE_Mongo')
public interface FolderMongoRepository extends org.commonlibrary.cllo.repositories.FolderRepository, MongoRepository<org.commonlibrary.cllo.model.Folder, String> {}
