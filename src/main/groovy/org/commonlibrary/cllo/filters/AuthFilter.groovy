/*
 * Copyright 2016 Edify Software Consulting.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


 package org.commonlibrary.cllo.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.util.logging.Log
import org.commonlibrary.cllo.auth.service.AuthService
import org.commonlibrary.cllo.repositories.LearningObjectRepository
import org.commonlibrary.cllo.services.LearningObjectService
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

    @Autowired
    LearningObjectRepository learningObjectRepository

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

        def allowsPublic = false

        // Check if the user requested a learning object's public file.
        if (method == "get") {
            allowsPublic = hasPublicAccess(req.getRequestURI())
        }

        requestURL = getFullURL(req.getRequestURL().toString(), req.getQueryString())

        if(!req.contentType?.startsWith('multipart') && (method == 'post' || method == 'put')) {
            bodyString = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()))
            body = mapper.readValue(bodyString, Object)
            body = mapper.writeValueAsString(body).replaceAll("[ \n]", "")
        } else {
            body = ''
        }

        if(allowsPublic || authService.authenticate(headers, method, requestURL, body)){
            chain.doFilter(req,res)
        } else{
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
        }
    }

    @Override
    void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    void destroy() {}

    private boolean hasPublicAccess(String requestURI) {
        try {
            def fileURI = "(.)*/learningObjects/([^/])+/contents/([^/])+/file/([^/])+(/(inputStream|base64|(versions/([^/])+)))?"
            if (requestURI.matches(fileURI)) {
                def learningObjectId = requestURI.split("/learningObjects/")[1].split("/")[0]
                def lo = learningObjectRepository.findById(learningObjectId)
                return !lo ? true : lo.isPublic
            } else {
                return false
            }
        } catch (Exception e) {
            return false
        }
    }

    private def getFullURL(requestURL, queryString) { (!queryString) ? requestURL : "${requestURL}?${queryString}" }

}
