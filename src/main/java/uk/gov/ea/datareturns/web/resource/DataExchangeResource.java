package uk.gov.ea.datareturns.web.resource;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.processors.FileCompletionProcessor;
import uk.gov.ea.datareturns.domain.processors.FileUploadProcessor;
import uk.gov.ea.datareturns.domain.result.DataExchangeResult;
import uk.gov.ea.datareturns.web.filters.FilenameAuthorization;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.io.InputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

/**
 * The {@link DataExchangeResource} RESTful service to handle file uploads to the data returns backend service
 *
 * @author Sam Gardner-Dell
 */
@Path("/data-exchange/")
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class DataExchangeResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);

    private final ApplicationContext context;

    /**
     * Create a new {@link DataExchangeResource} RESTful service
     *
     * @param context the spring application context
     */
    @Inject
    public DataExchangeResource(final ApplicationContext context) {
        this.context = context;
    }

    /**
     * REST method to handle Returns file upload.
     *
     * @param is the {@link InputStream} from which the upload is read
     * @param fileDetail details about the file being uploaded, filename etc
     * @return a {@link Response} object containing a JSON entity.  Responses with HTTP code 4XX indicate a validation error, 5XX a server error.
     * @throws Exception to be handled by the configured Jersey {@link javax.ws.rs.ext.ExceptionMapper}s
     */
    @POST
    @Path("/upload")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    @FilenameAuthorization
    public Response uploadFile(
            @FormDataParam("fileUpload") final InputStream is,
            @FormDataParam("fileUpload") final FormDataContentDisposition fileDetail) throws Exception {
        LOGGER.debug("/data-exchange/upload request received");

        // Define the client file name.  Make sure we strip path information to protect against naughty clients
        String clientFilename = "undefined.csv";
        if (fileDetail != null && fileDetail.getFileName() != null) {
            final File file = new File(fileDetail.getFileName());
            clientFilename = file.getName();
        }

        final FileUploadProcessor processor = this.context.getBean(FileUploadProcessor.class);
        processor.setClientFilename(clientFilename);
        processor.setInputStream(is);

        final DataExchangeResult result = processor.process();
        // Default response status
        Status responseStatus = Status.OK;
        if (result.getAppStatusCode() != -1) {
            responseStatus = Status.BAD_REQUEST;
        }
        return Response.status(responseStatus).entity(result).build();
    }

    /**
     * Complete an upload session
     *
     * @param orgFileKey the file key returned by the {@link DataExchangeResource}.uploadFile method
     * @param userEmail the email address of the user submitting the file
     * @param orgFileName the original filename of the file that was uploaded.
     * @return a {@link Response} object containing a JSON entity.  Responses with HTTP code 4XX indicate a client error (e.g. 404 for invalid file key), 5XX a server error.
     * @throws Exception to be handled by the configured Jersey {@link javax.ws.rs.ext.ExceptionMapper}s
     */
    @POST
    @Path("/complete")
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    @FilenameAuthorization
    public Response completeUpload(
            @NotEmpty @FormDataParam("fileKey") final String orgFileKey,
            @NotEmpty @FormDataParam("userEmail") final String userEmail,
            @NotEmpty @FormDataParam("orgFileName") final String orgFileName) throws Exception {
        LOGGER.debug("/data-exchange/complete request received");

        final FileCompletionProcessor processor = this.context.getBean(FileCompletionProcessor.class);
        processor.setOriginalFilename(orgFileName);
        processor.setStoredFileKey(orgFileKey);
        processor.setUserEmail(userEmail);

        final DataExchangeResult result = processor.process();
        return Response.status(Status.OK).entity(result).build();
    }
}