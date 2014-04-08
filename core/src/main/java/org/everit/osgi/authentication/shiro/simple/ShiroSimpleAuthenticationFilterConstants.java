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

public final class ShiroSimpleAuthenticationFilterConstants {

    public static final String COMPONENT_NAME = "org.everit.osgi.authentication.shiro.simple.ShiroSimpleAuthenticationFilter";

    public static final String FILTER_NAME = "filterName";

    public static final String FILTER_RANKING = "filterRanking";

    public static final String PROP_AUTHENTICATION_SERVICE_TARGET = "authenticationService.target";

    public static final String PROP_SIMPLE_SUBJECT_SERVICE_TARGET = "simpleSubjectService.target";

    public static final String PROP_REALM_TARGET = "realm.target";

    public static final String PROP_GLOBAL_SESSION_TIMEOUT = "globalSessionTimeout";

    public static final String PROP_SHIRO_INI_LOCATION = "shiroIniLocation";

    public static final long DEFAULT_GLOBAL_SESSION_TIMEOUT = 3600000; // one hour

    public static final String DEFAULT_SHIRO_INI_LOCATION = "configuration/shiro.ini";

    private ShiroSimpleAuthenticationFilterConstants() {
    }

}
