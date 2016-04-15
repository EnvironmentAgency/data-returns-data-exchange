package uk.gov.ea.datareturns.jpa.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import uk.gov.ea.datareturns.jpa.entities.UniqueIdentifier;

public class UniqueIdentifierDao extends AbstractJpaDao {
	private static final UniqueIdentifierDao INSTANCE = new UniqueIdentifierDao();

	public static UniqueIdentifierDao getInstance() {
		return INSTANCE;
	}

	/**
	 * Check if a specific identifier exists in the controlled list.
	 * 
	 * @param identifier the identifier to check
	 * @return true if the identifier exists, false otherwise.
	 */
	public boolean identfierExists(String identifier) {
		return findIdentifiers().contains(identifier);
	}
	

	/**
	 * Get a set of identifiers from the database
	 *
	 * @return a {@link UniqueIdentifier} for the given identifier or null if not found.
	 */
	public Set<String> findIdentifiers() {
		return new HashSet<String>(stringColumnQuery("UniqueIdentifier.findAllIdentifiers"));
	}
	

	/**
	 * Get a {@link UniqueIdentifier} instance for the given identifier
	 *
	 * @param identifier the unique identifier reference for the  {@link UniqueIdentifier} instance to retrieve
	 * @return a {@link UniqueIdentifier} for the given identifier or null if not found.
	 */
	public UniqueIdentifier forIdentifier(final String identifier) {
		UniqueIdentifier value = null;
		final EntityManager em = createEntityManager();
		try {
			final TypedQuery<UniqueIdentifier> query = em.createNamedQuery("UniqueIdentifier.findByIdentifier", UniqueIdentifier.class);
			query.setParameter("identifier", identifier);
			final List<UniqueIdentifier> results = query.getResultList();
			if (!results.isEmpty()) {
				value = results.get(0);
			}
		} finally {
			em.close();
		}
		return value;
	}
}
