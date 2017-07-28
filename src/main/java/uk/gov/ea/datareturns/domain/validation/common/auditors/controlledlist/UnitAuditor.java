package uk.gov.ea.datareturns.domain.validation.common.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.UnitDao;
import uk.gov.ea.datareturns.domain.validation.common.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for unit values
 *
 * @author Sam Gardner-Dell
 */
@Component
public class UnitAuditor implements ControlledListAuditor {
    private final UnitDao unitDao;

    /**
     *
     */
    @Inject public UnitAuditor(UnitDao unitDao) {
        this.unitDao = unitDao;
    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.controlledlist.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.unitDao.nameOrAliasExists(Key.relaxed(Objects.toString(value, "")));
    }
}
