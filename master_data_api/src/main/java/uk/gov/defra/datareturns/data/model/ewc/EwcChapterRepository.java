package uk.gov.defra.datareturns.data.model.ewc;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.model.MasterDataRepository;

/**
 * Spring REST repository for {@link EwcChapter} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface EwcChapterRepository extends MasterDataRepository<EwcChapter> {
}
