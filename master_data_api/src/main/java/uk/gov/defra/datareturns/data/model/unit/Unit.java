package uk.gov.defra.datareturns.data.model.unit;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractAliasedEntity;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AliasedEntity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * The persistent class for the units database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_unit")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_unit_id_seq")}
)
@Getter
@Setter
public class Unit extends AbstractAliasedEntity<UnitAlias> implements AliasedEntity<UnitAlias> {
    @Basic
    @Column(name = "long_name", length = 50)
    private String longName;

    @Basic
    @Column(name = "unicode", length = 50)
    private String unicode;

    @Basic
    @Column(name = "description", length = 200)
    private String description;

    @ManyToOne(optional = false)
    private UnitType type;
}