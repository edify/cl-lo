package org.commonlibrary.cllo.repositories.impl.mongo

import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/13/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */

@Repository
@Profile('BE_Mongo')
public interface ContentMongoRepository extends org.commonlibrary.cllo.repositories.ContentRepository, MongoRepository<org.commonlibrary.cllo.model.Contents, String> {}
