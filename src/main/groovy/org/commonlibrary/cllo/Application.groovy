package org.commonlibrary.cllo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.context.annotation.ComponentScan

/**
 * @author amasis
 * @since 1/10/14 9:26 AM
 */
@EnableAutoConfiguration(exclude = [
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class])
@ComponentScan(basePackages = ['org.commonlibrary.clcore', 'org.commonlibrary.cllo'])
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args)
    }
}
