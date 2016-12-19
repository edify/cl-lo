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


 package org.commonlibrary.cllo.auth.service


/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/1/14
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AuthService {

    def authenticate(headers, method, requestURL, body)

}
