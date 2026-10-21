package com.eventservice.controller;

import com.eventservice.dto.section.SectionRequest;
import com.eventservice.dto.section.SectionResponse;
import com.eventservice.service.SectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Section management", description = "Endpoints for section management")
@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;

    @PostMapping
    @Operation(summary = "Create a new section")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectionResponse> createSection(
            @RequestBody @Valid SectionRequest request
    ) {
        return ResponseEntity.ok(sectionService.createSection(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get section by id")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<SectionResponse> getSection(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(sectionService.getSection(id));
    }

    @GetMapping
    @Operation(summary = "Get all sections")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'ORGANIZER')")
    public ResponseEntity<Page<SectionResponse>> getAllSections(
            Pageable pageable
    ) {
        return ResponseEntity.ok(sectionService.getAllSections(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update section's data by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long id,
            @RequestBody @Valid SectionRequest request
    ) {
        return ResponseEntity.ok(sectionService.updateSection(request, id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete section by id")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long id
    ) {
        sectionService.deleteSection(id);

        return ResponseEntity.noContent().build();
    }
}
