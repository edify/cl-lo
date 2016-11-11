package org.commonlibrary.cllo.auth.service


/**
 * Created with IntelliJ IDEA.
 * User: amasis
 * Date: 10/1/14
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AuthService {

    def authenticate(headers, method, requestURL, body)

}
