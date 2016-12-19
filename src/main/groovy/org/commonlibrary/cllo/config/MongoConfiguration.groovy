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


 package org.commonlibrary.cllo.config

import com.mongodb.Mongo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.config.AbstractMongoConfiguration
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * @author amasis
 * @since 2/18/14 1:09 PM
 */
@Configuration
@EnableMongoRepositories(basePackages = [
        'org.commonlibrary.clcore.repositories.impl.mongo',
        'org.commonlibrary.cllo.auth.repositories.mongo'])
@Profile(['BE_Mongo', 'FS_Mongo'])
public class MongoConfiguration extends AbstractMongoConfiguration {
    @Autowired
    private MongoProperties properties;

    @Autowired
    private Mongo mongo

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }

    @Override
    protected String getDatabaseName() {
        properties.getMongoClientDatabase()
    }

    @Override
    Mongo mongo() throws Exception {
        return mongo
    }
}
