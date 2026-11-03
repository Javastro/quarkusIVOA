package org.javastro.ivoa.quarkus.tap;


import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.javastro.ivoacore.tap.TapExecutionPolicy;

/**
 * configuration bean for TAP policy
 *
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) */
@Singleton
public class TapPolicyAdaptor implements TapExecutionPolicy  {

   @Inject
   org.javastro.ivoa.quarkus.config.UWSExecutionPolicy uwsExecutionPolicy;

   @Inject
   org.javastro.ivoa.quarkus.config.TapExecutionPolicy  tapExecutionPolicy;

   @Override
   public long getMaxRunTime() {
      return uwsExecutionPolicy.maxRunTime();
   }

   @Override
   public long getDefaultRunTime() {
      return uwsExecutionPolicy.defaultRunTime();
   }

   @Override
   public int getKillPeriod() {
      return  uwsExecutionPolicy.killPeriod();
   }

   @Override
   public long getDefaultLifetime() {
      return uwsExecutionPolicy.defaultLifetime();
   }

   @Override
   public long getMaxLifetime() {
      return  uwsExecutionPolicy.maxLifetime();
   }

   @Override
   public int getDestroyPeriod() {
      return  uwsExecutionPolicy.destroyPeriod();
   }

   @Override
   public int getMaxConcurrent() {
      return uwsExecutionPolicy.maxConcurrent();
   }

   @Override
   public long maxRec() {
      return tapExecutionPolicy.maxRec();
   }
}
