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

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.realm.Realm;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;

@Component(name = CasRealmConstants.COMPONENT_NAME, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = CasRealmConstants.PROP_DEFAULT_ROLES,
                value = CasRealmConstants.DEFAULT_DEFAULT_ROLES),
        @Property(name = CasRealmConstants.PROP_CAS_SERVER_URL_PREFIX,
                value = CasRealmConstants.DEFAULT_CAS_SERVER_URL_PREFIX),
        @Property(name = CasRealmConstants.PROP_CAS_SERVICE,
                value = CasRealmConstants.DEFAULT_CAS_SERVICE)
})
public class CasRealmComponent {

    private ServiceRegistration<Realm> casRealmSR;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws Exception {
        String defaultRoles = getStringProperty(componentProperties, CasRealmConstants.PROP_DEFAULT_ROLES);
        String casServerUrlPrefix = getStringProperty(componentProperties, CasRealmConstants.PROP_CAS_SERVER_URL_PREFIX);
        String casService = getStringProperty(componentProperties, CasRealmConstants.PROP_CAS_SERVICE);

        CasRealm casRealm = new CasRealm();
        casRealm.setDefaultRoles(defaultRoles);
        casRealm.setCasServerUrlPrefix(casServerUrlPrefix);
        casRealm.setCasService(casService);
        Dictionary<String, Object> properties = new Hashtable<>();
        properties.put(CasRealmConstants.PROP_DEFAULT_ROLES, defaultRoles);
        properties.put(CasRealmConstants.PROP_CAS_SERVER_URL_PREFIX, casServerUrlPrefix);
        properties.put(CasRealmConstants.PROP_CAS_SERVICE, casService);
        properties.put("component.name", CasRealmConstants.COMPONENT_NAME);
        casRealmSR = context.registerService(Realm.class, casRealm, properties);
    }

    @Deactivate
    public void deactivate() {
        if (casRealmSR != null) {
            casRealmSR.unregister();
            casRealmSR = null;
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
