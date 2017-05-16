package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.constraints;

import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.constraints.impl.ValueTxtValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

/**
 * Created by graham on 11/05/17.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValueTxtValueValidator.class)
@Documented
public @interface ValueTxtValue {

    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
