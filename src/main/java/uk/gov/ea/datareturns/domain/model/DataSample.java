package uk.gov.ea.datareturns.domain.model;

import uk.gov.ea.datareturns.domain.model.fields.MappedField;
import uk.gov.ea.datareturns.domain.model.fields.impl.*;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;
import uk.gov.ea.datareturns.domain.model.validation.ValidationGroups;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.HierarchyValidator;
import uk.gov.ea.datareturns.domain.model.validation.constraints.factory.ValidRecord;

import javax.validation.Valid;

/**
 * Represents an individual sample (data return) record entry
 *
 * @author Sam Gardner-Dell
 */
@ValidRecord(value = DataSample.class)
@HierarchyValidator(value = DataSample.class, groups = ValidationGroups.OrderedChecks.class)
public class DataSample {

    /** The EA Unique Identifier (EA_ID) */
    @Valid @MappedField(FieldDefinition.EA_ID)
    private EaId eaId;

    /** The site name (Site_Name) */
    @Valid @MappedField(FieldDefinition.Site_Name)
    private SiteName siteName;

    /** The return type (Rtn_Type) */
    @Valid @MappedField(FieldDefinition.Rtn_Type)
    private ReturnType returnType;

    /** The monitoring date (Mon_Date) */
    @Valid @MappedField(FieldDefinition.Mon_Date)
    private MonitoringDate monitoringDate;

    /** The return period  (Rtn_Period) */
    @Valid @MappedField(FieldDefinition.Rtn_Period)
    private ReturnPeriod returnPeriod;

    /** The monitoring point (Mon_Point) */
    @Valid @MappedField(FieldDefinition.Mon_Point)
    private MonitoringPoint monitoringPoint;

    /** Parameter value (Parameter) */
    @Valid @MappedField(FieldDefinition.Parameter)
    private Parameter parameter;

    /** Value (Value) */
    @Valid @MappedField(FieldDefinition.Value)
    private Value value;

    /** Textual value (Txt_Value) */
    @Valid @MappedField(FieldDefinition.Txt_Value)
    private TxtValue textValue;

    /** Qualifier value (Qualifier) */
    @Valid @MappedField(FieldDefinition.Qualifier)
    private Qualifier qualifier;

    /** Unit of measurement (Unit) */
    @Valid @MappedField(FieldDefinition.Unit)
    private Unit unit;

    /** Reference period */
    @Valid @MappedField(FieldDefinition.Ref_Period)
    private ReferencePeriod referencePeriod;

    /** Method or standard used (Meth_Stand) */
    @Valid @MappedField(FieldDefinition.Meth_Stand)
    private MethodOrStandard methStand;

    /** Record comments (Comments) */
    @Valid @MappedField(FieldDefinition.Comments)
    private Comments comments;

    /** Commercial in confidence data (CiC) */
    @Valid @MappedField(FieldDefinition.CiC)
    private Cic cic;

    /** Pollution inventory releases and transfers */
    @Valid @MappedField(FieldDefinition.Rel_Trans)
    private ReleasesAndTransfers releasesAndTransfers;


    //    /** Sample reference (Smpl_Ref) */
    //    @Length(max = 255, message = "{DR9120-Length}")
    //    @Pattern(regexp = FieldValue.REGEX_SIMPLE_TEXT, message = "{DR9120-Incorrect}")
    //    @Valid @MappedField(FieldDefinition.Smpl_Ref)
    //    private String sampleReference;
    //
    //    /** Sampled by (Smpl_By) */
    //    @Length(max = 255, message = "{DR9130-Length}")
    //    @Valid @MappedField(FieldDefinition.Smpl_By)
    //    private String sampleBy;

    //    /** Chemical Abstracts Service value (CAS) */
    //    @Length(max = 255, message = "{DR9160-Length}")
    //    @Valid @MappedField(FieldDefinition.CAS)
    //    private String cas;
    //`
    //    /** Recovery and disposal code (RD_Code) */
    //    @Length(max = 255, message = "{DR9170-Length}")
    //    @Valid @MappedField(FieldDefinition.RD_Code)
    //    private String rdCode;

    /**
     * Default constructor
     */
    public DataSample() {
        super();
    }

    /**
     * Gets ea id.
     *
     * @return the ea id
     */
    public EaId getEaId() {
        return eaId;
    }

    /**
     * Sets ea id.
     *
     * @param eaId the ea id
     */
    public void setEaId(EaId eaId) {
        this.eaId = eaId;
    }

    /**
     * Gets site name.
     *
     * @return the site name
     */
    public SiteName getSiteName() {
        return siteName;
    }

    /**
     * Sets site name.
     *
     * @param siteName the site name
     */
    public void setSiteName(SiteName siteName) {
        this.siteName = siteName;
    }

    /**
     * Gets return type.
     *
     * @return the return type
     */
    public ReturnType getReturnType() {
        return returnType;
    }

    /**
     * Sets return type.
     *
     * @param returnType the return type
     */
    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    /**
     * Gets monitoring date.
     *
     * @return the monitoring date
     */
    public MonitoringDate getMonitoringDate() {
        return monitoringDate;
    }

    /**
     * Sets monitoring date.
     *
     * @param monitoringDate the monitoring date
     */
    public void setMonitoringDate(MonitoringDate monitoringDate) {
        this.monitoringDate = monitoringDate;
    }

    /**
     * Gets return period.
     *
     * @return the return period
     */
    public ReturnPeriod getReturnPeriod() {
        return returnPeriod;
    }

    /**
     * Sets return period.
     *
     * @param returnPeriod the return period
     */
    public void setReturnPeriod(ReturnPeriod returnPeriod) {
        this.returnPeriod = returnPeriod;
    }

    /**
     * Gets monitoring point.
     *
     * @return the monitoring point
     */
    public MonitoringPoint getMonitoringPoint() {
        return monitoringPoint;
    }

    /**
     * Sets monitoring point.
     *
     * @param monitoringPoint the monitoring point
     */
    public void setMonitoringPoint(MonitoringPoint monitoringPoint) {
        this.monitoringPoint = monitoringPoint;
    }

    /**
     * Gets parameter.
     *
     * @return the parameter
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * Sets parameter.
     *
     * @param parameter the parameter
     */
    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public Value getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     */
    public void setValue(Value value) {
        this.value = value;
    }

    /**
     * Gets text value.
     *
     * @return the text value
     */
    public TxtValue getTextValue() {
        return textValue;
    }

    /**
     * Sets text value.
     *
     * @param textValue the text value
     */
    public void setTextValue(TxtValue textValue) {
        this.textValue = textValue;
    }

    /**
     * Gets qualifier.
     *
     * @return the qualifier
     */
    public Qualifier getQualifier() {
        return qualifier;
    }

    /**
     * Sets qualifier.
     *
     * @param qualifier the qualifier
     */
    public void setQualifier(Qualifier qualifier) {
        this.qualifier = qualifier;
    }

    /**
     * Gets unit.
     *
     * @return the unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets unit.
     *
     * @param unit the unit
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Gets reference period.
     *
     * @return the reference period
     */
    public ReferencePeriod getReferencePeriod() {
        return referencePeriod;
    }

    /**
     * Sets reference period.
     *
     * @param referencePeriod the reference period
     */
    public void setReferencePeriod(ReferencePeriod referencePeriod) {
        this.referencePeriod = referencePeriod;
    }

    /**
     * Gets meth stand.
     *
     * @return the meth stand
     */
    public MethodOrStandard getMethStand() {
        return methStand;
    }

    /**
     * Sets meth stand.
     *
     * @param methStand the meth stand
     */
    public void setMethStand(MethodOrStandard methStand) {
        this.methStand = methStand;
    }

    /**
     * Gets comments.
     *
     * @return the comments
     */
    public Comments getComments() {
        return comments;
    }

    /**
     * Sets comments.
     *
     * @param comments the comments
     */
    public void setComments(Comments comments) {
        this.comments = comments;
    }

    /**
     * Gets cic.
     *
     * @return the cic
     */
    public Cic getCic() {
        return cic;
    }

    /**
     * Sets cic.
     *
     * @param cic the cic
     */
    public void setCic(Cic cic) {
        this.cic = cic;
    }

    /**
     * Gets releases and releasesAndTransfers
     *
     * @return the releasesAndTransfers
     */
    public ReleasesAndTransfers getReleasesAndTransfers() {
        return releasesAndTransfers;
    }

    /**
     * Sets releasesAndTransfers
     *
     * @param releasesAndTransfers the releasesAndTransfers
     */
    public void setReleasesAndTransfers(ReleasesAndTransfers releasesAndTransfers) {
        this.releasesAndTransfers = releasesAndTransfers;
    }
}