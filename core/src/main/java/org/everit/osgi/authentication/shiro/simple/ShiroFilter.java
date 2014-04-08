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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

public class ShiroFilter extends AbstractShiroFilter {

    public ShiroFilter(final long globalSessionTimeout, final String shiroIniLocation, final Realm realm) {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setGlobalSessionTimeout(globalSessionTimeout);

        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(realm);
        defaultWebSecurityManager.setSessionManager(defaultWebSessionManager);

        setSecurityManager(defaultWebSecurityManager);

        File shiroIniFile = new File(shiroIniLocation);
        if (!shiroIniFile.exists()) {
            throw new IllegalArgumentException("file [" + shiroIniLocation + "] not exists");
        }
        InputStream shiroIniInputStream;
        try {
            URL shiroIniUrl = shiroIniFile.toURI().toURL();
            shiroIniInputStream = shiroIniUrl.openStream();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        Ini ini = new Ini();
        ini.load(shiroIniInputStream);

        IniWebEnvironment iniWebEnvironment = new IniWebEnvironment();
        iniWebEnvironment.setIni(ini);
        iniWebEnvironment.init();

        FilterChainResolver resolver = iniWebEnvironment.getFilterChainResolver();
        setFilterChainResolver(resolver);
    }

}
