package org.javastro.ivoa.quarkus.uws;


import com.fasterxml.jackson.databind.jsontype.NamedType;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.persistence.EntityManager;
import org.javastro.ivoacore.uws.BaseUWSJob;
import org.javastro.ivoacore.uws.persist.DatabaseJobStore;

import java.util.List;

/**
 * A transactional database store for UWS jobs that ensures each job is stored in a new transaction.
 *
 * @author Paul Harrison (paul.harrison@manchester.ac.uk) */
public class TransactionalDatabaseStore extends DatabaseJobStore {

   /**
    * Standard Constructor.
    * @param entityManager the entity manager to use for database operations.
    * @param typeDetails the descriptions of the types of jobs in the store.
    */
    public TransactionalDatabaseStore(EntityManager entityManager, List<NamedType> typeDetails) {
        super(entityManager, typeDetails);
    }

    //TODO perhaps think about transaction control more carefully.
   @Override
   public void store(BaseUWSJob job) {
      QuarkusTransaction.requiringNew().call(() ->{ super.store(job);
      return 0;});
   }

   @Override
   public boolean delete(String id) {
      return QuarkusTransaction.requiringNew().call(() -> super.delete(id));
   }
}
