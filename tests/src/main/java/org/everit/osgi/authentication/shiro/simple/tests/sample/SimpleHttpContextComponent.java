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
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.http.whiteboard.HttpWhiteboardConstants;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.http.HttpContext;

@Component(name = "org.everit.osgi.authentication.shiro.simple.tests.sample.SimpleHttpContext", metatype = true,
        configurationFactory = false, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = HttpWhiteboardConstants.CONTEXT_ID),
        @Property(name = HttpWhiteboardConstants.CONTEXT_SHARED, boolValue = true)
})
public class SimpleHttpContextComponent {

    private class SimpleHttpContext implements HttpContext {

        @Override
        public String getMimeType(final String name) {
            return null;
        }

        @Override
        public URL getResource(final String name) {
            return null;
        }

        @Override
        public boolean handleSecurity(final HttpServletRequest request, final HttpServletResponse response)
                throws IOException {
            return true;
        }

    }

    private ServiceRegistration<HttpContext> httpContextSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws Exception {
        String contextId = getStringProperty(componentProperties, "contextId");
        Boolean shared = Boolean.valueOf(getStringProperty(componentProperties, "context.shared"));

        HttpContext httpContext = new SimpleHttpContext();
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put("contextId", contextId);
        properties.put("context.shared", shared);
        httpContextSR = context.registerService(HttpContext.class, httpContext, properties);
    }

    @Deactivate
    public void deactivate() {
        if (httpContextSR != null) {
            httpContextSR.unregister();
            httpContextSR = null;
        }
    }

    private String getStringProperty(final Map<String, Object> componentProperties, final String propertyName)
            throws ConfigurationException {
        Object value = componentProperties.get(propertyName);
        if (value == null) {
            throw new ConfigurationException(propertyName, "property not defined");
        }
        return String.valueOf(value);
    }

}
