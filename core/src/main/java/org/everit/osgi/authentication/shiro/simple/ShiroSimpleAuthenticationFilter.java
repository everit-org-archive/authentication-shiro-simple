/**
 * This file is part of org.everit.osgi.authentication.shiro.simple.
 *
 * org.everit.osgi.authentication.shiro.simple is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * org.everit.osgi.authentication.shiro.simple is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with org.everit.osgi.authentication.shiro.simple.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.everit.osgi.authentication.shiro.simple;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.everit.osgi.authentication.api.AuthenticatedAction;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.authentication.api.Subject;
import org.everit.osgi.authentication.simple.SimpleSubjectService;

public class ShiroSimpleAuthenticationFilter implements Filter {

    private final AuthenticationService authenticationService;

    private final SimpleSubjectService simpleSubjectService;

    public ShiroSimpleAuthenticationFilter(final AuthenticationService authenticationService,
            final SimpleSubjectService simpleSubjectService) {
        super();
        this.authenticationService = authenticationService;
        this.simpleSubjectService = simpleSubjectService;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        Object principalObject = SecurityUtils.getSubject().getPrincipal();
        Subject subject;
        if (principalObject != null) {
            String principal = String.valueOf(principalObject);
            subject = simpleSubjectService.readByPrincipal(principal);
        } else {
            subject = authenticationService.getCurrentSubject();
        }
        Exception exception = authenticationService.runAs(subject, new AuthenticatedAction<Exception>() {

            @Override
            public Exception run() {
                try {
                    chain.doFilter(request, response);
                    return null;
                } catch (IOException | ServletException e) {
                    return e;
                }
            }

        });
        if (exception != null) {
            if (exception instanceof IOException) {
                throw (IOException) exception;
            } else if (exception instanceof ServletException) {
                throw (ServletException) exception;
            } else {
                throw new ServletException("Failed to serve request", exception);
            }
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

}
