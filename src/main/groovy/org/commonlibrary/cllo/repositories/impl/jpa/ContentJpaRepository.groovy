package org.commonlibrary.cllo.repositories.impl.jpa

import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/13/13
 * Time: 10:07 AM
 * To change this template use File | Settings | File Templates.
 */

@Repository
@Profile('BE_JPA')
public interface ContentJpaRepository extends org.commonlibrary.cllo.repositories.ContentRepository,  JpaRepository<org.commonlibrary.cllo.model.Contents, String> {}
