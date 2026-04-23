package com.wtaumaturgo.benefits.api.error;

import com.wtaumaturgo.benefits.grant.domain.exception.GrantNotFoundException;
import com.wtaumaturgo.benefits.shared.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

/**
 * Global RFC 7807 exception handler (D-05, D-06). Renders domain exceptions
 * as transport-appropriate HTTP responses:
 * <ul>
 *   <li>{@link GrantNotFoundException} — HTTP 404 with empty body
 *       (RFC 9110 §15.5.5 — "resource not found" is a transport concern).</li>
 *   <li>Any other {@link DomainException} — HTTP 422 with {@code application/problem+json}
 *       body (RFC 7807 — business invariant violation).</li>
 * </ul>
 *
 * <p>Spring's {@code ExceptionHandlerExceptionResolver} picks the MOST SPECIFIC
 * {@code @ExceptionHandler} method by walking the thrown exception's class
 * hierarchy — so the {@code GrantNotFoundException} handler wins for that type,
 * and the {@code DomainException} handler catches every other subclass.</p>
 *
 * <p>Intentionally does NOT extend {@code ResponseEntityExceptionHandler} —
 * Spring Boot 3.3 auto-registers that class when
 * {@code spring.mvc.problemdetails.enabled=true} (set in application.yml),
 * handling framework exceptions with ProblemDetail out of the box. Extending
 * here would cause duplicate handling (Pitfall 4 in 02-RESEARCH.md).</p>
 *
 * <p>Placement: {@code app/} rather than {@code grant-context/api/}. The handler
 * is cross-cutting — Phase 3 Wallet and Phase 4 Redemption exceptions are also
 * subclasses of DomainException and will route through the same method without
 * any new advice class.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_TYPE_BASE =
        "https://github.com/wesleytaumaturgo/ddd-benefits-engine/errors/";

    /**
     * Maps {@link GrantNotFoundException} to HTTP 404 with an empty body.
     * Declared BEFORE {@link #handleDomainException(DomainException)} so that
     * Spring's most-specific-wins rule is visually obvious to readers —
     * actual precedence is determined by the thrown class hierarchy, not file
     * order, but the ordering here documents the intent.
     */
    @ExceptionHandler(GrantNotFoundException.class)
    public ResponseEntity<Void> handleGrantNotFound(GrantNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(DomainException.class)
    public ProblemDetail handleDomainException(DomainException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create(ERROR_TYPE_BASE + ex.errorCode()));
        pd.setTitle(ex.errorTitle());
        pd.setProperty("timestamp", Instant.now());
        pd.setProperty("errorCode", ex.errorCode());
        return pd;
    }
}
