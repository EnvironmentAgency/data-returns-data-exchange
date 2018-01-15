package uk.gov.defra.datareturns.data.model.referenceperiod;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;


/**
 * Spring REST repository for {@link ReferencePeriod} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface ReferencePeriodRepository extends MasterDataRepository<ReferencePeriod> {
}