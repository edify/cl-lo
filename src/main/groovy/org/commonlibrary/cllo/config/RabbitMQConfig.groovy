package org.commonlibrary.cllo.config

import com.rabbitmq.client.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

/**
 * Created by diugalde on 22/09/16.
 */
@Configuration
@Profile('INDEX_RMQ')
class RabbitMQConfig {

    @Value('${rabbitmq.general.url}')
    String rabbitMQUrl

    @Bean
    ConnectionFactory connectionFactory() {
        def factory = new ConnectionFactory()
        factory.setUri(rabbitMQUrl)
        return factory
    }

}
