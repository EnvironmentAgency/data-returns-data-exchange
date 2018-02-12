package uk.gov.defra.datareturns.data.model.returntype;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the md_return_type_group database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_return_type_group")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_return_type_group_id_seq")}
)
@Getter
@Setter
public class ReturnTypeGroup extends AbstractMasterDataEntity implements MasterDataEntity {
    @ManyToMany
    @JoinTable(name = "md_return_type_group_entries")
    @Setter(AccessLevel.NONE)
    private Set<ReturnType> returnTypes = new HashSet<>();
}
