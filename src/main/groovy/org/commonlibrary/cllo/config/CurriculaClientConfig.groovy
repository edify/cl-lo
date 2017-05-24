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

import org.commonlibrary.clauth.model.ApiKeyCredentials
import org.commonlibrary.clsdk.curricula.Curricula
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Created by diugalde on 22/05/17.
 */
@Configuration
class CurriculaClientConfig {

    @Value('${curricula.general.base_url}')
    private String baseUrl

    @Value('${curricula.general.api_url}')
    private String apiUrl

    @Value('${curricula.general.api_key_id}')
    private String apiKeyId

    @Value('${curricula.general.api_secret_key}')
    private String apiSecretKey

    @Bean
    Curricula curricula() {
        ApiKeyCredentials apiKeyCredentials = new ApiKeyCredentials(apiKeyId, apiSecretKey)
        return new Curricula(apiKeyCredentials, baseUrl, apiUrl)
    }
}
