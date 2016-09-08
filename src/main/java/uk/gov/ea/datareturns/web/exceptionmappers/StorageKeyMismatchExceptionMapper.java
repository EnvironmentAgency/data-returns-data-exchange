package uk.gov.ea.datareturns.web.exceptionmappers;

import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;
import uk.gov.ea.datareturns.domain.storage.StorageKeyMismatchException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Storage key mismatch exception mapper
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class StorageKeyMismatchExceptionMapper implements ExceptionMapper<StorageKeyMismatchException> {
    /**
     * Handles {@link StorageKeyMismatchException}s which should always generated a HTTP 404 - Not Found response
     *
     * @param exception the {@link StorageKeyMismatchException} thrown by the {@link uk.gov.ea.datareturns.domain.storage.StorageProvider}
     * @return a {@link Response} to return to the client for the exception that was thrown
     */
    @Override
    public Response toResponse(final StorageKeyMismatchException exception) {
        final Status status = Status.NOT_FOUND;
        final ExceptionMessageContainer entity = new ExceptionMessageContainer(ApplicationExceptionType.SYSTEM_FAILURE,
                exception.getMessage());
        return Response.status(status).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
    }
}
