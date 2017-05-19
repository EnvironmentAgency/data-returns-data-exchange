package uk.gov.ea.datareturns.domain.validation.basicmeasurement;

import uk.gov.ea.datareturns.domain.validation.newmodel.validator.FieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.basicmeasurement.fields.*;

/**
 * The field mapping for basic measurements
 */
public class BasicMeasurementFieldMessageMap extends FieldMessageMap<BasicMeasurementMvo> {

    public class Incorrect {
        public final static String Value = "{BM9040-Incorrect}";
    }

    public class Missing {
        public final static String Parameter = "{BM9030-Missing}";
    }

    public class ControlledList {
        public final static String Parameter = "{BM9030-Incorrect}";
    }

    public BasicMeasurementFieldMessageMap() {
        super(BasicMeasurementMvo.class);
        /*
         * Add the (atomic) length errors to the map
         */
        add(BasicMeasurementFieldMessageMap.Incorrect.Value, Value.class);
        add(BasicMeasurementFieldMessageMap.Missing.Parameter, Parameter.class);
        add(BasicMeasurementFieldMessageMap.ControlledList.Parameter, Parameter.class);
    }
}