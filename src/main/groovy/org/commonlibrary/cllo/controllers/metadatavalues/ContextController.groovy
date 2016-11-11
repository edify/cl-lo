package org.commonlibrary.cllo.controllers.metadatavalues

import com.mangofactory.swagger.annotations.ApiIgnore
import org.commonlibrary.cllo.model.metadatavalues.Context
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 12:56 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping('${api.base.url}/listContexts')
//@Api(value = 'contexts', description = 'Operations about Contexts')
@ApiIgnore
class ContextController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all contexts', notes = 'Returns a list of different contexts')
    public @ResponseBody List<Context> listContexts() {

        try {
            List<Context> contexts = Context.values()
            return contexts

        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}
