package com.youthx.backend.exception;

/**
 * Thrown when an authenticated user attempts to access or mutate a resource
 * owned by another user. Subclasses {@link IllegalArgumentException} to preserve
 * backward-compatibility with existing unit test assertions.
 */
public class ResourceOwnershipException extends IllegalArgumentException {

    public ResourceOwnershipException(String message) {
        super(message);
    }
}
