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

import java.util.Map;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.realm.Realm;
import org.everit.osgi.authentication.simple.CredentialService;
import org.everit.osgi.authentication.simple.SimpleSubject;
import org.osgi.framework.BundleContext;

/**
 * An {@link AuthenticatingRealm} for authenticating {@link SimpleSubject}s.
 */
@Component(name = SimpleSubjectRealmConstants.COMPONENT_NAME, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = SimpleSubjectRealmConstants.PROP_CREDENTIAL_SERVICE_TARGET),
        @Property(name = SimpleSubjectRealmConstants.PROP_CREDENTIALS_MATCHER_TARGET)
})
@Service(value = Realm.class)
public class SimpleSubjectRealmComponent extends AuthenticatingRealm {

    @Reference
    private CredentialService credentialService;

    @Reference
    private CredentialsMatcher credentialsMatcher;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws Exception {
        setCredentialsMatcher(credentialsMatcher);
    }

    public void bindCredentialService(final CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void bindCredentialsMatcher(final CredentialsMatcher credentialsMatcher) {
        this.credentialsMatcher = credentialsMatcher;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken token) {
        String principal = String.valueOf(token.getPrincipal());
        String credential = credentialService.getCredential(principal);
        if (credential == null) {
            return null;
        }
        return new SimpleAuthenticationInfo(principal, credential, SimpleSubjectRealmComponent.class.getName());
    }

}
