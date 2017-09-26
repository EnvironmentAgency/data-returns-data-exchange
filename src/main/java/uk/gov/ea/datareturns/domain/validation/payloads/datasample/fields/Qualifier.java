package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * Qualifies a measurement with additional information to better define the properties of measurement.  E.g. dry weight, wet weight
 *
 * @author Sam Gardner-Dell
 */
public class Qualifier implements FieldValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Qualifier> {
    public static final String FIELD_NAME = "Qualifier";

    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Qualifier.class, message = "DR9180-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new Qualifier.
     *
     * @param inputValue the input value
     */
    public Qualifier(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
