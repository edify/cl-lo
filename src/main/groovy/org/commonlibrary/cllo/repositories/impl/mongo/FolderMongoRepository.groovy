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
