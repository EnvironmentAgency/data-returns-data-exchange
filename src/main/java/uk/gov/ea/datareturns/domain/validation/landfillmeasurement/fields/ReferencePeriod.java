package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.ReferencePeriodDao;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.ReferencePeriodAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;

/**
 * The reference period for the sample describes how the sample was taken - eg, '24 hour total', 'Half hour average'
 *
 * @author Sam Gardner-Dell
 */
public class ReferencePeriod extends AbstractAliasingEntityValue<LandfillMeasurementMvo, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.ReferencePeriod> {
    private static final ReferencePeriodDao DAO = EntityDao.getDao(ReferencePeriodDao.class);
    @ControlledList(auditor = ReferencePeriodAuditor.class, message = MessageCodes.ControlledList.Ref_Period)
    private final String inputValue;

    /**
     * Instantiates a new ReferencePeriod
     *
     * @param inputValue the input value
     */
    public ReferencePeriod(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected ReferencePeriodDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
