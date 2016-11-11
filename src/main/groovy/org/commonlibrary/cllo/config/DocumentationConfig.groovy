package org.commonlibrary.cllo.config

import com.fasterxml.classmate.TypeResolver
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.models.alternates.AlternateTypeProvider
import com.mangofactory.swagger.plugin.EnableSwagger
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import com.wordnik.swagger.model.ApiInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpEntity
import com.mangofactory.swagger.models.alternates.WildcardType
import static com.mangofactory.swagger.models.alternates.Alternates.newRule

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 9/22/14
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@EnableSwagger
@ComponentScan("org.commonlibrary.cllo.controllers.metadatavalues")
public class DocumentationConfig {

    private SpringSwaggerConfig springSwaggerConfig

    @Autowired
    public void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig
    }

    @Bean
    public SwaggerSpringMvcPlugin customImplementation(){

        AlternateTypeProvider alternateTypeProvider = springSwaggerConfig.defaultAlternateTypeProvider()
        TypeResolver typeResolver = new TypeResolver()

        alternateTypeProvider.addRule(newRule(typeResolver.resolve(HttpEntity.class, WildcardType.class),
        typeResolver.resolve(WildcardType.class)))

        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .includePatterns(".*api.*")
                .alternateTypeProvider(alternateTypeProvider)
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Common Library API",
                "This is Common Library API description",
                "http://www.edify.cr/",
                "info@edify.cr",
                "Common Library API Licence Type",
                "http://www.edify.cr/"
        );
        return apiInfo
    }
}
