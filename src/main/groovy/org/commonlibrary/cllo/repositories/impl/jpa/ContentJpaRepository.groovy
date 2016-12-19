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
