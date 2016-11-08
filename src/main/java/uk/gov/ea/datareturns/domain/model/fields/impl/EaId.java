package uk.gov.ea.datareturns.domain.model.fields.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.ea.datareturns.domain.jpa.entities.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.jpa.service.UniqueIdentifierService;
import uk.gov.ea.datareturns.domain.model.DataSample;
import uk.gov.ea.datareturns.domain.model.MessageCodes;
import uk.gov.ea.datareturns.domain.model.fields.AbstractEntityValue;
import uk.gov.ea.datareturns.domain.model.rules.EaIdType;
import uk.gov.ea.datareturns.domain.model.validation.auditors.controlledlist.UniqueIdentifierAuditor;
import uk.gov.ea.datareturns.domain.model.validation.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.util.SpringApplicationContextProvider;

/**
 * Models details about an EA Unique Identifier (EA_ID)
 *
 * @author Sam Gardner-Dell
 */
public class EaId extends AbstractEntityValue<DataSample, UniqueIdentifier> implements Comparable<EaId> {

    @NotBlank(message = MessageCodes.Missing.EA_ID)
    @ControlledList(auditor = UniqueIdentifierAuditor.class, message = MessageCodes.ControlledList.EA_ID)
    private String identifier;

    private EaIdType type;

    /**
     * Create a new {@link EaId} from the given identifier
     *
     * @param identifier the String representation of the unique identifier.
     */
    public EaId(final String identifier) {
        super();
        UniqueIdentifierService uniqueIdentifierService =
                SpringApplicationContextProvider.getApplicationContext().getBean(UniqueIdentifierService.class);
        UniqueIdentifier uniqueIdentifier = uniqueIdentifierService.getUniqueIdentifier(identifier);
        super.setEntity(uniqueIdentifier);
        this.identifier = identifier;
        if (getEntity() != null) {
            this.type = EaIdType.forUniqueId(getEntity().getName());
        }
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
    @JsonIgnore
    public boolean isNumeric() {
        return EaIdType.LOWER_NUMERIC.equals(this.type) || EaIdType.UPPER_NUMERIC.equals(this.type);
    }

    /**
     * Determine if the identifier is an alphanumeric type
     *
     * @return true if the identifier is alphanumeric, false if the identifier is numeric
     */
    @JsonIgnore
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