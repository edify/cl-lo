package org.commonlibrary.cllo.controllers.metadatavalues

import com.mangofactory.swagger.annotations.ApiIgnore
import org.commonlibrary.cllo.model.metadatavalues.Language
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/21/14
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
@RestController
@RequestMapping('${api.base.url}/listLanguages')
//@Api(value = 'languages', description = 'Operations about Languages')
@ApiIgnore
class LanguageController {

    @RequestMapping(method = RequestMethod.GET)
    //@ApiOperation(value = 'Find all languages', notes = 'Returns a list of different languages')
    public @ResponseBody
    List<Language> listLanguages() {

        try {
            List<Language> languages = Language.values()
            return languages
        }
        catch (Exception e) {
            println(e.message)
            return null
        }

    }

}
