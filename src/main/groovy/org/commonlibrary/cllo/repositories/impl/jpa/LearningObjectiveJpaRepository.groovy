package org.commonlibrary.cllo.repositories.impl.jpa

import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 12/6/13
 * Time: 8:52 AM
 * To change this template use File | Settings | File Templates.
 */

@Repository
@Profile('BE_JPA')
public interface LearningObjectiveJpaRepository extends org.commonlibrary.cllo.repositories.LearningObjectiveRepository, JpaRepository<org.commonlibrary.cllo.model.LearningObjective, String> {

    public Page<org.commonlibrary.cllo.model.LearningObjective> findAll(Pageable pageable)

}
