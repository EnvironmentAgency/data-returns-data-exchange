package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.exceptions.ProcessingException;
import uk.gov.ea.datareturns.domain.jpa.entities.Dependencies;
import uk.gov.ea.datareturns.domain.jpa.entities.DependenciesId;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.CacheProvider;
import uk.gov.ea.datareturns.domain.jpa.hierarchy.HierarchySymbols;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by graham on 03/10/16.
 */
@Repository
public class ParameterHierarchyDao extends CacheProvider<Map<String, Map<String, Map<String, Set<String>>>>> {

    @Inject
    private ParameterDao parameterDao;

    @Inject
    private ReturnTypeDao returnTypeDao;

    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    @Inject
    private UnitDao unitDao;

    public Dependencies getById(DependenciesId id) {
        return entityManager.find(Dependencies.class, id);
    }

    private volatile Map<String, Map<String, Map<String, Set<String>>>> cache = null;

    /*
     * Use to sort the results of the query, strictly unnecessary
     */
    private Comparator<Dependencies> groupByComparator = Comparator
            .comparing(Dependencies::getReturnType)
            .thenComparing(Dependencies::getReleasesAndTransfers)
            .thenComparing(Dependencies::getParameter)
            .thenComparing(Dependencies::getUnits);

    /**
     * Builds the dependencies cache.
     * It pretty much explains itself. Note the doublic check locking
     * on the cache and the volatile keyword
     */
    private Map<String, Map<String, Map<String, Set<String>>>> buildCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    LOGGER.info("Build name cache of: Dependencies");
                    cache = list().stream().collect(
                            Collectors.groupingBy(t -> returnTypeDao.getKeyFromRelaxedName(t.getReturnType()),
                                    Collectors.groupingBy(r -> releasesAndTransfersDao.getKeyFromRelaxedName(r.getReleasesAndTransfers()),
                                            Collectors.groupingBy(p -> parameterDao.getKeyFromRelaxedName(p.getParameter()),
                                                    Collectors.mapping(u -> unitDao.getKeyFromRelaxedName(u.getUnits()), Collectors.toCollection(HashSet::new))
                                            )
                                    )
                            )
                    );
                }
            }
        }

        return cache;
    }

    public Map<String, Map<String, Map<String, Set<String>>>> getCache() {
        return buildCache();
    }

    /**
     * List all the dependencies
     */
    public List<Dependencies> list() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dependencies> q = cb.createQuery(Dependencies.class);
        Root<Dependencies> c = q.from(Dependencies.class);
        q.select(c);
        TypedQuery<Dependencies> query = entityManager.createQuery(q);
        List<Dependencies> results = query.getResultList();
        results.sort(groupByComparator);
        return results;
    }

    /**
     * Test that all items in the dependencies table
     * can be found in the base tables
     * @return true is OK
     */
    @PostConstruct
    public void checkIntegrity() throws ProcessingException{
        boolean hasIntegrity = true;

        // Test parameters
        List<String> missingParameters = list()
            .stream()
                .map(Dependencies::getParameter)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL_OPTIONALLY))
                .filter(p -> !p.trim().equals(HierarchySymbols.NOT_APPLICABLE))
                .filter(p -> !parameterDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingParameters.size() != 0) {
            hasIntegrity = false;
            for(String name : missingParameters) {
                LOGGER.error("Dependent parameter not found: " + name);
            }
        }

        // Test return types
        List<String> missingReturnTypes = list()
                .stream()
                .map(Dependencies::getReturnType)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL_OPTIONALLY))
                .filter(p -> !p.trim().equals(HierarchySymbols.NOT_APPLICABLE))
                .filter(p -> !returnTypeDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingReturnTypes.size() != 0) {
            hasIntegrity = false;
            for(String name : missingReturnTypes) {
                LOGGER.error("Dependent return type not found: " + name);
            }
        }

        // Test releases and transfers
        List<String> missingReleasesAndTransfers = list()
                .stream()
                .map(Dependencies::getReleasesAndTransfers)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL_OPTIONALLY))
                .filter(p -> !p.trim().equals(HierarchySymbols.NOT_APPLICABLE))
                .filter(p -> !releasesAndTransfersDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingReleasesAndTransfers.size() != 0) {
            hasIntegrity = false;
            for(String name : missingReleasesAndTransfers) {
                LOGGER.error("Dependent releases and transfer type not found: " + name);
            }
        }

        // Test units
        List<String> missingUnits = list()
                .stream()
                .map(Dependencies::getUnits)
                .map(p -> removeExclusion(p))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE_ALL))
                .filter(p -> !p.trim().equals(HierarchySymbols.EXCLUDE))
                .filter(p -> !p.trim().equals(HierarchySymbols.INCLUDE_ALL_OPTIONALLY))
                .filter(p -> !p.trim().equals(HierarchySymbols.NOT_APPLICABLE))
                .filter(p -> !unitDao.nameExists(p))
                .collect(Collectors.toList());

        if (missingUnits.size() != 0) {
            hasIntegrity = false;
            for(String name : missingUnits) {
                LOGGER.error("Dependent units not found: " + name);
            }
        }

        if (!hasIntegrity) {
            throw new ProcessingException("Dependencies data has errors");
        }
    }
}