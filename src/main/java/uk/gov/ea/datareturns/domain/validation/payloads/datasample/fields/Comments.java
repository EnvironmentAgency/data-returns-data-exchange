package uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields;

import org.hibernate.validator.constraints.Length;
import uk.gov.ea.datareturns.domain.validation.common.entityfields.FieldValue;

/**
 * Free-text comments about the row of data
 *
 * @author Sam Gardner-Dell
 */
public class Comments implements FieldValue<String> {

    public static final String FIELD_NAME = "Comments";

    @Length(max = 255, message = "DR9140-Length")
    private final String inputValue;

    /**
     * Instantiates a new Comments
     *
     * @param inputValue the input value
     */
    public Comments(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return inputValue;
    }
}