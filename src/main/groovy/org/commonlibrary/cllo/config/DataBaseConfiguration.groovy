package org.commonlibrary.cllo.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import groovy.util.logging.Slf4j
import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.orm.jpa.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.sql.DataSource


/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 7/17/14
 * Time: 10:45 AM
 * To change this template use File | Settings | File Templates.
 */

@Slf4j
@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages=["org.commonlibrary.clcore.model","org.commonlibrary.cllo.auth.model"])
@EnableJpaRepositories(basePackages = ["org.commonlibrary.clcore.repositories.impl.jpa","org.commonlibrary.cllo.auth.repositories.jpa"])
@EnableTransactionManagement
@Profile('BE_JPA')
class DataBaseConfiguration {

    @Autowired
    private Environment environment

    @Value('${database.url}')
    private String databaseUrl
    @Value('${database.dataSourceClassName}')
    private String dataSourceClassName

    @Bean
    public DataSource dataSource() throws Exception {
        return new HikariDataSource(parseDatabaseUrl(databaseUrl))
    }

    @Bean(name = 'org.springframework.boot.autoconfigure.AutoConfigurationUtils.basePackages')
    public List<String> getBasePackages() {
        List<String> basePackages = new ArrayList<>()
        basePackages.add('org.commonlibrary.clcore.model')
        basePackages.add('org.commonlibrary.cllo.auth.model')
        return basePackages
    }

    @Bean
    public SpringLiquibase liquibase() throws Exception {
        log.debug("Configuring Liquibase")
        SpringLiquibase liquibase = new SpringLiquibase()
        liquibase.setDataSource(dataSource())
        liquibase.setChangeLog("classpath:config/liquibase/db-changelog.xml")
        liquibase.setContexts("development, production")
        return liquibase
    }

    private HikariConfig parseDatabaseUrl(String url) throws Exception {

        URI dbUri = new URI(url)
        String username = ""
        String password = ""
        if (dbUri.getUserInfo() != null) {
            username = dbUri.getUserInfo().split(":")[0]
            if(dbUri.getUserInfo().split(":").size() > 1){
                password = dbUri.getUserInfo().split(":")[1]
            }
        }
        String dbUrl

        if (url.contains("postgres")) {
            dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath()
        } else if (url.contains("h2")) {
            dbUrl = "jdbc:h2:mem:" + dbUri.getPath()
        } else {
            throw new RuntimeException("No URL known for: [ " + url + " ]")
        }

        HikariConfig config = new HikariConfig()
        config.setDataSourceClassName(dataSourceClassName)
        if (environment.acceptsProfiles("heroku")) {
            config.addDataSourceProperty("ssl", true)
            config.addDataSourceProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory")
        }
        config.addDataSourceProperty("url", dbUrl)
        config.addDataSourceProperty("user", username)
        config.addDataSourceProperty("password", password)
        config.setConnectionTestQuery("SELECT 1")

        return config
    }

}
