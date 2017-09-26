package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * The reference period for the sample describes how the sample was taken - eg, '24 hour total', 'Half hour average'
 *
 * @author Sam Gardner-Dell
 */
public class ReferencePeriod implements FieldValue<uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReferencePeriod> {
    public static final String FIELD_NAME = "Ref_Period";
    @ControlledList(entities = uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReferencePeriod.class, message = "DR9090-Incorrect")
    private final String inputValue;

    /**
     * Instantiates a new ReferencePeriod
     *
     * @param inputValue the input value
     */
    public ReferencePeriod(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
