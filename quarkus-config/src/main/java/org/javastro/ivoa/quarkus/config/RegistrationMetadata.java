package org.javastro.ivoa.quarkus.config;

import io.smallrye.config.ConfigMapping;

/**
 * The metadata that is needed to register a service with the IVOA registry.
 * This interface defines the necessary information required for service registration, including details such as service name, description, contact information, and any other relevant metadata that may be required by the IVOA registry standards.
 *
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) */
@ConfigMapping(prefix = "ivoa.dc")
public interface RegistrationMetadata {


   /**
    * the Authority under which the resource is being published.
    * @return the authority part of the ivoa identifier, which is typically a domain name or a unique string that represents the organization or entity responsible for the resource. This value is used to construct the full IVOA identifier for the resource being registered.
    */
   String authority();

   /**
    * The name of the resource.
    * @return
    */
   String name();

   /**
    * a human readable description of the resouce.
    * @return
    */
   String description();


   /**
    * Human readable title of the resource.
    * @return
    */
   String title();


   /**
    * The organisation
    * @return
    */
   String organisationName();

   /**
    * contact name for the resource.
    * @return
    */
   String contactName();

   /**
    * contact email.
    * @return
    */
   String contactEmail();

   /**
    * contact telephone number.
    * @return
    */
   String contactTelephone();

   /**
    * A reference URL to find more information about the resource.
    * @return
    */
   String referenceUrl();

}
