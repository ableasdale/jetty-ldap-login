package archived;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.jaas.spi.LdapLoginModule;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;

import javax.naming.directory.Attribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TryThis {
    public static void main(String[] args) throws Exception {

        Server jettyServer = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(jettyServer, "/", ServletContextHandler.SESSIONS | ServletContextHandler.SECURITY);

        context.addServlet(new ServletHolder(new DefaultServlet() {
            @Override
            protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                request.getSession().invalidate();  // do logout
                response.getWriter().append("<html><form method='POST' action='/j_security_check'>"
                        + "<input type='text' name='j_username'/>"
                        + "<input type='password' name='j_password'/>"
                        + "<input type='submit' value='Login'/></form></html>");
            }
        }), "/login");

        context.addServlet(new ServletHolder(new DefaultServlet() {
            @Override
            protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
                //request.getSession().invalidate();  // do logout
                ConstraintSecurityHandler securityHandler;
                Constraint constraint = new Constraint();
                constraint.setName(Constraint.__FORM_AUTH);
                constraint.setRoles(new String[]{"user"});
                constraint.setAuthenticate(true);

                ConstraintMapping constraintMapping = new ConstraintMapping();
                constraintMapping.setConstraint(constraint);
                constraintMapping.setPathSpec("/*");
                LdapLoginModule lm = new LdapLoginModule();
                Map options = new HashMap<>();
                options.put( "hostname", "WIN-7F40HNU7OPJ" );
                options.put( "debug", "true");
                options.put( "port", "389" );
                options.put( "contextFactory", "com.sun.jndi.ldap.LdapCtxFactory" );
                options.put( "bindDn", "kafka@ad-test.confluent.io" );
                options.put( "bindPassword", "t1nk3r&b3ll" );
                options.put("forceBindingLogin", "true");
                options.put( "userBaseDn", "CN=Users,DC=ad-test,DC=confluent,DC=io");
                lm.initialize(null,null,null,options);

                securityHandler = new ConstraintSecurityHandler ();
                securityHandler.addConstraintMapping(constraintMapping);
                JAASLoginService loginService = new JAASLoginService("ldaploginmodule");
                loginService.setIdentityService(new DefaultIdentityService());
                securityHandler.setLoginService(loginService);

                /*
                for (Attribute s: request.getSession().getAttributeNames()){

                } */
                //loginService.login("xxx", "yyy");
               // LOG.info(securityHandler.getLoginService().validate());
                response.getWriter().append("<html><h1>Sec</h1></html>");
            }
        }), "/j_security_check");

        //context.addServlet(new ServletHolder(new TryThis()),"/*");

        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__FORM_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler securityHandler;

        boolean ldapEnabled = true;
        if (ldapEnabled) { // *** something is missing ****
            LdapLoginModule lm = new LdapLoginModule();
            Map options = new HashMap<>();
            options.put( "hostname", "WIN-7F40HNU7OPJ" );
            options.put( "port", "389" );
            options.put( "contextFactory", "com.sun.jndi.ldap.LdapCtxFactory" );
            options.put( "bindDn", "kafka@ad-test.confluent.io" );
            options.put( "bindPassword", "t1nk3r&b3ll" );
            options.put("forceBindingLogin", "true");
            options.put( "userBaseDn", "CN=Users,DC=ad-test,DC=confluent,DC=io");
            lm.initialize(null,null,null,options);

            securityHandler = new ConstraintSecurityHandler ();
            securityHandler.addConstraintMapping(constraintMapping);
            JAASLoginService loginService = new JAASLoginService("ldaploginmodule");
            loginService.setIdentityService(new DefaultIdentityService());
            securityHandler.setLoginService(loginService);
        } else {  // This works
            securityHandler = new ConstraintSecurityHandler();
            securityHandler.addConstraintMapping(constraintMapping);
            HashLoginService loginService = new HashLoginService();
            //loginService.putUser("username", new Password("password"), new String[]{"user"});
            securityHandler.setLoginService(loginService);
        }
    jettyServer.start();
    }
}
