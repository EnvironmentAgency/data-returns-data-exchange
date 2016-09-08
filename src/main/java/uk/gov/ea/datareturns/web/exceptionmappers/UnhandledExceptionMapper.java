package uk.gov.ea.datareturns.web.exceptionmappers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Catch-all Exception mapper
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class UnhandledExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UnhandledExceptionMapper.class);

    /**
     * Handle all types of exception thrown that are not handled by any other exception mapper.
     *
     * These are unexpected exceptions and represent a problem in the server configuration or a programming error.
     *
     * These exceptions should generated a HTTP 500 - Internal Server Error response
     *
     * @param exception the exception which occurred
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final Throwable exception) {
        LOGGER.error("He's Dead Jim", exception);
        final Status status = Status.INTERNAL_SERVER_ERROR;
        final ExceptionMessageContainer entity = new ExceptionMessageContainer(ApplicationExceptionType.SYSTEM_FAILURE,
                exception.toString());
        return Response.status(status).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}