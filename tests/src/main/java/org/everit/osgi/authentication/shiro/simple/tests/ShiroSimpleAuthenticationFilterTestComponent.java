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
package org.everit.osgi.authentication.shiro.simple.tests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.everit.osgi.authentication.api.AuthenticationService;
import org.everit.osgi.authentication.simple.SimpleSubject;
import org.everit.osgi.authentication.simple.SimpleSubjectService;
import org.everit.osgi.dev.testrunner.TestRunnerConstants;
import org.everit.osgi.props.PropertyService;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;

@Component(name = "ShiroSimpleAuthenticationFilterTest", immediate = true, configurationFactory = false,
        policy = ConfigurationPolicy.OPTIONAL)
@Properties({
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TESTRUNNER_ENGINE_TYPE, value = "junit4"),
        @Property(name = TestRunnerConstants.SERVICE_PROPERTY_TEST_ID, value = "ShiroSimpleAuthenticationFilterTest"),
        @Property(name = "propertyService.target"),
        @Property(name = "simpleSubjectService.target"),
        @Property(name = "httpService.target", value = "(org.osgi.service.http.port=*)")
})
@Service(value = ShiroSimpleAuthenticationFilterTestComponent.class)
public class ShiroSimpleAuthenticationFilterTestComponent {

    @Reference
    private PropertyService propertyService;

    @Reference
    private SimpleSubjectService simpleSubjectService;

    @Reference
    private HttpService httpService;

    private long defaultSubjectResourceId;

    private int port;

    private String guestUri;

    private String secureUri;

    private long simpleSubjectResourceId;

    private Header successAuthorizationHeader;

    private Header failedAuthorizationHeader;

    @Activate
    public void activate(final BundleContext context, final Map<String, Object> componentProperties)
            throws Exception {
        // port = getPort();
        guestUri = "http://localhost:" + port + "/hello/basic/guest";
        secureUri = "http://localhost:" + port + "/hello/basic/protected";

        defaultSubjectResourceId = Long.valueOf(
                propertyService.getProperty(AuthenticationService.PROP_DEFAULT_SUBJECT_RESOURCE_ID)).longValue();

        String userName = "Aladdin";
        String password = "open sesame";
        SimpleSubject simpleSubject = simpleSubjectService.create(null, userName, password, true);
        simpleSubjectResourceId = simpleSubject.getResourceId();
        successAuthorizationHeader = new BasicHeader("Authorization", "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
        failedAuthorizationHeader = new BasicHeader("Authorization", "Basic QWxhZGRpbJpvcGVuIHNlc2FtZQ==");
        Thread.sleep(5000); // FIXME waiting for the servlet registration to the servlet container - temporary solution
    }

    private void assertGet(final String uri, final Header header, final int expectedStatusCode,
            final Long expectedResourceId) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);
        if (header != null) {
            httpGet.addHeader(header);
        }
        HttpResponse httpResponse = httpClient.execute(httpGet);
        Assert.assertEquals("Wrong status code on URI [" + uri + "] with header [" + header + "]",
                expectedStatusCode,
                httpResponse.getStatusLine().getStatusCode());
        if ((expectedStatusCode == HttpStatus.SC_OK) && (expectedResourceId != null)) {
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputStream, writer);
            String responseBodyAsString = writer.toString();
            Assert.assertEquals(expectedResourceId, Long.valueOf(responseBodyAsString));
        }
    }

    public void bindHttpService(final HttpService httpService, final Map<String, Object> properties) {
        this.httpService = httpService;
        port = Integer.valueOf((String) properties.get("org.osgi.service.http.port"));
        port--; // TODO port must be decremented because the port of the Server is less than the value of the service
                // portperty queried above
    }

    public void bindPropertyService(final PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    public void bindSimpleSubjectService(final SimpleSubjectService simpleSubjectService) {
        this.simpleSubjectService = simpleSubjectService;
    }

    // private int getPort() {
    // Connector[] connectors = server.getConnectors();
    // for (Connector connector : connectors) {
    // if (connector.isStarted()) {
    // return connector.getLocalPort();
    // }
    // }
    // throw new RuntimeException("No opened connector found for Jetty server");
    // }

    @Test
    public void testAnonAccessGuestUri() throws Exception {
        assertGet(guestUri, null, HttpStatus.SC_OK, defaultSubjectResourceId);
    }

    @Test
    public void testAnonAccessSecureUri() throws Exception {
        assertGet(secureUri, null, HttpStatus.SC_UNAUTHORIZED, defaultSubjectResourceId);
    }

    @Test
    public void testAuthenticatedAccessGuestUri() throws Exception {
        assertGet(guestUri, successAuthorizationHeader, HttpStatus.SC_OK, defaultSubjectResourceId);
    }

    @Test
    public void testAuthenticatedAccessSecureUri() throws Exception {
        assertGet(secureUri, successAuthorizationHeader, HttpStatus.SC_OK, simpleSubjectResourceId);
    }

    @Test
    public void testFailedAuthenticationAccessGuestUri() throws Exception {
        assertGet(guestUri, failedAuthorizationHeader, HttpStatus.SC_OK, defaultSubjectResourceId);
    }

    @Test
    public void testFailedAuthenticationAccessSecureUri() throws Exception {
        assertGet(secureUri, failedAuthorizationHeader, HttpStatus.SC_UNAUTHORIZED, defaultSubjectResourceId);
    }

}
