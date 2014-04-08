/**
 * This file is part of org.everit.osgi.authentication.shiro.simple.tests.
 *
 * org.everit.osgi.authentication.shiro.simple.tests is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.shiro.simple.tests is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.shiro.simple.tests.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.shiro.simple.tests.sample;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.http.whiteboard.HttpWhiteboardConstants;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.everit.osgi.authentication.api.AuthenticationService;

@Component(name = "org.everit.osgi.authentication.shiro.simple.tests.sample.HelloWorldServlet", metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = HttpWhiteboardConstants.ALIAS),
        @Property(name = HttpWhiteboardConstants.CONTEXT_ID),
        @Property(name = "authenticationService.target")
})
@Service(value = Servlet.class)
public class HelloWorldServletComponent extends HttpServlet {

    private static final long serialVersionUID = -5545883781165913751L;

    @Reference
    private AuthenticationService authenticationService;

    public void bindAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {
        long currentResourceId = authenticationService.getCurrentSubject().getResourceId();
        resp.setContentType("text/plain");
        PrintWriter out = resp.getWriter();
        out.print(currentResourceId);
    }

}
