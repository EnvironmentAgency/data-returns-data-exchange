package uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist;

import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.Key;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReleasesAndTransfersDao;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledListAuditor;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Controlled list auditor for Releases and transfers.
 *
 * @author Sam Gardner-Dell
 */
@Component
public class ReleasesAndTransfersAuditorNew implements ControlledListAuditor {
    @Inject
    private ReleasesAndTransfersDao releasesAndTransfersDao;

    /**
     *
     */
    public ReleasesAndTransfersAuditorNew() {

    }

    /* (non-Javadoc)
     * @see uk.gov.ea.datareturns.domain.validation.model.validation.entityfields.ControlledListAuditor#isValid(java.lang.Object)
     */
    @Override
    public boolean isValid(final Object value) {
        return this.releasesAndTransfersDao.nameExists(Key.relaxed(Objects.toString(value, "")));
    }
}