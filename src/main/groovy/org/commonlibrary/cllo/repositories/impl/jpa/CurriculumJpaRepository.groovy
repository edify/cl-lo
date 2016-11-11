package org.commonlibrary.cllo.repositories.impl.jpa

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/11/13
 * Time: 9:24 AM
 * To change this template use File | Settings | File Templates.
 */

/***
 * @deprecated See <a href="https://github.com/edify/cl-curricula">Edify cl-curricula</a>
 */

@Deprecated
@Repository
@Profile('BE_JPA')
public interface CurriculumJpaRepository extends org.commonlibrary.cllo.repositories.CurriculumRepository, JpaRepository<org.commonlibrary.cllo.model.Curriculum, String> {

    @Query('select c from clc_curriculum c where c.enabled = ?1')
    public Page<org.commonlibrary.cllo.model.Curriculum> findAll(boolean enabled, Pageable pageable)

}
