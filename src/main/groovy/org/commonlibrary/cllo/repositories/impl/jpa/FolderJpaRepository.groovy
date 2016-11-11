package org.commonlibrary.cllo.repositories.impl.jpa

import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
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
@Profile('BE_JPA')
public interface FolderJpaRepository extends org.commonlibrary.cllo.repositories.FolderRepository, JpaRepository<org.commonlibrary.cllo.model.Folder, String> {}
