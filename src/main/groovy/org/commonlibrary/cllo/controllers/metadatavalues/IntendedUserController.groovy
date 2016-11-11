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
