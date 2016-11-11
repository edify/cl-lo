package org.commonlibrary.cllo.support

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 9/29/14
 * Time: 12:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class ApiException extends Exception {
    public ApiException(String message, Exception cause) {
        super(message,cause)
    }
}
