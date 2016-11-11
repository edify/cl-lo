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

