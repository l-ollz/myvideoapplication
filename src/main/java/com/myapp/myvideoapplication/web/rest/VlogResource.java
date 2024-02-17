package com.myapp.myvideoapplication.web.rest;

import com.myapp.myvideoapplication.domain.Vlog;
import com.myapp.myvideoapplication.repository.VlogRepository;
import com.myapp.myvideoapplication.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.myapp.myvideoapplication.domain.Vlog}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class VlogResource {

    private final Logger log = LoggerFactory.getLogger(VlogResource.class);

    private static final String ENTITY_NAME = "vlog";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VlogRepository vlogRepository;

    public VlogResource(VlogRepository vlogRepository) {
        this.vlogRepository = vlogRepository;
    }

    /**
     * {@code POST  /vlogs} : Create a new vlog.
     *
     * @param vlog the vlog to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new vlog, or with status {@code 400 (Bad Request)} if the vlog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/vlogs")
    public ResponseEntity<Vlog> createVlog(@Valid @RequestBody Vlog vlog) throws URISyntaxException {
        log.debug("REST request to save Vlog : {}", vlog);
        if (vlog.getId() != null) {
            throw new BadRequestAlertException("A new vlog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Vlog result = vlogRepository.save(vlog);
        return ResponseEntity
            .created(new URI("/api/vlogs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /vlogs/:id} : Updates an existing vlog.
     *
     * @param id the id of the vlog to save.
     * @param vlog the vlog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vlog,
     * or with status {@code 400 (Bad Request)} if the vlog is not valid,
     * or with status {@code 500 (Internal Server Error)} if the vlog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/vlogs/{id}")
    public ResponseEntity<Vlog> updateVlog(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Vlog vlog)
        throws URISyntaxException {
        log.debug("REST request to update Vlog : {}, {}", id, vlog);
        if (vlog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vlog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vlogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Vlog result = vlogRepository.save(vlog);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vlog.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /vlogs/:id} : Partial updates given fields of an existing vlog, field will ignore if it is null
     *
     * @param id the id of the vlog to save.
     * @param vlog the vlog to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated vlog,
     * or with status {@code 400 (Bad Request)} if the vlog is not valid,
     * or with status {@code 404 (Not Found)} if the vlog is not found,
     * or with status {@code 500 (Internal Server Error)} if the vlog couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/vlogs/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Vlog> partialUpdateVlog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Vlog vlog
    ) throws URISyntaxException {
        log.debug("REST request to partial update Vlog partially : {}, {}", id, vlog);
        if (vlog.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, vlog.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!vlogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Vlog> result = vlogRepository
            .findById(vlog.getId())
            .map(existingVlog -> {
                if (vlog.getName() != null) {
                    existingVlog.setName(vlog.getName());
                }
                if (vlog.getHandle() != null) {
                    existingVlog.setHandle(vlog.getHandle());
                }

                return existingVlog;
            })
            .map(vlogRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, vlog.getId().toString())
        );
    }

    /**
     * {@code GET  /vlogs} : get all the vlogs.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of vlogs in body.
     */
    @GetMapping("/vlogs")
    public ResponseEntity<List<Vlog>> getAllVlogs(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Vlogs");
        Page<Vlog> page;
        if (eagerload) {
            page = vlogRepository.findAllWithEagerRelationships(pageable);
        } else {
            page = vlogRepository.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /vlogs/:id} : get the "id" vlog.
     *
     * @param id the id of the vlog to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the vlog, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/vlogs/{id}")
    public ResponseEntity<Vlog> getVlog(@PathVariable Long id) {
        log.debug("REST request to get Vlog : {}", id);
        Optional<Vlog> vlog = vlogRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(vlog);
    }

    /**
     * {@code DELETE  /vlogs/:id} : delete the "id" vlog.
     *
     * @param id the id of the vlog to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/vlogs/{id}")
    public ResponseEntity<Void> deleteVlog(@PathVariable Long id) {
        log.debug("REST request to delete Vlog : {}", id);
        vlogRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
