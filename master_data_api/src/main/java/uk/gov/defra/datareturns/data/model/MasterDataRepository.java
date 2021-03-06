package uk.gov.defra.datareturns.data.model;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import uk.gov.defra.datareturns.data.BaseRepository;

import java.util.List;

/**
 * Common repository definition for master data entities
 *
 * @param <E> the type of the entity
 * @author Sam Gardner-Dell
 */
@NoRepositoryBean
public interface MasterDataRepository<E extends MasterDataEntity> extends BaseRepository<E, Long> {
    /**
     * Retrieve a master data entity by its code
     *
     * @param nomenclature the primary term/name for the entity to retrieve
     * @return The entity E or null
     */
    @SuppressWarnings("unused")
    E getByNomenclature(@Param("nomenclature") String nomenclature);

    // FIXME Security needs further work..
    @Override
    @PreAuthorize("hasRole('ADMIN')")
    <S extends E> S save(S entity);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    <S extends E> List<S> save(Iterable<S> entities);

    // TODO: Spring boot 2:
    // <S extends E> List<S> saveAll(Iterable<S> entities);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void deleteInBatch(Iterable<E> entities);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void deleteAllInBatch();

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void delete(Long id);
    // TODO: Spring boot 2:
    // void deleteById(Long id);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void delete(E entity);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void delete(Iterable<? extends E> entities);
    // TODO: Spring boot 2:
    // void deleteAll(Iterable<? extends E> entities);

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    void deleteAll();
}
