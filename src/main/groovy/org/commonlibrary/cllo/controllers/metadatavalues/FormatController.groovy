package org.commonlibrary.cllo.controllers.metadatavalues

import com.mangofactory.swagger.annotations.ApiIgnore
import org.commonlibrary.cllo.model.metadatavalues.Format
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
@RequestMapping('${api.base.url}/listFormats')
//@Api(value = 'format', description = 'Operations about Formats')
@ApiIgnore
class FormatController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all formats', notes = 'Returns a list of different formats')
    public @ResponseBody
    List<Format> listFormats() {

        try {
            List<Format> formats = Format.values()
            return formats
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}
