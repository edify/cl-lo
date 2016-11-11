package org.commonlibrary.cllo.controllers.metadatavalues

import com.mangofactory.swagger.annotations.ApiIgnore
import org.commonlibrary.cllo.model.metadatavalues.Type
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/22/14
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping('${api.base.url}/listTypes')
//@Api(value = 'types', description = 'Operations about Types')
@ApiIgnore
class TypeController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all Types', notes = 'Returns a list of different Types')
    public @ResponseBody
    List<Type> listTypes() {

        try {
            List<Type> types = Type.values()
            return types
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}
