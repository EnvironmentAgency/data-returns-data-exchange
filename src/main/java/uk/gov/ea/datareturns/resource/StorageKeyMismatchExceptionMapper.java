/**
 *
 */
package uk.gov.ea.datareturns.resource;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import uk.gov.ea.datareturns.storage.StorageKeyMismatchException;
import uk.gov.ea.datareturns.type.ApplicationExceptionType;

/**
 * Storage key mismatch exception mapper
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class StorageKeyMismatchExceptionMapper implements ExceptionMapper<StorageKeyMismatchException> {
	@Override
	public Response toResponse(final StorageKeyMismatchException exception) {
		final Status status = Status.NOT_FOUND;
		final ExceptionMessageContainer entity = new ExceptionMessageContainer(ApplicationExceptionType.SYSTEM_FAILURE,
				exception.getMessage());
		return Response.status(status).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}
