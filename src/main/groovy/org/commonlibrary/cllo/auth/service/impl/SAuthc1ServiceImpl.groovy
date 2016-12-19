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


 package org.commonlibrary.cllo.auth.service.impl

import org.commonlibrary.clauth.SAuthc1Signer
import org.commonlibrary.clauth.model.ApiKeyCredentials
import org.commonlibrary.clauth.services.ApiKeyService
import org.commonlibrary.cllo.auth.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.SimpleDateFormat;


/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/1/14
 * Time: 11:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
@Transactional
class SAuthc1ServiceImpl implements AuthService {

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private SAuthc1Signer sAuthc1Signer

    @Override
    def authenticate(headers, method, requestURL, body) {
        try {
            def authHeader = headers.get('authorization')
            def sauthId = authHeader.split(',')[0].split('=')[1]

            def nonce = sauthId.split('/')[2]
            def apiKeyId = sauthId.split('/')[0]
            def timestamp = headers.get('x-stormpath-date')


            def df = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
            def date = df.parse(timestamp)

            def credentials = new ApiKeyCredentials(apiKeyId, apiKeyService.getApiSecretKey(apiKeyId))

            def calcAuthHeader = sAuthc1Signer.sign(new HashMap(), method, requestURL, body, date, credentials, nonce)

            return authHeader == calcAuthHeader
        }catch(Exception e) {
            return false
        }

    }

}
