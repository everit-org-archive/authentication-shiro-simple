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

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.felix.http.whiteboard.HttpWhiteboardConstants;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.shiro.realm.Realm;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.authentication.simple.SimpleSubjectService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.log.LogService;

@Component(name = ShiroSimpleAuthenticationFilterConstants.COMPONENT_NAME, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_FILTER_NAME),
        @Property(name = HttpWhiteboardConstants.PATTERN),
        @Property(name = HttpWhiteboardConstants.CONTEXT_ID),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_FILTER_RANKING),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_CAS_LOGIN_URL,
                value = ShiroSimpleAuthenticationFilterConstants.DEFAULT_CAS_LOGIN_URL),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_CAS_FAILURE_URL,
                value = ShiroSimpleAuthenticationFilterConstants.DEFAULT_CAS_FAILURE_URL),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_GLOBAL_SESSION_TIMEOUT,
                longValue = ShiroSimpleAuthenticationFilterConstants.DEFAULT_GLOBAL_SESSION_TIMEOUT),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_SHIRO_INI_LOCATION,
                value = ShiroSimpleAuthenticationFilterConstants.DEFAULT_SHIRO_INI_LOCATION),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_AUTHENTICATION_SERVICE_TARGET),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_SIMPLE_SUBJECT_SERVICE_TARGET),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_REALM_TARGET),
        @Property(name = ShiroSimpleAuthenticationFilterConstants.PROP_LOG_SERVICE_TARGET)
})
public class ShiroSimpleAuthenticationFilterComponent {

    @Reference
    private AuthenticationService authenticationService;

    @Reference
    private SimpleSubjectService simpleSubjectService;

    @Reference
    private Realm realm;

    @Reference
    private LogService logService;

    private ServiceRegistration<Filter> shiroFilterSR;

    private ServiceRegistration<Filter> shiroSimpleAuthFilterSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws Exception {
        String filterName = getStringProperty(componentProperties,
                ShiroSimpleAuthenticationFilterConstants.PROP_FILTER_NAME);
        String pattern = getStringProperty(componentProperties,
                HttpWhiteboardConstants.PATTERN);
        String contextId = getStringProperty(componentProperties,
                HttpWhiteboardConstants.CONTEXT_ID);
        int ranking = Integer.valueOf(getStringProperty(componentProperties,
                ShiroSimpleAuthenticationFilterConstants.PROP_FILTER_RANKING));
        String casLoginUrl = getStringProperty(componentProperties,
                ShiroSimpleAuthenticationFilterConstants.PROP_CAS_LOGIN_URL);
        String casFailureUrl = getStringProperty(componentProperties,
                ShiroSimpleAuthenticationFilterConstants.PROP_CAS_FAILURE_URL);
        long globalSessionTimeout = Long.valueOf(
                getStringProperty(componentProperties,
                        ShiroSimpleAuthenticationFilterConstants.PROP_GLOBAL_SESSION_TIMEOUT))
                .longValue();
        String shiroIniLocation = getStringProperty(componentProperties,
                ShiroSimpleAuthenticationFilterConstants.PROP_SHIRO_INI_LOCATION);

        Filter shiroFilter =
                new DefaultShiroFilter(globalSessionTimeout, shiroIniLocation, realm, casLoginUrl, casFailureUrl);
        Dictionary<String, Object> shiroFilterProperties = new Hashtable<>();
        shiroFilterProperties.put(ShiroSimpleAuthenticationFilterConstants.PROP_FILTER_NAME,
                filterName + "-" + DefaultShiroFilter.class.getSimpleName());
        shiroFilterProperties.put(HttpWhiteboardConstants.PATTERN, pattern);
        shiroFilterProperties.put(HttpWhiteboardConstants.CONTEXT_ID, contextId);
        shiroFilterProperties.put(Constants.SERVICE_RANKING, ranking);
        shiroFilterSR = context.registerService(Filter.class, shiroFilter, shiroFilterProperties);

        Filter shiroSimpleFilter =
                new ShiroSimpleAuthenticationFilter(authenticationService, simpleSubjectService, logService);
        Dictionary<String, Object> shiroSimpleProperties = new Hashtable<>();
        shiroSimpleProperties.put(ShiroSimpleAuthenticationFilterConstants.PROP_FILTER_NAME,
                filterName + "-" + ShiroSimpleAuthenticationFilter.class.getSimpleName());
        shiroSimpleProperties.put(HttpWhiteboardConstants.PATTERN, pattern);
        shiroSimpleProperties.put(HttpWhiteboardConstants.CONTEXT_ID, contextId);
        shiroSimpleProperties.put(Constants.SERVICE_RANKING, ranking - 1);
        shiroSimpleAuthFilterSR = context.registerService(Filter.class, shiroSimpleFilter, shiroSimpleProperties);
    }

    public void bindAuthenticationService(final AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    public void bindLogService(final LogService logService) {
        this.logService = logService;
    }

    public void bindRealm(final Realm realm) {
        this.realm = realm;
    }

    public void bindSimpleSubjectService(final SimpleSubjectService simpleSubjectService) {
        this.simpleSubjectService = simpleSubjectService;
    }

    @Deactivate
    public void deactivate() {
        if (shiroFilterSR != null) {
            shiroFilterSR.unregister();
            shiroFilterSR = null;
        }
        if (shiroSimpleAuthFilterSR != null) {
            shiroSimpleAuthFilterSR.unregister();
            shiroSimpleAuthFilterSR = null;
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
