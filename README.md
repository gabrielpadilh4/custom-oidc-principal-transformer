# Custom OIDC principal transformer

## Overview

This project provides a custom OIDC principal transformer to allow the usage of custom values in addition to the default values of an OidcPrincipal.

The use case for this is to populate an OIDC principal with custom values.

This was tested with JBoss EAP 8 and Wildfly 28.

## Installation

To use this custom role mapper in your JBoss / Wildfly instance, follow these steps:

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/gabrielpadilh4/custom-oidc-principal-transformer.git
   ```

2. **Compile the project**
   ```bash
   mvn clean install
   ```
3. **Install the artifact as a module in JBoss / Wildfly**
   With the server stopped, add the module:
   ```bash
   $JBOSS_HOME/jboss-cli.sh
   module add --name=custom-oidc-principal-transformer --resources=<PROJECT_DIRECTORY>/target/custom-oidc-principal-transformer-1.0.0.jar --dependencies=org.wildfly.security.elytron,org.wildfly.security.elytron-http-oidc,org.wildfly.extension.elytron
   ```
4. **Configure the custom oidc principal transformer**
   ```bash
   /subsystem=elytron/custom-principal-transformer=CustomOidcPrincipalTransformer:add(class-name=org.example.CustomOidcPrincipalTransformer, module=custom-oidc-principal-transformer)
   ```

5. Configure an Elytron security domain that will use the Oidc security realm capability
   ```bash
   /subsystem=elytron/custom-realm=myOidcRealm:add(module=org.wildfly.security.elytron-http-oidc, class-name=org.wildfly.security.http.oidc.OidcSecurityRealm)
   /subsystem=elytron/security-domain=myDomain:add(default-realm=myOidcRealm, permission-mapper=default-permission-mapper, realms=[ {realm=myOidcRealm}])
   /subsystem=elytron/service-loader-http-server-mechanism-factory=myFactory:add(module=org.wildfly.security.elytron-http-oidc)
   /subsystem=elytron/http-authentication-factory=myMechanism:add(http-server-mechanism-factory=myFactory,security-domain=myDomain,mechanism-configurations=[ {mechanism-name=OIDC}])
   /subsystem=undertow/application-security-domain=myDomain:add(http-authentication-factory=myMechanism,override-deployment-config=true)
   ```

6. **Use the custom OIDC principal transformer in the security domain, it can be pre or post realm principal transformer**
   ```bash
   /subsystem=elytron/security-domain=myDomain:write-attribute(name=post-realm-principal-transformer,value=CustomOidcPrincipalTransformer)
   ```
## Use case

I have need to add more attribute other than the ones provided by the OidcPrincipal. To fill this attributes, i can use the principal transformer and add my custom attributes.

## Prerequite in the application

For this to work, you need to be using a oidc.json inside the application. As well, the following files need to be changed:

1. Remove the OIDC auth method from the web.xml and add the OIDC listener in the application
```bash
...
  <!-- 
  <login-config>
      <auth-method>OIDC</auth-method>
  </login-config>
  -->
...
  <listener>
      <listener-class>org.wildfly.security.http.oidc.OidcConfigurationServletListener</listener-class>
  </listener>
...
```

2. Modify the jboss-web.xml to use a security domain in the application. E.g: "myDomain"
```bash
<?xml version="1.0" encoding="UTF-8"?>
<jboss-web>
  <context-root>/</context-root>
  <security-domain>myDomain</security-domain>
</jboss-web>
```

3. Add a new file in the WEB-INF directory called jboss-deployment-structure.xml with the content in the application:
```bash
<jboss-deployment-structure>
  <deployment>
      <dependencies>
          <module name="org.wildfly.security.elytron-http-oidc"/>
      </dependencies>
  </deployment>
</jboss-deployment-structure>
```

With the above, the authentication request from the OIDC elytron client subsystem will be intercepted by the security domain. Once this happens, the capabilities of a security domain will be available.