package org.wildfly.swarm.examples.jaxrs.health;

import java.util.Properties;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.container.Container;
import org.wildfly.swarm.jaxrs.JAXRSArchive;
import org.wildfly.swarm.logging.LoggingFraction;
import org.wildfly.swarm.management.ManagementFraction;

/**
 * @author Lance Ball
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Container container = new Container();

        JAXRSArchive archive = ShrinkWrap.create(JAXRSArchive .class, "healthcheck-app.war");
        JAXRSArchive deployment = archive.as(JAXRSArchive.class).addPackage(Main.class.getPackage());
        deployment.addResource(HealthCheckResource.class);
        deployment.addResource(RegularResource.class);

        deployment.addAllDependencies();
        container
                .fraction(LoggingFraction.createDefaultLoggingFraction())
                .fraction(new ManagementFraction()
                                  .securityRealm("ManagementRealm", (realm) -> {
                                      realm.inMemoryAuthentication((authn) -> {
                                          authn.add(new Properties() {{
                                              put("admin", "password");
                                          }}, true);
                                      });
                                      realm.inMemoryAuthorization();
                                  }))
                .start()
                .deploy(deployment);
    }
}
