package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.validation.newmodel.rules.EaIdType;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.UniqueIdentifierAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;

/**
 * Models details about an EA Unique Identifier (EA_ID)
 *
 * @author Sam Gardner-Dell
 */
public class EaId extends AbstractEntityValue<UniqueIdentifierDao, UniqueIdentifier> implements Comparable<EaId> {
    private static final UniqueIdentifierDao DAO = EntityDao.getDao(UniqueIdentifierDao.class);

    @NotBlank(message = DataSampleFieldMessageMap.Missing.EA_ID)
    @ControlledList(auditor = UniqueIdentifierAuditorNew.class, message = DataSampleFieldMessageMap.ControlledList.EA_ID)
    private String identifier;

    private EaIdType type;

    /**
     * Create a new {@link EaId} from the given identifier
     *
     * @param identifier the String representation of the unique identifier.
     */
    public EaId(final String identifier) {
        super(identifier);
        this.identifier = identifier;
        if (getEntity() != null) {
            this.type = EaIdType.forUniqueId(getEntity().getName());
        }
    }

    protected UniqueIdentifier findEntity(String inputValue) {
        return getDao().getByNameOrAlias(Key.explicit(inputValue));
    }

    @Override protected UniqueIdentifierDao getDao() {
        return DAO;
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getInputValue() {
        return this.identifier;
    }

    /**
     * @return the type
     */
    public EaIdType getType() {
        return this.type;
    }

    /**
     * Determine if the identifier is a numeric type
     *
     * @return true if the identifier is numeric, false if the identifier is alphanumeric
     */
    public boolean isNumeric() {
        return EaIdType.LOWER_NUMERIC.equals(this.type) || EaIdType.UPPER_NUMERIC.equals(this.type);
    }

    /**
     * Determine if the identifier is an alphanumeric type
     *
     * @return true if the identifier is alphanumeric, false if the identifier is numeric
     */
    public boolean isAlphaNumeric() {
        return !isNumeric();
    }

    /**
     * Compare this {@link EaId} to the specified {@link EaId} to determine the natural sort order.
     *
     * Rules for sorting {@link EaId}s:
     * - Numeric Unique Identifiers appear before alphanumeric identifiers and are sorted by natural integer sort order
     * - Alphanumeric Unique identifiers appear next and are sorted using natural lexicographical sort order.
     *
     * @param o the {@link EaId} to compare to this instance
     * @return an {@link Integer} according to default Java compareTo rules.
     */
    @Override
    public int compareTo(final EaId o) {
        if (isNumeric() && o.isNumeric()) {
            // Numeric comparison
            final Long thisId = NumberUtils.toLong(this.identifier);
            final Long otherId = NumberUtils.toLong(o.identifier);
            return thisId.compareTo(otherId);
        } else if (isNumeric() && o.isAlphaNumeric()) {
            return -1;
        } else if (isAlphaNumeric() && o.isNumeric()) {
            return 1;
        }
        // Default alpha comparison
        return this.identifier.compareTo(o.identifier);
    }

    /**
     * Determine if two {@link EaId}s are equal.  This method checks the identifier value only.
     *
     * @param o the {@link EaId} to check equality against
     * @return true if the two identifiers are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        EaId eaId = (EaId) o;

        if (getEntity() != null) {
            return getEntity().equals(eaId.getEntity());
        } else {
            return identifier != null ? identifier.equals(eaId.identifier) : eaId.identifier == null;
        }
    }

    /**
     * Generate a hashcode based on the identifier value
     *
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        if (getEntity() != null) {
            return getEntity().getName().hashCode();
        } else {
            return identifier != null ? identifier.hashCode() : 0;
        }
    }

}