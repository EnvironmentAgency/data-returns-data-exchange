package uk.gov.ea.datareturns.domain.validation.datasample.fields;

import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.EntityDao;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.MethodOrStandardDao;
import uk.gov.ea.datareturns.domain.validation.datasample.DataSampleFieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.newmodel.auditors.controlledlist.MethodOrStandardAuditorNew;
import uk.gov.ea.datareturns.domain.validation.newmodel.constraints.controlledlist.ControlledList;
import uk.gov.ea.datareturns.domain.validation.newmodel.entityfields.AbstractEntityValue;

/**
 * The method or standard used for monitoring
 *
 * @author Sam Gardner-Dell
 */
public class MethodOrStandard
        extends AbstractEntityValue<MethodOrStandardDao, uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.MethodOrStandard> {
    private static final MethodOrStandardDao DAO = EntityDao.getDao(MethodOrStandardDao.class);

    @ControlledList(auditor = MethodOrStandardAuditorNew.class, message = DataSampleFieldMessageMap.ControlledList.MethodOrStandard)
    private final String inputValue;

    /**
     * Instantiates a new MethodOrStandard
     *
     * @param inputValue the input value
     */
    public MethodOrStandard(String inputValue) {
        super(inputValue);
        this.inputValue = inputValue;
    }

    @Override protected MethodOrStandardDao getDao() {
        return DAO;
    }

    @Override public String getInputValue() {
        return inputValue;
    }

}