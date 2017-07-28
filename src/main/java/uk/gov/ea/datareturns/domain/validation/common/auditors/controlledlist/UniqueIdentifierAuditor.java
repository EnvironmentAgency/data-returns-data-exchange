package uk.gov.ea.datareturns.domain.validation.common.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UniqueIdentifierDao;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list value for unique identifiers
 *
 * @author Sam Gardner-Dell
 */
@Component
public class UniqueIdentifierAuditor implements ControlledListAuditor {
    final
    UniqueIdentifierDao uniqueIdentifierDao;

    @Inject public UniqueIdentifierAuditor(UniqueIdentifierDao uniqueIdentifierDao) {
        this.uniqueIdentifierDao = uniqueIdentifierDao;
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.uniqueIdentifierDao.uniqueIdentifierExists(Objects.toString(value, ""));
    }
}
