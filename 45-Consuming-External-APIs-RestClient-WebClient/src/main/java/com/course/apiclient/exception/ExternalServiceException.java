package com.course.apiclient.exception;

/*
 * The OTHER API answered with an error (4xx other than 404, or 5xx).
 * For OUR client this becomes 502 Bad Gateway: "the server behind me failed".
 */
public class ExternalServiceException extends RuntimeException {

    private final int remoteStatus;

    public ExternalServiceException(String message, int remoteStatus) {
        super(message);
        this.remoteStatus = remoteStatus;
    }

    public int getRemoteStatus() {
        return remoteStatus;
    }
}
