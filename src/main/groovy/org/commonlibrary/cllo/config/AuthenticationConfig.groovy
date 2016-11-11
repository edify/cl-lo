package org.commonlibrary.cllo.config

import org.commonlibrary.clauth.SAuthc1Signer
import org.commonlibrary.clauth.services.ApiKeyService
import org.commonlibrary.clauth.services.impl.ApiKeyServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by diugalde on 02/09/16.
 */
@Configuration
class AuthenticationConfig {

    @Bean
    SAuthc1Signer sAuthc1Signer() { new SAuthc1Signer() }

    @Bean
    ApiKeyService apiKeyService() { new ApiKeyServiceImpl() }
}
