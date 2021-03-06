package uk.gov.defra.datareturns.data.model.nosep;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "md_nose_activity")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nose_activity_id_seq")
                  }
)
@Getter
@Setter
public class NoseActivity extends AbstractMasterDataEntity {

    @ManyToOne(optional = false)
    private NoseActivityClass noseActivityClass;

    @ManyToMany(mappedBy = "noseActivities")
    private Set<NoseProcess> noseProcesses = new HashSet<>();
}
