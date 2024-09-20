package org.example;

import java.security.Principal;

import org.wildfly.extension.elytron.capabilities.PrincipalTransformer;
import org.wildfly.security.http.oidc.IDToken;
import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

public class CustomOidcPrincipalTransformer implements PrincipalTransformer {

    @Override
    public Principal apply(Principal originalPrincipal) {
        
        if (originalPrincipal instanceof OidcPrincipal) {
            IDToken oidcToken = ((OidcPrincipal<?>) originalPrincipal).getOidcSecurityContext().getIDToken();
            OidcSecurityContext oidcSecContext = ((OidcPrincipal<?>) originalPrincipal).getOidcSecurityContext();

            CustomOidcPrincipal customOidcPrincipal = new CustomOidcPrincipal(oidcToken.getPreferredUsername(), oidcSecContext);
            customOidcPrincipal.setMyProperty("Custom value of my property");

            System.out.println("This is my property: " + customOidcPrincipal.getMyProperty());

            return customOidcPrincipal;
        }
         
         return originalPrincipal;
    }
}
