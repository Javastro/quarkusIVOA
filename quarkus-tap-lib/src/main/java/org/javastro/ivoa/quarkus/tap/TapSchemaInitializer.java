package org.javastro.ivoa.quarkus.tap;


import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.javastro.ivoacore.tap.schema.SchemaProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Created on 28/08/2025 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */
@ApplicationScoped
@io.quarkus.arc.Unremovable
public class TapSchemaInitializer {
   private static final Logger log = LoggerFactory.getLogger(TapSchemaInitializer.class);
   @PersistenceContext
   EntityManager em;

   @Inject
   SchemaProvider schemaProvider;

   @Transactional
   void onStart(@Observes StartupEvent ev) {
       log.info("Checking TAP schema");
       Long i = em.createQuery("select count(o) from schemas o", Long.class).getSingleResult();
       if(i == 0) {
           log.info("Populating tap schema");
           for(var s: schemaProvider.getSchemasForDB()) {
               log.info("adding "+s.getSchema_name());
               em.persist(s);
           }

       }
   }

}
