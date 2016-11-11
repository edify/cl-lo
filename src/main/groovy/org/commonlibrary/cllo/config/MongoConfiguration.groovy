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
