package uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.TextValueDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.TextValue;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.LandfillMeasurementMvo;
import uk.gov.ea.datareturns.domain.validation.model.MessageCodes;
import uk.gov.ea.datareturns.domain.validation.model.fields.AbstractAliasingEntityValue;
import uk.gov.ea.datareturns.domain.validation.model.validation.auditors.controlledlist.TxtValueAuditor;
import uk.gov.ea.datareturns.domain.validation.model.validation.constraints.controlledlist.ControlledList;

/**
 * Models measurements/observations returned as text such as true, false, yes and no
 *
 * @author Sam Gardner-Dell
 */
public class TxtValue extends AbstractAliasingEntityValue<LandfillMeasurementMvo, TextValue> {
    private static final TextValueDao DAO = EntityDao.getDao(TextValueDao.class);
    @ControlledList(auditor = TxtValueAuditor.class, message = MessageCodes.ControlledList.Txt_Value)
    private final String inputValue;

    /**
     * Instantiates a new Txt_Value.
     *
     * @param inputValue the input value
     */
    public TxtValue(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected TextValueDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }
}
