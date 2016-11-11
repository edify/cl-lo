package org.commonlibrary.cllo.controllers.metadatavalues

import com.mangofactory.swagger.annotations.ApiIgnore
import org.commonlibrary.cllo.model.metadatavalues.Status
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 9:27 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping('${api.base.url}/listStatuses')
//@Api(value = 'statuses', description = 'Operations about statuses')
@ApiIgnore
class StatusController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all possible statuses', notes = 'Returns a list of possible statuses')
    public @ResponseBody
    List<Status> listStatuses() {

        try {
            List<Status> statuses = Status.values()
            return statuses
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}
