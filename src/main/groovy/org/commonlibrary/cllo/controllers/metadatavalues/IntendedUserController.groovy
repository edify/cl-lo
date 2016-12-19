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


 package org.commonlibrary.cllo.controllers.metadatavalues

import com.mangofactory.swagger.annotations.ApiIgnore
import org.commonlibrary.cllo.model.metadatavalues.IntendedUser
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping('${api.base.url}/listIntendedUsers')
//@Api(value = 'intended users', description = 'Operations about Intended Users')
@ApiIgnore
class IntendedUserController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all intended users', notes = 'Returns a list of different users')
    public @ResponseBody
    List<IntendedUser> listIntendedUsers() {

        try {
            List<IntendedUser> intendedUsers = IntendedUser.values()
            return intendedUsers
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }
}
