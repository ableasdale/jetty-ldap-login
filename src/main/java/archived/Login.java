package archived;

import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;

import java.util.Collection;
import java.util.List;

public class Login {

    public static void main(String[] args) {

        LoginService loginService = new AbstractLoginService() {
            @Override
            public String dumpSelf() {
                return super.dumpSelf();
            }

            @Override
            public boolean isDumpable(Object o) {
                return super.isDumpable(o);
            }

            @Override
            public <T> Collection<T> getCachedBeans(Class<T> clazz) {
                return super.getCachedBeans(clazz);
            }

            @Override
            protected List<RolePrincipal> loadRoleInfo(UserPrincipal user) {
                return null;
            }

            @Override
            protected UserPrincipal loadUserInfo(String username) {
                return null;
            }
        };
    }
}
