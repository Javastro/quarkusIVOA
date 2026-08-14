package org.javastro.ivoa.quarkus.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * This interface represents the configuration for the SRCNet services.
 * Note that the defaults are what is expected in the SRCNet development integration test environment.
 *
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) */
@ConfigMapping(prefix = "srcnet")
public interface SRCNet {

   /**
    * The SRC IAM service.
    * @return
    */
   @WithDefault("iam.test")
   String IAMServiceURL();

   /**
    * The Permissions API.
    * @return
    */
   @WithDefault("papi.test")
   String PermissionsServiceURL();

}
