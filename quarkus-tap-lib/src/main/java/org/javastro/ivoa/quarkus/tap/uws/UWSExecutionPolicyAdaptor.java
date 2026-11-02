package org.javastro.ivoa.quarkus.tap.uws;


import jakarta.inject.Inject;


public class UWSExecutionPolicyAdaptor implements org.javastro.ivoacore.uws.environment.ExecutionPolicy{

   @Inject
   org.javastro.ivoa.quarkus.config.UWSExecutionPolicy uwsExecutionPolicy;

   @Override
   public int getMaxRunTime() {
      return uwsExecutionPolicy.maxRunTime();
   }

   @Override
   public int getDefaultRunTime() {
      return uwsExecutionPolicy.defaultRunTime();
   }

   @Override
   public int getKillPeriod() {
      return  uwsExecutionPolicy.killPeriod();
   }

   @Override
   public int getDefaultLifetime() {
      return uwsExecutionPolicy.defaultLifetime();
   }

   @Override
   public int getMaxLifetime() {
      return  uwsExecutionPolicy.maxLifetime();
   }

   @Override
   public int getDestroyPeriod() {
      return  uwsExecutionPolicy.destroyPeriod();
   }

   @Override
   public int getMaxConcurrent() {
      return uwsExecutionPolicy.getMaxConcurrent();
   }
}
