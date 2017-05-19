package uk.gov.ea.datareturns.domain.validation.datasample.constraints.record;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifier;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleMvo;
import uk.gov.ea.datareturns.domain.validation.datasample.fields.EaId;
import uk.gov.ea.datareturns.domain.validation.datasample.fields.SiteName;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.record.RecordConstraintValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * Created by sam on 11/11/16.
 */
public class SiteAndUniqueIdentifierValidatorNew implements RecordConstraintValidator<DataSampleMvo> {
    @Override
    public boolean isValid(DataSampleMvo record, final ConstraintValidatorContext context) {
        final EaId eaId = record.getEaId();
        final UniqueIdentifier eaIdEntity = eaId.getEntity();
        final SiteName site = record.getSiteName();

        // If we have no site name we need to just OK it. The site name validation
        // checks for not-blank so we don't want to report this as well
        // This gives an unfortunate dependency between the two validations
        // which is unavoidable at this stage. It should really check against the controlled list
        // but cannot do so while being case-insensitive. (There are collisions)
        if (site.getInputValue() == null) {
            return true;
        }

        if (eaIdEntity != null && !StringUtils.equalsIgnoreCase(eaId.getEntity().getSite().getName(), site.getValue())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(DataSampleFieldMessageMap.Conflict.UniqueIdentifierSiteConflict).addConstraintViolation();
            return false;
        }
        return true;
    }
}