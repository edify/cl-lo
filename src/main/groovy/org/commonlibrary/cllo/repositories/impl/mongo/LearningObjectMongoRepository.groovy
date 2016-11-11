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
