package uk.gov.ea.datareturns.web.resource.v1.model.response;

import javax.ws.rs.core.Response;

/**
 * Error response.  Populates response metadata when handling an API request error.
 *
 * @author Sam Gardner-Dell
 */
public class ErrorResponse extends ResponseWrapper<Object> {

    public static final ErrorResponse DATASET_NOT_FOUND = new ErrorResponse(
            Response.Status.NOT_FOUND, "No dataset was found for the given dataset_id.");
    public static final ErrorResponse RECORD_NOT_FOUND = new ErrorResponse(
            Response.Status.NOT_FOUND, "No record was found for the given record_id.");
    public static final ErrorResponse PAYLOAD_TYPE_NOT_FOUND = new ErrorResponse(
            Response.Status.NOT_FOUND, "No payload was found for the given payload_type");
    public static final ErrorResponse FIELD_NOT_FOUND = new ErrorResponse(
            Response.Status.NOT_FOUND, "No field could be found for the given field_id");
    public static final ErrorResponse CONSTRAINT_NOT_FOUND = new ErrorResponse(
            Response.Status.NOT_FOUND, "No constraint could be found for the given contraint_id");
    public static final ErrorResponse PRECONDITION_FAILED = new ErrorResponse(
            Response.Status.PRECONDITION_FAILED, "A request precondition failed.");
    public static final ErrorResponse MULTISTATUS_REQUEST_EMPTY = new ErrorResponse(
            Response.Status.BAD_REQUEST, "No request items could be extracted from the request body.");

    @SuppressWarnings("unused")
    public ErrorResponse() {

    }

    public ErrorResponse(Response.Status status, String error) {
        super();
        this.setMeta(new Metadata(status.getStatusCode(), error));
    }

    @Override public Object getData() {
        return null;
    }

    @Override public void setData(Object data) {
    }
}