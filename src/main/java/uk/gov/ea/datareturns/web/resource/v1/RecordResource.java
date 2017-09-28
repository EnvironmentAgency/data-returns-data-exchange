package uk.gov.ea.datareturns.web.resource.v1;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.DatasetEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.userdata.impl.RecordEntity;
import uk.gov.ea.datareturns.domain.jpa.service.DatasetService;
import uk.gov.ea.datareturns.domain.jpa.service.SitePermitService;
import uk.gov.ea.datareturns.domain.jpa.service.SubmissionService;
import uk.gov.ea.datareturns.web.resource.JerseyResource;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Linker;
import uk.gov.ea.datareturns.web.resource.v1.model.common.Preconditions;
import uk.gov.ea.datareturns.web.resource.v1.model.common.references.EntityReference;
import uk.gov.ea.datareturns.web.resource.v1.model.record.Record;
import uk.gov.ea.datareturns.web.resource.v1.model.record.RecordAdaptor;
import uk.gov.ea.datareturns.web.resource.v1.model.record.payload.Payload;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequest;
import uk.gov.ea.datareturns.web.resource.v1.model.request.BatchRecordRequestItem;
import uk.gov.ea.datareturns.web.resource.v1.model.response.*;

import javax.inject.Inject;
import javax.validation.constraints.Pattern;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static uk.gov.ea.datareturns.web.resource.v1.model.common.PreconditionChecks.onPreconditionsPass;

/**
 * RESTful resource to manage record entities.
 *
 * @author Sam Gardner-Dell
 */
@Api(description = "Records Resource",
        tags = { "Records" },
        // Specifying consumes/produces (again) here allows us to default the swagger ui to json
        consumes = APPLICATION_JSON + "," + APPLICATION_XML,
        produces = APPLICATION_JSON + "," + APPLICATION_XML
)
@Path("/ea_ids/{ea_id}/datasets/{dataset_id}/records")
@Consumes({ APPLICATION_JSON, APPLICATION_XML })
@Produces({ APPLICATION_JSON, APPLICATION_XML })
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RecordResource implements JerseyResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordResource.class);
    private final SitePermitService sitePermitService;
    private final RecordAdaptor recordAdaptor;
    @Context
    private UriInfo uriInfo;

    private SubmissionService submissionService;
    private DatasetService datasetService;

    /**
     * Create a new {@link RecordResource} RESTful service
     */
    @Inject
    public RecordResource(SitePermitService sitePermitService, RecordAdaptor recordAdaptor,
                          SubmissionService submissionService, DatasetService datasetService) {

        this.sitePermitService = sitePermitService;
        this.recordAdaptor = recordAdaptor;
        this.submissionService = submissionService;
        this.datasetService = datasetService;
    }

    /**
     * List all records for the given dataset_id
     *
     * @param eaIdId the owning EA_ID
     * @param datasetId the unique identifier for the target dataset
     * @return a response containing an {@link EntityReferenceListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @ApiOperation(value = "List records",
            notes = "This operation will list all records for the given `dataset_id`."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = EntityReferenceListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `ea_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response listRecords(
            @PathParam("ea_id") @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the owning ea_id") final String eaIdId,
            @PathParam("dataset_id") @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId)
            throws Exception {
        return onDataset(eaIdId, datasetId, datasetEntity -> {
            List<RecordEntity> records = submissionService.getRecords(datasetEntity);
            return new EntityReferenceListResponse(
                    records.stream().map((entity) -> {
                        String uri = Linker.info(uriInfo).record(eaIdId, datasetId, entity.getIdentifier());
                        return new EntityReference(entity.getIdentifier(), uri);
                    }).collect(Collectors.toList()),
                    Date.from(datasetEntity.getRecordChangedDate()),
                    Preconditions.createEtag(records)
            ).toResponseBuilder();
        }).build();
    }

    /**
     * Retrieve all record and payload data for the given dataset_id
     *
     * @param eaIdId the owning EA_ID
     * @param datasetId the unique identifier for the target dataset
     * @return a response containing an {@link RecordListResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/$data")
    @ApiOperation(value = "List full record data",
            notes = "Retrieve all record and payload data for the given `dataset_id`."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RecordListResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `ea_id` parameter did not match a known resource",
                    response = ErrorResponse.class
            )
    })
    public Response listRecordData(
            @PathParam("ea_id") @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the owning ea_id") final String eaIdId,
            @PathParam("dataset_id") @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId)
            throws Exception {
        return onDataset(eaIdId, datasetId, datasetEntity -> {
            List<RecordEntity> records = submissionService.getRecords(datasetEntity);
            return new RecordListResponse(
                    records.stream().map(e -> fromEntity(eaIdId, datasetId, e)).collect(Collectors.toList()),
                    Date.from(datasetEntity.getRecordChangedDate()),
                    Preconditions.createEtag(records)
            ).toResponseBuilder();
        }).build();
    }

    /**
     * Batch record request (create/update)
     *
     * @param datasetId the unique identifier for the target dataset
     * @param batchRequest the batch record request data
     * @return a response containing a {@link MultiStatusResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @POST
    @ApiOperation(value = "Create or update multiple records",
            notes = "Multiple records can be create or updated in a single POST request by passing a collection of individual requests "
                    + "in the request body.\n\n"
                    + "Each request passed within the collection can contain its own set of preconditions as per the conditional request "
                    + "mechanism.\n\n"
                    + "To enable server-side ID generation omit the record_id from the individual request body.\n\n"
                    + "The response body uses a multistatus structure based on the principles outlined in "
                    + "https://tools.ietf.org/html/rfc4918#section-13."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    code = 207,
                    message = "Multi-Status - responses to each create/update request are encoded in the response body",
                    response = MultiStatusResponse.class
            ),
            @ApiResponse(
                    code = 400,
                    message = "Bad Request - no request items could be extracted from the request body.",
                    response = ErrorResponse.class
            ),
            @ApiResponse(code = 404, message = "Not Found - The `dataset_id` parameter did not match a known resource."),
            @ApiResponse(
                    code = 403,
                    message = "Forbidden - the dataset is already submitted and cannot be ammended",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 409,
                    message = "Conflict - The records submitted in the batch do not have unique record_id's",
                    response = ErrorResponse.class
            )
    })
    public Response postRecords(
            @PathParam("ea_id") @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the owning ea_id") final String eaIdId,
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @ApiParam("The bulk record create/update request") final BatchRecordRequest batchRequest
    )
            throws Exception {

        return onDataset(eaIdId, datasetId, datasetEntity -> {
            Response.ResponseBuilder rb;
            if (batchRequest.getRequests().isEmpty()) {
                rb = ErrorResponse.MULTISTATUS_REQUEST_EMPTY.toResponseBuilder();
            } else if (batchRequest
                    .getRequests()
                    .stream()
                    .map(BatchRecordRequestItem::getRecordId)
                    .collect(Collectors.toSet()).size() != batchRequest.getRequests().size()) {
                // The record id's are not unique - issue a 409 error.
                rb = ErrorResponse.DUPLICATE_RECORD_ID_WITHIN_BATCH.toResponseBuilder();
            } else {
                List<String> recordIds = batchRequest.getRequests().stream().map(BatchRecordRequestItem::getRecordId)
                        .collect(Collectors.toList());

                Map<String, RecordEntity> recordEntities = submissionService.getRecords(datasetEntity)
                        .stream()
                        .filter(r -> recordIds.contains(r.getIdentifier()))
                        .collect(Collectors.toMap(RecordEntity::getIdentifier, r -> r));

                Map<String, Response.ResponseBuilder> preconditionFailures = new HashMap<>();
                Map<String, Response.Status> responses = new HashMap<>();
                Map<String, Payload> payloads = new HashMap<>();

                // Build requests for the service layer
                for (BatchRecordRequestItem request : batchRequest.getRequests()) {
                    RecordEntity recordEntity = recordEntities.get(request.getRecordId());
                    Response.Status defaultResponse;

                    if (recordEntity == null) {
                        defaultResponse = Response.Status.CREATED;
                    } else if (datasetEntity.getStatus() == DatasetEntity.Status.SUBMITTED) {
                        defaultResponse = Response.Status.FORBIDDEN;
                    } else {
                        defaultResponse = Response.Status.OK;
                    }

                    // Store default response status
                    responses.put(request.getRecordId(), defaultResponse);

                    // Store existing entity
                    recordEntities.put(request.getRecordId(), recordEntity);

                    // Check preconditions, building a list of entries and any precondition failures
                    Response.ResponseBuilder failureResponse = onPreconditionsPass(fromEntity(eaIdId, datasetId, recordEntity),
                            request.getPreconditions(),
                            () -> {
                                // Preconditions passed, add a new PayloadIdentifier
                                payloads.put(request.getRecordId(), request.getPayload());
                                return null;
                            });

                    if (failureResponse != null) {
                        preconditionFailures.put(request.getRecordId(), failureResponse);
                    }
                }

                // Create and validate records if they are not already submitted
                recordEntities.putAll(submissionService.createRecords(datasetEntity, payloads));

                // Build the response
                MultiStatusResponse multiResponse = new MultiStatusResponse();
                for (BatchRecordRequestItem request : batchRequest.getRequests()) {
                    MultiStatusResponse.Response responseItem = new MultiStatusResponse.Response();
                    responseItem.setId(request.getRecordId());

                    RecordEntity recordEntity = recordEntities.get(request.getRecordId());
                    Response.ResponseBuilder failureResponse = preconditionFailures.get(request.getRecordId());

                    if (failureResponse == null) {
                        // Preconditions passed, service the request
                        Record record = fromEntity(eaIdId, datasetId, recordEntity);

                        responseItem.setCode(responses.get(request.getRecordId()).getStatusCode());
                        responseItem.setHref(Linker.info(uriInfo).record(eaIdId, datasetId, request.getRecordId()));
                        responseItem.setEntityTag(Preconditions.createEtag(record).toString());
                        responseItem.setLastModified(record.getLastModified());
                    } else {
                        // Preconditions failed, build a response item from the ResponseBuilder returned by the preconditions checks
                        Response response = failureResponse.build();
                        responseItem.setCode(response.getStatus());

                        if (recordEntity != null) {
                            // Entity does exist so populate the href
                            responseItem.setHref(Linker.info(uriInfo)
                                    .record(eaIdId, datasetId, request.getRecordId()));
                        }
                    }
                    multiResponse.addResponse(responseItem);
                }
                rb = multiResponse.toResponseBuilder();
            }
            return rb;
        }).build();
    }

    /**
     * Retrieve record data for the given `record_id` and `dataset_id`
     *
     * @param eaIdId the owning EA_ID
     * @param datasetId the unique identifier for the target dataset
     * @param recordId the unique identifier for the target record
     * @param preconditions conditional request structure
     * @return a response containing an {@link RecordEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @GET
    @Path("/{record_id : [a-zA-Z0-9_-]+ }")
    @ApiOperation(value = "Retrieve record details",
            notes = "Retrieve record data for the given `record_id` and `dataset_id`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = RecordEntityResponse.class),
            @ApiResponse(code = 304, message = "Not Modified - see conditional request documentation"),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `record_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail"
            )
    })
    public Response getRecord(
            @PathParam("ea_id") @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the owning ea_id") final String eaIdId,
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @PathParam("record_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for a rcord") final String recordId,
            @BeanParam Preconditions preconditions
    )
            throws Exception {
        return onDataset(eaIdId, datasetId, (datasetEntity) ->
                onRecord(datasetEntity, recordId, (recordEntity) ->
                        onPreconditionsPass(fromEntity(eaIdId, datasetId, recordEntity), preconditions, () ->
                                new RecordEntityResponse(Response.Status.OK, fromEntity(eaIdId, datasetId, recordEntity)).toResponseBuilder()
                        )
                )
        ).build();
    }

    /**
     * Create or update the record with the given record_id for the dataset with the given dataset_id
     *
     * @param eaIdId the owning EA_ID
     * @param datasetId the unique identifier for the target dataset
     * @param recordId the unique identifier for the target record
     * @param payload the data payload to associate with the record
     * @param preconditions conditional request structure
     * @return a response containing an {@link RecordEntityResponse} entity
     * @throws Exception if the request cannot be completed normally.
     */
    @PUT
    @Path("/{record_id : [a-zA-Z0-9_-]+ }")
    @ApiOperation(value = "Create or update record",
            notes = "Create or update the record with the given `record_id` for the dataset with the given `dataset_id`."
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK - an existing record was successfully updated.", response = RecordEntityResponse.class),
            @ApiResponse(code = 201, message = "Created - a new record was created.", response = RecordEntityResponse.class),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `record_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed - see conditional request documentation",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 403,
                    message = "Forbidden - the dataset is already submitted and cannot be ammended",
                    response = ErrorResponse.class
            )
    })
    public Response putRecord(
            @PathParam("ea_id") @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the owning ea_id") final String eaIdId,
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,

            @PathParam("record_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target record") final String recordId,
            @ApiParam("The data payload to be associated with the record") final Payload payload,

            @BeanParam Preconditions preconditions
    )
            throws Exception {
        return onDataset(eaIdId,datasetId, (datasetEntity) -> {
            RecordEntity existingEntity = submissionService.getRecord(datasetEntity, recordId);
            return onPreconditionsPass(fromEntity(eaIdId, datasetId, existingEntity), preconditions, () -> {
                Response.Status status = Response.Status.OK;
                Record record = null;

                // If it already exists then we have a conflict
                if (existingEntity == null) {
                    status = Response.Status.CREATED;
                } else if (datasetEntity.getStatus() == DatasetEntity.Status.SUBMITTED) {
                    status = Response.Status.FORBIDDEN;
                    record = fromEntity(eaIdId, datasetId, existingEntity);
                }

                // Preconditions passed, create/update the record
                if (status != Response.Status.FORBIDDEN) {
                    RecordEntity recordEntity = submissionService.createRecord(datasetEntity, recordId, payload);

                    record = fromEntity(eaIdId, datasetId, recordEntity);
                }

                return new RecordEntityResponse(status, record).toResponseBuilder();
            });
        }).build();
    }

    /**
     * Delete the record with the given record_id from the dataset with the given dataset_id
     *
     * @param eaIdId the owning EA_ID
     * @param datasetId the unique identifier for the target dataset
     * @param recordId the unique identifier for the target record
     * @param preconditions conditional request structure
     * @return an empty response body as per HTTP 204
     * @throws Exception if the request cannot be completed normally.
     */
    @DELETE
    @Path("/{record_id : [a-zA-Z0-9_-]+ }")
    @ApiOperation(value = "Delete record",
            notes = "Delete the record with the given `record_id` from the dataset with the given `dataset_id`"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No Content - an existing record was successfully deleted."),
            @ApiResponse(
                    code = 404,
                    message = "Not Found - The `dataset_id` or `record_id` parameter did not match a known resource - "
                            + "see the `meta` structure in the response envelope for more detail",
                    response = ErrorResponse.class
            ),
            @ApiResponse(
                    code = 412,
                    message = "Precondition Failed - see conditional request documentation",
                    response = ErrorResponse.class
            )
    })
    public Response deleteRecord(
            @PathParam("ea_id") @Pattern(regexp = "\\p{Print}+")
            @ApiParam("The unique identifier for the owning ea_id") final String eaIdId,
            @PathParam("dataset_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target dataset") final String datasetId,
            @PathParam("record_id")
            @Pattern(regexp = "[A-Za-z0-9_-]+")
            @ApiParam("The unique identifier for the target record") final String recordId,
            @BeanParam Preconditions preconditions
    )
            throws Exception {
        return onDataset(eaIdId, datasetId, (datasetEntity) ->
                onRecord(datasetEntity, recordId, (recordEntity) ->
                        onPreconditionsPass(fromEntity(eaIdId, datasetId, recordEntity), preconditions, () -> {
                            // Preconditions passed, delete the record
                            submissionService.removeRecord(datasetEntity, recordEntity.getIdentifier());
                            return Response.status(Response.Status.NO_CONTENT);
                        })
                )
        ).build();
    }

    private Record fromEntity(String eaIdId, String datasetId, RecordEntity entity) {
        Record record = null;
        if (entity != null) {
            record = recordAdaptor.convert(entity);
            Linker.info(uriInfo).resolve(eaIdId, datasetId, record);
        }
        return record;
    }

    private Response.ResponseBuilder onDataset(String eaIdId, String datasetId, Function<DatasetEntity, Response.ResponseBuilder> handler) {
        if (sitePermitService.getUniqueIdentifierByName(eaIdId) == null) {
            return ErrorResponse.EA_ID_NOT_FOUND.toResponseBuilder();
        }
        DatasetEntity datasetEntity = datasetService.getDataset(eaIdId, datasetId);
        return (datasetEntity == null) ? ErrorResponse.DATASET_NOT_FOUND.toResponseBuilder() : handler.apply(datasetEntity);
    }

    private Response.ResponseBuilder onRecord(DatasetEntity datasetEntity, String recordId, Function<RecordEntity, Response
            .ResponseBuilder> handler) {
        RecordEntity recordEntity = submissionService.getRecord(datasetEntity, recordId);
        return (recordEntity == null) ? ErrorResponse.RECORD_NOT_FOUND.toResponseBuilder() : handler.apply(recordEntity);
    }
}