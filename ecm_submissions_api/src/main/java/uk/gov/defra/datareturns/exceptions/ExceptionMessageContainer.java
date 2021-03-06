package uk.gov.defra.datareturns.exceptions;

/**
 * Response container for exception messages
 *
 * @author Sam Gardner-Dell
 */
public final class ExceptionMessageContainer {
    private final int appStatusCode;

    private final String message;

    /**
     * Create a new exception message container for the specified exception type and error message
     *
     * @param exceptionType the exception type associated with the error
     * @param message       the message detailing the problem
     */
    public ExceptionMessageContainer(final ApplicationExceptionType exceptionType, final String message) {
        this.appStatusCode = exceptionType != null ? exceptionType.getAppStatusCode() : -1;
        this.message = message;
    }

    /**
     * @return the status code associated with exception
     */
    public int getAppStatusCode() {
        return this.appStatusCode;
    }

    /**
     * @return the message detailing the problem
     */
    public String getMessage() {
        return this.message;
    }
}
