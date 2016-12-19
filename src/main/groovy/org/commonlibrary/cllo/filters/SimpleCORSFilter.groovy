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
