package uk.gov.ea.datareturns.domain.validation.datasample.constraints.validators;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleValidationObject;
import uk.gov.ea.datareturns.domain.validation.datasample.constraints.annotations.RequireUnitWithValue;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Graham Willis
 */
public class RequireUnitWithValueValidator implements ConstraintValidator<RequireUnitWithValue, DataSampleValidationObject> {
    @Override
    public void initialize(RequireUnitWithValue requireUnitWithValue) {

    }

    @Override
    public boolean isValid(DataSampleValidationObject dataSampleValidationObject, ConstraintValidatorContext constraintValidatorContext) {

        boolean hasValue = !StringUtils.isEmpty(dataSampleValidationObject.getValue().getValue());
        boolean hasUnit = (dataSampleValidationObject.getUnit().getEntity() != null);

        if (hasValue && !hasUnit) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("DR9050-Conflict").addConstraintViolation();
            return false;
        }
        return true;
    }
}
