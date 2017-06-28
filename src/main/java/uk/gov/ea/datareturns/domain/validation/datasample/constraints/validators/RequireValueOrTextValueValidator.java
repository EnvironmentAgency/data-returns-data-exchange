package uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations.ProhibitTxtValueWithValue;
import uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations.RequireValueOrTxtValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by graham on 11/05/17.
 */
public class RequireValueOrTextValueValidator implements ConstraintValidator<RequireValueOrTxtValue, DataSampleValidationObject> {

    @Override
    public void initialize(RequireValueOrTxtValue prohibitTxtValueWithValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {

        boolean hasTxtValue = (dataSampleValidationObject.getTextValue().getEntity() != null);
        boolean hasValue = !StringUtils.isEmpty(dataSampleValidationObject.getValue().getValue());

        if (!hasValue && !hasTxtValue) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9999-Missing").addConstraintViolation();
            return false;
        }

        return true;
    }
}
