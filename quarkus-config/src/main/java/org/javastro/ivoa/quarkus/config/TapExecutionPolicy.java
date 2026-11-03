package org.javastro.ivoa.quarkus.config;


import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * A policy for controlling the execution of TAP queries.
 */
@ConfigMapping(prefix = "ivoa.tap.policy")
public interface TapExecutionPolicy {
    /**
     * Returns the maximum number of records that can be returned by a query.
     * @return the maximum number of records.
     */
    @WithDefault("1000000")
    long maxRec();
}
