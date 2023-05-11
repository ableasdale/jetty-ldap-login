/*
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.util.security.Credential;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.security.sasl.AuthenticationException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LDAPLoginService {
    LoginService loginService = new AbstractLoginService() {

        private final InitialLdapContext _ldap = _getLdap(
                "cn=" + CONFIG.getString("ldap.manager") + "," + CONFIG.getString("ldap.baseDn"),
                CONFIG.getString("ldap.managerPassword"));

        @Override
        protected void finalize() throws Throwable {
            _ldap.close();
        }

        private InitialLdapContext _getLdap(String userDn, String password) {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.PROVIDER_URL, CONFIG.getString("ldap.server"));
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);//dn user password
            try {
                InitialLdapContext ldap = new InitialLdapContext(env, null);
                return ldap;
            } catch (AuthenticationException e) {
                return null;
            } catch (NamingException e) {
                return null;
            }
        }

        // Based on https://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java
        private String _escapeLDAPSearchFilter(String filter) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < filter.length(); i++) {
                char curChar = filter.charAt(i);
                switch (curChar) {
                    case '\\':
                        sb.append("\\5c");
                        break;
                    case '*':
                        sb.append("\\2a");
                        break;
                    case '(':
                        sb.append("\\28");
                        break;
                    case ')':
                        sb.append("\\29");
                        break;
                    case '\u0000':
                        sb.append("\\00");
                        break;
                    default:
                        sb.append(curChar);
                }
            }
            return sb.toString();
        }

        @Override
        protected String[] loadRoleInfo(AbstractLoginService.UserPrincipal user) {
            String groupBaseDn = CONFIG.getString("ldap.groupBaseDn") + "," + CONFIG.getString("ldap.baseDn");

            String search = CONFIG.getString("ldap.groupFilter");

            String userDn;
            if (CONFIG.getBoolean("ldap.usePosixGroups", true)) {
                userDn = user.getName();
            } else {
                userDn = "uid=" + user.getName() + "," + CONFIG.getString("ldap.userBaseDn") + "," + CONFIG.getString("ldap.baseDn"); // TODO: not sure in this, never tested
            }
            search = search + "(" + CONFIG.getString("ldap.groupMemberAttribute") + "=" + _escapeLDAPSearchFilter(userDn) + ")";

            search = "(&" + search + ")";

            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setTimeLimit(30000);

            NamingEnumeration<SearchResult> enumeration = null;

            ArrayList<String> roles = new ArrayList<>();
            try {
                enumeration = _ldap.search(groupBaseDn, search, searchControls);
                while(enumeration.hasMore()){
                    SearchResult result = enumeration.nextElement();
                    final Attributes attributes = result.getAttributes();
                    Attribute attribute = attributes.get(CONFIG.getString("ldap.groupIdAttribute"));
                    if (attribute != null) {
                        roles.add((String) attribute.get());
                    }
                }
            } catch (NamingException e) {

            } finally {
                if (enumeration != null) {
                    try {
                        enumeration.close();
                    } catch (NamingException ee) {

                    }
                }
            }

            String[] ret = new String[roles.size()];
            return roles.toArray(ret);

        }

        @Override
        protected List<RolePrincipal> loadRoleInfo(UserPrincipal user) {
            return null;
        }

        @Override
        protected AbstractLoginService.UserPrincipal loadUserInfo(String username) {

            final Credential credential = new Credential() {
                @Override
                public boolean check(Object credentials) {
                    InitialLdapContext myLdap = _getLdap(
                            "uid=" + username + "," + CONFIG.getString("ldap.userBaseDn") + "," + CONFIG.getString("ldap.baseDn"),
                            (String) credentials);
                    if (myLdap == null) {
                        return false;
                    } else {
                        try {
                            myLdap.close();
                        } catch (NamingException e) {
                            //okay...
                        }
                        return true;
                    }
                }
            };

            final LDAPLoginService.UserPrincipal webUser = new UserPrincipal(username, credential);

            return webUser;

        }
    };
}
*/