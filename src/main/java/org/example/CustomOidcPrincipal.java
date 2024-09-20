package org.example;

import org.wildfly.security.http.oidc.OidcPrincipal;
import org.wildfly.security.http.oidc.OidcSecurityContext;

public class CustomOidcPrincipal extends OidcPrincipal {

    private String myProperty;

    public CustomOidcPrincipal(String name, OidcSecurityContext context) {
        super(name, context);
    }

    public void setMyProperty(String myProperty) {
        this.myProperty = myProperty;
    }

    public String getMyProperty() {
        return this.myProperty;
    }
    
}
