/**
 *
 */
package uk.gov.ea.datareturns.web.exceptionmappers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import uk.gov.ea.datareturns.domain.exceptions.AbstractValidationException;
import uk.gov.ea.datareturns.domain.result.ExceptionMessageContainer;

/**
 * Validation exception mapper
 *
 * @author Sam Gardner-Dell
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<AbstractValidationException> {
	@Override
	public Response toResponse(final AbstractValidationException exception) {
		final Status status = Status.BAD_REQUEST;
		final ExceptionMessageContainer entity = new ExceptionMessageContainer(exception.getType(), exception.getMessage());
		return Response.status(status).entity(entity).type(MediaType.APPLICATION_JSON_TYPE).build();
	}
}