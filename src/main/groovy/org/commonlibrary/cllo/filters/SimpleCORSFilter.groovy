package org.commonlibrary.cllo.filters

import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 1/27/14
 * Time: 5:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Order(value = 0)
public class SimpleCORSFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res
        if(((HttpServletRequest)req).getRequestURL().toString().contains("api")){
            response.setHeader("Access-Control-Allow-Origin", "*")
            response.setHeader("Access-Control-Allow-Cred", "true")
            response.setHeader("Content-Type", "application/json")
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE")
            response.setHeader("Access-Control-Max-Age", "3600")
            response.setHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with, Authorization")
            response.setCharacterEncoding("UTF-8")
        }
        chain.doFilter(req, res)
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

}
