package uk.gov.defra.datareturns.data.model.referenceperiod;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.AliasingEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The persistent class for reference_period aliases.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_reference_period_alias")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(
                                  name = SequenceStyleGenerator.SEQUENCE_PARAM,
                                  value = "md_reference_period_alias_id_seq"
                          )
                  }
)
@Getter
@Setter
public class ReferencePeriodAlias extends AbstractMasterDataEntity implements MasterDataEntity, AliasingEntity<ReferencePeriod> {
    @ManyToOne
    @JoinColumn(name = "preferred", nullable = false)
    private ReferencePeriod preferred;
}
