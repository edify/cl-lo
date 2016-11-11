package org.commonlibrary.cllo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.commons.CommonsMultipartResolver

import javax.servlet.MultipartConfigElement

/**
 * @author amasis
 * @since 2/21/14 5:18 PM
 */
@Configuration
class ContentConfig {
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver mr = new CommonsMultipartResolver()
        mr.setMaxUploadSize(Long.MAX_VALUE)
        return mr
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {

        return new MultipartConfigElement('/tmp',-1,-1,-1)

    }

}

