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
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 6/13/14
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository
@Profile('BE_Mongo')
public interface LearningObjectMongoRepository extends org.commonlibrary.cllo.repositories.LearningObjectRepository, MongoRepository<org.commonlibrary.cllo.model.LearningObject, String> {


    @Query(value = '{"enabled" : ?0}')
    public Page<org.commonlibrary.cllo.model.LearningObject> findAll(boolean enabled, Pageable pageable)

    @Query(value = '{"enabled" : ?0}')
    public Page<org.commonlibrary.cllo.model.LearningObject> findAllIgnoreCase(boolean enabled, Pageable pageable)

    @Query(value = '{"enabled" : ?0}')
    public List<org.commonlibrary.cllo.model.LearningObject> findAll(boolean enabled)

}
