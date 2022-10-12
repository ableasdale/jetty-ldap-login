package working;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.jaas.spi.LdapLoginModule;
import org.eclipse.jetty.security.*;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Log in with user: Kafka_test and password
 */
public class LdapAuthentication {
    public static class TestConfiguration extends Configuration {
        private boolean forceBindingLogin;

        public TestConfiguration(boolean forceBindingLogin) {
            this.forceBindingLogin = forceBindingLogin;
        }

        @Override
        public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
            Map<String, String> options = new HashMap<>();
            options.put("useLdaps", "false");
            options.put("contextFactory", "com.sun.jndi.ldap.LdapCtxFactory");
            options.put("hostname", "WINDOWS_AD_HOSTNAME");
            options.put("port", "389");
            options.put("bindDn", "kafka@ad-test.confluent.io");
            options.put("bindPassword", "password-for-user-to-perform-binding");
            options.put("authenticationMethod", "simple");
            options.put("forceBindingLogin", Boolean.toString(forceBindingLogin));
            options.put("userBaseDn", "CN=Users,DC=ad-test,DC=confluent,DC=io");
            options.put("userRdnAttribute", "sAMAccountName");
            options.put("userIdAttribute", "sAMAccountName");
            options.put("userPasswordAttribute", "userPassword");
            options.put("userObjectClass", "person");
            options.put("roleBaseDn", "CN=Builtin,DC=ad-test,DC=confluent,DC=io");
            options.put("roleNameAttribute", "cn");
            options.put("roleMemberAttribute", "member");
            options.put("roleObjectClass", "group");
            options.put("debug", "true");

            AppConfigurationEntry entry = new AppConfigurationEntry(LdapLoginModule.class.getCanonicalName(), AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);

            return new AppConfigurationEntry[]{entry};
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

        context.addServlet(new ServletHolder(new DefaultServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                response.getWriter().append("hello " + request.getUserPrincipal().getName());
            }
        }), "/*");

        context.addServlet(new ServletHolder(new DefaultServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                response.getWriter().append("<html><form method='POST' action='/j_security_check'>"
                        + "<input type='text' name='j_username'/>"
                        + "<input type='password' name='j_password'/>"
                        + "<input type='submit' value='Login'/></form></html>");
            }
        }), "/login");

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__FORM_AUTH);
        constraint.setRoles(new String[]{"Administrators"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.addConstraintMapping(constraintMapping);
        securityHandler = new ConstraintSecurityHandler();
        securityHandler.addConstraintMapping(constraintMapping);

        JAASLoginService ls = new JAASLoginService("foo");
        ls.setCallbackHandlerClass("org.eclipse.jetty.jaas.callback.DefaultCallbackHandler");
        ls.setIdentityService(new DefaultIdentityService());
        ls.setConfiguration(new TestConfiguration(true));

        securityHandler.setLoginService(ls);
        FormAuthenticator authenticator = new FormAuthenticator("/login", "/login", false);
        securityHandler.setAuthenticator(authenticator);
        context.setSecurityHandler(securityHandler);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
