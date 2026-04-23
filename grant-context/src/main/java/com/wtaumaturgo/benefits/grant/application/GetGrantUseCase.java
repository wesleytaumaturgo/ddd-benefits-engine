package com.wtaumaturgo.benefits.grant.application;

import com.wtaumaturgo.benefits.grant.domain.model.Grant;
import com.wtaumaturgo.benefits.grant.domain.model.GrantId;
import com.wtaumaturgo.benefits.grant.domain.repository.GrantRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/** Read-only query (GRANT-10 GET endpoint). No event publication, no @Transactional. */
@Service
public class GetGrantUseCase {

    private final GrantRepository repository;

    public GetGrantUseCase(GrantRepository repository) {
        this.repository = repository;
    }

    public Optional<Grant> execute(GrantId id) {
        return repository.findById(id);
    }
}
