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

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.everit.osgi.authentication.simple.ActivationService;
import org.everit.osgi.authentication.simple.CredentialEncryptor;
import org.everit.osgi.authentication.simple.SimpleSubject;

/**
 * The {@link CredentialsMatcher} for authenticating {@link SimpleSubject}s.
 */
@Component(name = SimpleSubjectCredentialsMatcherConstants.COMPONENT_NAME, metatype = true,
        configurationFactory = true, policy = ConfigurationPolicy.REQUIRE)
@Properties({
        @Property(name = SimpleSubjectCredentialsMatcherConstants.PROP_ACTIVATION_SERVICE_TARGET),
        @Property(name = SimpleSubjectCredentialsMatcherConstants.PROP_CREDENTIAL_ENCRYPTOR_TARGET)
})
@Service
public class SimpleSubjectCredentialsMatcherComponent implements CredentialsMatcher {

    @Reference
    private ActivationService activationService;

    @Reference
    private CredentialEncryptor credentialEncryptor;

    public void bindActivationService(final ActivationService activationService) {
        this.activationService = activationService;
    }

    public void bindCredentialEncryptor(final CredentialEncryptor credentialEncryptor) {
        this.credentialEncryptor = credentialEncryptor;
    }

    @Override
    public boolean doCredentialsMatch(final AuthenticationToken token, final AuthenticationInfo info) {
        String principal = String.valueOf(token.getPrincipal());
        if (!activationService.isActive(principal)) {
            return false;
        }
        char[] credentials = (char[]) token.getCredentials();
        String plainCredential = String.valueOf(credentials);
        String encryptedCredential = String.valueOf(info.getCredentials());
        boolean credetialsMatch = credentialEncryptor.checkCredential(plainCredential, encryptedCredential);
        return credetialsMatch;
    }

}
