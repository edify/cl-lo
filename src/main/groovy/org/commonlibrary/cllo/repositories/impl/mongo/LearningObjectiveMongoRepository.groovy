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
 * Date: 12/6/13
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */

@Repository
@Profile('BE_Mongo')
public interface LearningObjectiveMongoRepository extends org.commonlibrary.cllo.repositories.LearningObjectiveRepository, MongoRepository<org.commonlibrary.cllo.model.LearningObjective, String> {

    @Query(value = '{"enabled" : ?0}')
    public Page<org.commonlibrary.cllo.model.LearningObjective> findAll(boolean enabled, Pageable pageable)

    @Query(value = '{"enabled" : ?0}')
    public Page<org.commonlibrary.cllo.model.LearningObjective> findAllIgnoreCase(boolean enabled, Pageable pageable)

    @Query(value = '{"enabled" : ?0}')
    public List<org.commonlibrary.cllo.model.LearningObjective> findAll(boolean enabled)

}
