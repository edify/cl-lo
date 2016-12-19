package org.commonlibrary.cllo.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.util.logging.Log
import org.commonlibrary.cllo.auth.service.AuthService
import org.commonlibrary.cllo.support.MultiReadHttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.stream.Collectors

/**
 * Created with IntelliJ IDEA.
 * Users: amasis
 * Date: 9/29/14
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Log
@Component
@Order(value = 1)
class AuthFilter implements Filter {

    @Autowired
    AuthService authService

    ObjectMapper mapper

    AuthFilter() {
        mapper = new ObjectMapper()
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
    }

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) response
        // This wrapper is used in order to read the body twice.
        MultiReadHttpServletRequest req = new MultiReadHttpServletRequest((HttpServletRequest) request);

        // Required authorization params.
        def headers, method, requestURL, body, bodyString

        // Set headers map.
        headers = new HashMap()
        Enumeration headerNames = req.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String headerName = (String) headerNames.nextElement();
            headers.put(headerName.toLowerCase(), req.getHeader(headerName))
        }

        method = req.getMethod().toLowerCase()

        requestURL = getFullURL(req.getRequestURL().toString(), req.getQueryString())

        if(!req.contentType?.startsWith('multipart') && (method == 'post' || method == 'put')) {
            bodyString = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()))
            body = mapper.readValue(bodyString, Object)
            body = mapper.writeValueAsString(body).replaceAll("[ \n]", "")
        } else {
            body = ''
        }

        if(authService.authenticate(headers, method, requestURL, body)){
            chain.doFilter(req,res)
        } else{
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    void destroy() {}

    private def getFullURL(requestURL, queryString) { (!queryString) ? requestURL : "${requestURL}?${queryString}" }

}
