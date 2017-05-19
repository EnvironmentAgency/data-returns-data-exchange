package uk.gov.ea.datareturns.domain.validation.model.fields.impl.ds;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import uk.gov.ea.datareturns.domain.validation.model.DataSample;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

/**
 * Free-text comments about the row of data
 *
 * @author Sam Gardner-Dell
 */
public class Comments implements FieldValue<DataSample, String> {
    @Length(max = 255, message = MessageCodes.Length.Comments)
    private final String inputValue;

    /**
     * Instantiates a new Comments
     *
     * @param inputValue the input value
     */
    @JsonCreator
    public Comments(String inputValue) {
        this.inputValue = inputValue;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

    @Override public String getValue() {
        return inputValue;
    }

    @Override public String transform(DataSample record) {
        return StringUtils.trim(getValue());
    }
}