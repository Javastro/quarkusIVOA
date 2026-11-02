

package org.javastro.ivoa.quarkus.config;


import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Defines aspects of how an UWS job is managed and behaves.
 * @author Paul Harrison (paul.harrison@manchester.ac.uk)
 *
 * mirrors org.javastro.ivoacore.uws.environment.ExecutionPolicy for quarkus configuration purposes.
 */
@ConfigMapping(prefix = "ivoa.dc")
public interface UWSExecutionPolicy {

    /**
     * Returns the maximum run time for applications in seconds. A value of 0 implies unlimited run times;
     * @return the maximum run time in seconds, or 0 for unlimited.
     */
    @WithDefault("86400")
    int maxRunTime();

    /**
     * Returns the default run time for applications in seconds. A value of 0 implies unlimited run times;
     * @return the default run time in seconds, or 0 for unlimited.
     */
    @WithDefault("3600")
    int defaultRunTime();
    
    /**
     * Returns the period of the checks for overrunning applications.
     * How often the system checks for jobs that have exceeded their runtime limit.
     * @return the period in seconds
     */
    @WithDefault("60")
    int killPeriod();
    
    
    /**
     * Returns the default lifetime for a job record in seconds.
     * @return the default lifetime in seconds.
     */
    @WithDefault("604800")
    int defaultLifetime();

   /**
    * The maximum lifetime that a job record can exist for.
     * @return The maximum time in seconds.
     */
    @WithDefault("604800")
    int maxLifetime();

    /**
     * Returns the period of the checks for jobs that should be destroyed.
     * How often the system checks for jobs whose destruction time has passed and deletes them.
     * @return the period in seconds
     */
    @WithDefault("3600")
    int destroyPeriod();


    /**
     * The maximum number of jobs that can run concurrently
     * @return the maximum number of concurrently running jobs.
     */
    @WithDefault("10")
    int getMaxConcurrent();
}


