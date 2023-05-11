package archived;

import org.eclipse.jetty.jaas.JAASLoginService;
import org.eclipse.jetty.security.DefaultIdentityService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

public class TryThisNext {
    public static void main(String[] args) {
        Server server = new Server(8080);

        //2. Creating the WebAppContext for the created content
        WebAppContext ctx = new WebAppContext();
        ctx.setResourceBase("src/main/resources/webapp");

        //3. Creating the LoginService for the realm
        JAASLoginService loginService = new JAASLoginService("ldaploginmodule");
        //HashLoginService loginService = new HashLoginService("JCGRealm");

        //4. Setting the realm configuration there the users, passwords and roles reside
       //JAASLoginService loginService = new JAASLoginService("ldaploginmodule");
        Configuration configuration = new Configuration() {
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
                return new AppConfigurationEntry[0];
            }
        };
        loginService.setConfiguration(configuration);
        //loginService.setIdentityService(new DefaultIdentityService());
        //securityHandler.setLoginService(loginService);
        //loginService.setConfig("jcgrealm.txt");

        //5. Appending the loginService to the Server
        server.addBean(loginService);

        //6. Setting the handler
        server.setHandler(ctx);

        //7. Starting the Server
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
