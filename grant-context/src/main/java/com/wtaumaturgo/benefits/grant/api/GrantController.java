package com.wtaumaturgo.benefits.grant.api;

import com.wtaumaturgo.benefits.grant.api.dto.CreateGrantRequest;
import com.wtaumaturgo.benefits.grant.api.dto.GrantResponse;
import com.wtaumaturgo.benefits.grant.api.dto.RenewGrantRequest;
import com.wtaumaturgo.benefits.grant.application.CreateGrantUseCase;
import com.wtaumaturgo.benefits.grant.application.GetGrantUseCase;
import com.wtaumaturgo.benefits.grant.application.RenewGrantUseCase;
import com.wtaumaturgo.benefits.grant.application.RevokeGrantUseCase;
import com.wtaumaturgo.benefits.grant.application.dto.RevokeGrantCommand;
import com.wtaumaturgo.benefits.grant.domain.exception.GrantNotFoundException;
import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

/**
 * REST facade over the Grant use cases (GRANT-10).
 * <ul>
 *   <li>{@code POST   /grants}            — create</li>
 *   <li>{@code GET    /grants/{id}}        — read</li>
 *   <li>{@code PUT    /grants/{id}/renew}  — renew</li>
 *   <li>{@code DELETE /grants/{id}}        — revoke (not physical delete)</li>
 * </ul>
 * Never returns the aggregate — always {@link GrantResponse}.
 */
@RestController
@RequestMapping("/grants")
public class GrantController {

    private final CreateGrantUseCase createUseCase;
    private final RevokeGrantUseCase revokeUseCase;
    private final RenewGrantUseCase renewUseCase;
    private final GetGrantUseCase getUseCase;

    public GrantController(CreateGrantUseCase createUseCase,
                           RevokeGrantUseCase revokeUseCase,
                           RenewGrantUseCase renewUseCase,
                           GetGrantUseCase getUseCase) {
        this.createUseCase = createUseCase;
        this.revokeUseCase = revokeUseCase;
        this.renewUseCase = renewUseCase;
        this.getUseCase = getUseCase;
    }

    @PostMapping
    public ResponseEntity<GrantResponse> create(@RequestBody CreateGrantRequest request,
                                                UriComponentsBuilder uri) {
        GrantId id = createUseCase.execute(request.toCommand());
        Grant grant = getUseCase.execute(id)
            .orElseThrow(() -> new GrantNotFoundException(id));
        return ResponseEntity
            .created(uri.path("/grants/{id}").build(id.value()))
            .body(GrantResponse.from(grant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revoke(@PathVariable UUID id) {
        revokeUseCase.execute(new RevokeGrantCommand(new GrantId(id)));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/renew")
    public ResponseEntity<GrantResponse> renew(@PathVariable UUID id,
                                               @RequestBody RenewGrantRequest request) {
        GrantId grantId = new GrantId(id);
        renewUseCase.execute(request.toCommand(grantId));
        Grant grant = getUseCase.execute(grantId)
            .orElseThrow(() -> new GrantNotFoundException(grantId));
        return ResponseEntity.ok(GrantResponse.from(grant));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrantResponse> get(@PathVariable UUID id) {
        return getUseCase.execute(new GrantId(id))
            .map(GrantResponse::from)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
