package com.eventservice.repository;

import com.eventservice.entity.Section;
import com.eventservice.entity.Venue;
import com.testcommon.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/section/sections-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SectionRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Should find section by ID together with its venue")
    void shouldFindSectionById() {
        Optional<Section> section = sectionRepository.findById(1L);

        assertThat(section).isPresent();
        assertThat(section.get().getName()).isEqualTo("Parter");
        assertThat(section.get().getVenue().getId()).isEqualTo(1L);
        assertThat(section.get().getVenue().getName()).isEqualTo("Main Hall");
    }

    @Test
    @DisplayName("Should return empty when section does not exist")
    void shouldReturnEmptyWhenSectionDoesNotExist() {
        assertThat(sectionRepository.findById(999L)).isEmpty();
    }

    @Test
    @DisplayName("Should return paginated sections")
    void shouldFindAllSectionsPaginated() {
        Page<Section> page = sectionRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent())
                .extracting(Section::getName)
                .containsExactlyInAnyOrder("Parter", "Balcony", "VIP");
    }

    @Test
    @DisplayName("Should save a new section assigned to an existing venue")
    void shouldSaveSection() {
        Venue venue = venueRepository.findById(2L).orElseThrow();

        Section section = new Section();
        section.setName("Lodge");
        section.setVenue(venue);

        Section saved = sectionRepository.saveAndFlush(section);

        assertThat(saved.getId()).isNotNull().isGreaterThan(3L);
        assertThat(saved.getVenue().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Should fail to delete a venue that still has sections")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotDeleteVenueWithSections() {
        assertThatThrownBy(() -> {
            venueRepository.deleteById(1L);
            venueRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
