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

public final class CasRealmConstants {

    public static final String COMPONENT_NAME = "org.everit.osgi.authentication.shiro.simple.cas.CasRealm";

    public static final String PROP_DEFAULT_ROLES = "defaultRoles";

    public static final String DEFAULT_DEFAULT_ROLES = "ROLE_USER";

    public static final String PROP_CAS_SERVER_URL_PREFIX = "casServerUrlPrefix";

    public static final String DEFAULT_CAS_SERVER_URL_PREFIX = "http://localhost:18080/cas";

    public static final String PROP_CAS_SERVICE = "casService";

    public static final String DEFAULT_CAS_SERVICE = "http://localhost:8080/hello/cas/shiro-cas";

    private CasRealmConstants() {
    }

}
