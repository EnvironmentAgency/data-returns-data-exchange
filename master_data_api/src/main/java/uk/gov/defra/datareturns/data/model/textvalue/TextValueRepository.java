package uk.gov.defra.datareturns.data.model.textvalue;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link TextValue} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface TextValueRepository extends MasterDataRepository<TextValue> {
}
