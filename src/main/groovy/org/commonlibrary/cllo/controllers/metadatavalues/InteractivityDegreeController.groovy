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
import org.commonlibrary.cllo.model.metadatavalues.InteractivityDegree
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping('${api.base.url}/listInteractivityDegrees')
//@Api(value = 'interactivity degrees', description = 'Operations about Interactivity Degrees')
@ApiIgnore
class InteractivityDegreeController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all interactivity degrees', notes = 'Returns a list of different interactivity degrees')
    public @ResponseBody
    List<InteractivityDegree> listInteractivityDegrees() {

        try {
            List<InteractivityDegree> interactivityDegrees = InteractivityDegree.values()
            return interactivityDegrees
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}

