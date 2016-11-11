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
