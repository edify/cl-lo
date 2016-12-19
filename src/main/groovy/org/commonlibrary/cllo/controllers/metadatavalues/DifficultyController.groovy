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
import org.commonlibrary.cllo.model.metadatavalues.Difficulty
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 9:22 AM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping('${api.base.url}/listDifficulties')
//@Api(value = 'difficulties', description = 'Operations about Difficulties')
@ApiIgnore
class DifficultyController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all difficulty levels', notes = 'Returns a list of different difficulty levels')
    public @ResponseBody
    List<Difficulty> listDifficulties() {

        try {
            List<Difficulty> difficulties = Difficulty.values()
            return difficulties
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}
