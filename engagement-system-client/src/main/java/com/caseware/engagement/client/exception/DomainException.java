package com.caseware.engagement.client.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DomainException extends RuntimeException {
    public enum Kind {
        NOT_FOUND,
        CONFLICT,
        BAD_REQUEST
    }

    private final Kind kind;
    private final String domainMessage;

    @Override
    public String getMessage() {
        return domainMessage;
    }

    public static DomainException notFound(String message) {
        return new DomainException(Kind.NOT_FOUND, message);
    }

    public static DomainException conflict(String message) {
        return new DomainException(Kind.CONFLICT, message);
    }

    public static DomainException badRequest(String message) {
        return new DomainException(Kind.BAD_REQUEST, message);
    }
}
