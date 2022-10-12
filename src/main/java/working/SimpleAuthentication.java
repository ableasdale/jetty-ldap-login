package working;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.UserStore;
import org.eclipse.jetty.security.authentication.FormAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;

/***
 * This example shows the basic authentication flow
 * Run, then go to localhost:8080 and enter "a" and "b" and submit the form (see line 59)
 */
import java.io.IOException;

public class SimpleAuthentication {
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
        constraint.setRoles(new String[]{"user","admin","moderator"});
        constraint.setAuthenticate(true);

        ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");

        ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
        securityHandler.addConstraintMapping(constraintMapping);
        HashLoginService loginService = new HashLoginService();
        UserStore userStore = new UserStore();
        userStore.addUser("a", new Password("b"), new String[]{"user"});
        loginService.setUserStore(userStore);
        // loginService.putUser("username", new Password("password"), new String[] {"user"});
        securityHandler.setLoginService(loginService);

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
