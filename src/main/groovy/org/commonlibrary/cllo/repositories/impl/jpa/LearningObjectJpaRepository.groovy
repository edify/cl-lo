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
 * Date: 6/13/14
 * Time: 5:11 PM
 * To change this template use File | Settings | File Templates.
 */

@Repository
@Profile('BE_JPA')
public interface LearningObjectJpaRepository extends org.commonlibrary.cllo.repositories.LearningObjectRepository, JpaRepository<org.commonlibrary.cllo.model.LearningObject, String> {

    @Query('select lo from clc_learning_object lo where lo.enabled = ?1')
    public Page<org.commonlibrary.cllo.model.LearningObject> findAll(boolean enabled, Pageable pageable)

    @Query('select lo from clc_learning_object lo where lo.enabled = ?1')
    public List<org.commonlibrary.cllo.model.LearningObject> findAll(boolean enabled)

    @Query('select lo from clc_learning_object lo where lo.enabled = ?1')
    public Page<org.commonlibrary.cllo.model.LearningObject> findAllIgnoreCase(boolean enabled, Pageable pageable)

    @Query(value = 'select lo from clc_learning_object lo, clc_l_object_x_l_objective_list loxloi where loxloi.clc_learning_objective_id = ?1 and lo.id = loxloi.clc_learning_object_id',nativeQuery = true)
    public List<org.commonlibrary.cllo.model.LearningObject> findAll(String learningObjectiveId)

}
