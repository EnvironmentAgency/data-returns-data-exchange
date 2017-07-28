package uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.impl;

import org.apache.commons.lang3.StringUtils;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.*;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.AbstractPayloadEntityFactory;
import uk.gov.ea.datareturns.domain.jpa.dao.userdata.factories.TranslationResult;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DataSampleEntity;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.EaId;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.MonitoringDate;
import uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.TxtValue;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.DataSamplePayload;

import java.math.BigDecimal;

/**
 * @author Graham Willis
 * Boilerplate to generate instances of the hibernate persistence entity
 */
public class DataSampleFactory extends AbstractPayloadEntityFactory<DataSampleEntity, DataSamplePayload> {

    private final MethodOrStandardDao methodOrStandardDao;
    private final ParameterDao parameterDao;
    private final QualifierDao qualifierDao;
    private final ReferencePeriodDao referencePeriodDao;
    private final ReturnPeriodDao returnPeriodDao;
    private final ReturnTypeDao returnTypeDao;
    private final SiteDao siteDao;
    private final TextValueDao textValueDao;
    private final UniqueIdentifierAliasDao uniqueIdentifierAliasDao;
    private final UniqueIdentifierDao uniqueIdentifierDao;
    private final UnitDao unitDao;

    public DataSampleFactory(
            MethodOrStandardDao methodOrStandardDao,
            ParameterDao parameterDao,
            QualifierDao qualifierDao,
            ReferencePeriodDao referencePeriodDao,
            ReturnPeriodDao returnPeriodDao,
            ReturnTypeDao returnTypeDao,
            SiteDao siteDao,
            TextValueDao textValueDao,
            UniqueIdentifierAliasDao uniqueIdentifierAliasDao,
            UniqueIdentifierDao uniqueIdentifierDao,
            UnitDao unitDao
    ) {
        super(DataSamplePayload.class);

        this.methodOrStandardDao = methodOrStandardDao;
        this.parameterDao = parameterDao;
        this.qualifierDao = qualifierDao;
        this.referencePeriodDao = referencePeriodDao;
        this.returnTypeDao = returnTypeDao;
        this.returnPeriodDao = returnPeriodDao;
        this.siteDao = siteDao;
        this.textValueDao = textValueDao;
        this.uniqueIdentifierAliasDao = uniqueIdentifierAliasDao;
        this.uniqueIdentifierDao = uniqueIdentifierDao;
        this.unitDao = unitDao;
    }

    public TranslationResult<DataSampleEntity> create(DataSamplePayload payload) {
        // Set up an observation with teh entity objects and also logging any substitutions made
        TranslationResult<DataSampleEntity> result = new TranslationResult<>();

        DataSampleEntity dataSampleEntity = new DataSampleEntity();
        result.setEntity(dataSampleEntity);

        // Unique identifier or alias
        UniqueIdentifierAlias uniqueIdentifierAlias = uniqueIdentifierAliasDao.getByName(Key.relaxed(payload.getEaId()));
        if (uniqueIdentifierAlias != null) {
            dataSampleEntity.setUniqueIdentifier(uniqueIdentifierAlias.getUniqueIdentifier());
            result.addSubstitution(EaId.FIELD_NAME, uniqueIdentifierAlias.getName(),
                    uniqueIdentifierAlias.getUniqueIdentifier().getName());
        } else {
            dataSampleEntity.setUniqueIdentifier(uniqueIdentifierDao.getByNameOrAlias(Key.relaxed(payload.getEaId())));
        }

        // Return type
        dataSampleEntity.setReturnType(returnTypeDao.getByName(Key.relaxed((payload.getReturnType()))));

        // Parameter or alias
        Parameter parameter = parameterDao.getByAliasName(Key.relaxed(payload.getParameter()));
        if (parameter != null) {
            dataSampleEntity.setParameter(parameter);
            result.addSubstitution(
                    uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.Parameter.FIELD_NAME,
                    parameter.getName(), parameter.getPreferred());
        } else {
            dataSampleEntity.setParameter(parameterDao.getByName(Key.relaxed(payload.getParameter())));
        }

        // Monitoring point
        dataSampleEntity.setMonPoint(payload.getMonitoringPoint());

        // Monitoring date
        MonitoringDate monDate = new MonitoringDate(payload.getMonitoringDate());
        dataSampleEntity.setMonDate(monDate.getInstant());

        // Numeric value

        /*
        TODO: Discuss with Graham as to intention here....
         */
        if (StringUtils.isNotBlank(payload.getValue())) {
            String value = payload.getValue().trim();
            char firstChar = value.charAt(0);

            if (firstChar == '<' || firstChar == '>') {
                dataSampleEntity.setNumericValueText(String.valueOf(firstChar));
                // Get the remainder after the < or >
                value = value.substring(1);
            }
            try {
                dataSampleEntity.setNumericValue(new BigDecimal(value));
            } catch (NumberFormatException e) {
                dataSampleEntity.setNumericValue(null);
            }
        }

        // Text value or alias
        TextValue textValueAlias = textValueDao.getByAliasName(Key.relaxed(payload.getTextValue()));
        if (textValueAlias != null) {
            dataSampleEntity.setTextValue(textValueAlias);
            result.addSubstitution(TxtValue.FIELD_NAME,
                    textValueAlias.getName(), textValueAlias.getPreferred());
        } else {
            dataSampleEntity.setTextValue(textValueDao.getByName(Key.relaxed(payload.getTextValue())));
        }

        // Qualifier
        dataSampleEntity.setQualifier(qualifierDao.getByName(Key.relaxed((payload.getQualifier()))));

        // Comments
        dataSampleEntity.setComments(payload.getComments());

        // Units or alias
        Unit unitAlias = unitDao.getByAliasName(Key.relaxed((payload.getUnit())));
        if (unitAlias != null) {
            dataSampleEntity.setUnit(unitAlias);
            result.addSubstitution(
                    uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.Unit.FIELD_NAME,
                    unitAlias.getName(), unitAlias.getPreferred());
        } else {
            dataSampleEntity.setUnit(unitDao.getByName(Key.relaxed((payload.getUnit()))));
        }

        // Method or standard
        dataSampleEntity.setMethodOrStandard(methodOrStandardDao.getByName(Key.relaxed(payload.getMethStand())));

        // Reference period or alias
        ReferencePeriod referencePeriodAlias = referencePeriodDao
                .getByAliasName(Key.relaxed(payload.getReferencePeriod()));

        if (referencePeriodAlias != null) {
            dataSampleEntity.setReferencePeriod(referencePeriodAlias);
            result.addSubstitution(
                    uk.gov.ea.datareturns.domain.validation.payloads.datasample.fields.ReferencePeriod.FIELD_NAME,
                    referencePeriodAlias.getName(), referencePeriodAlias.getPreferred());
        } else {
            dataSampleEntity.setReferencePeriod(
                    referencePeriodDao.getByName(Key.relaxed(payload.getReferencePeriod())));
        }

        return result;
    }
}
