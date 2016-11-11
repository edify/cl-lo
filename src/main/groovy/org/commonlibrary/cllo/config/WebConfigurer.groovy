package org.commonlibrary.cllo.config

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @since
 */
@Configuration
class WebConfigurer {
    @Value('${server.port:9990}')
    private int port;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory(this.port);
        factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            void customize(Connector connector) {
                connector.setProperty("bindOnInit", "true");
            }
        });
        return factory;
    }
}
