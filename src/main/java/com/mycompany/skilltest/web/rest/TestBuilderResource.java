package com.mycompany.skilltest.web.rest;

import com.mycompany.skilltest.security.AuthoritiesConstants;
import com.mycompany.skilltest.service.TestBuilderService;
import com.mycompany.skilltest.service.dto.TestBuilderDTO;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test-builder")
@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.MANAGER })
public class TestBuilderResource {

    private static final Logger LOG = LoggerFactory.getLogger(TestBuilderResource.class);

    private final TestBuilderService testBuilderService;

    public TestBuilderResource(TestBuilderService testBuilderService) {
        this.testBuilderService = testBuilderService;
    }

    @PostMapping("")
    public ResponseEntity<TestBuilderDTO> saveTest(@Valid @RequestBody TestBuilderDTO dto) throws URISyntaxException {
        LOG.debug("REST request to save test via builder: {}", dto.getTitre());
        TestBuilderDTO result = testBuilderService.save(dto);
        if (dto.getId() == null) {
            return ResponseEntity.created(new URI("/api/test-builder/" + result.getId())).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestBuilderDTO> loadTest(@PathVariable Long id) {
        LOG.debug("REST request to load test via builder: {}", id);
        TestBuilderDTO result = testBuilderService.load(id);
        return ResponseEntity.ok(result);
    }
}
