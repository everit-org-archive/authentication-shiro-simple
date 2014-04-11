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
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.config.Ini;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.IniWebEnvironment;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.filter.mgt.FilterChainManager;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

public class DefaultShiroFilter extends AbstractShiroFilter {

    public DefaultShiroFilter(final long globalSessionTimeout, final String shiroIniLocation, final Realm realm,
            final String casLoginUrl, final String casFailureUrl) {

        Ini ini = loadIni(shiroIniLocation);

        IniWebEnvironment iniWebEnvironment = new IniWebEnvironment() {

            @Override
            protected FilterChainResolver createFilterChainResolver() {
                FilterChainResolver filterChainResolver = super.createFilterChainResolver();

                if (realm instanceof CasRealm) {
                    if (filterChainResolver instanceof PathMatchingFilterChainResolver) {
                        FilterChainManager filterChainManager =
                                ((PathMatchingFilterChainResolver) filterChainResolver).getFilterChainManager();
                        Map<String, Filter> filters = filterChainManager.getFilters();
                        for (Filter filter : filters.values()) {
                            if (filter instanceof RolesAuthorizationFilter) {
                                ((RolesAuthorizationFilter) filter).setLoginUrl(casLoginUrl);
                            }
                        }
                    }
                }

                return filterChainResolver;
            }

            @Override
            protected WebSecurityManager createWebSecurityManager() {
                DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
                defaultWebSessionManager.setGlobalSessionTimeout(globalSessionTimeout);

                DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
                webSecurityManager.setRealm(realm);
                webSecurityManager.setSessionManager(defaultWebSessionManager);
                if (realm instanceof CasRealm) {
                    webSecurityManager.setSubjectFactory(new CasSubjectFactory());
                    CasFilter casFilter = new CasFilter();
                    casFilter.setFailureUrl(casFailureUrl);
                    setObject("casFilter", casFilter);
                }
                return webSecurityManager;
            }

        };
        iniWebEnvironment.setIni(ini);
        iniWebEnvironment.init();

        WebSecurityManager securityManager = (WebSecurityManager) iniWebEnvironment.getSecurityManager();
        setSecurityManager(securityManager);

        FilterChainResolver filterChainResolver = iniWebEnvironment.getFilterChainResolver();
        setFilterChainResolver(filterChainResolver);
    }

    private Ini loadIni(final String shiroIniLocation) {
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
        return ini;
    }

}
