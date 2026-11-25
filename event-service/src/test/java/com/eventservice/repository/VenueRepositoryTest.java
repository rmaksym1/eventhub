package com.eventservice.repository;

import com.eventservice.entity.Venue;
import com.testcommon.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/venue/venues-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class VenueRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    @DisplayName("Should find venue by ID")
    void shouldFindVenueById() {
        Optional<Venue> venue = venueRepository.findById(1L);

        assertThat(venue).isPresent();
        assertThat(venue.get().getName()).isEqualTo("Main Hall");
    }

    @Test
    @DisplayName("Should return empty when venue does not exist")
    void shouldReturnEmptyWhenVenueDoesNotExist() {
        Optional<Venue> venue = venueRepository.findById(999L);

        assertThat(venue).isEmpty();
    }

    @Test
    @DisplayName("Should return paginated venues")
    void shouldFindAllVenuesPaginated() {
        Page<Venue> page = venueRepository.findAll(PageRequest.of(0, 2, Sort.by("id")));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(Venue::getName)
                .containsExactly("Main Hall", "Small Room");
    }

    @Test
    @DisplayName("Should save a new venue with generated ID")
    void shouldSaveVenue() {
        Venue venue = new Venue();
        venue.setName("New Arena");

        Venue saved = venueRepository.saveAndFlush(venue);

        assertThat(saved.getId()).isNotNull().isGreaterThan(3L);
        assertThat(venueRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Venue::getName)
                .isEqualTo("New Arena");
    }

    @Test
    @DisplayName("Should delete venue by ID")
    void shouldDeleteVenue() {
        venueRepository.deleteById(3L);
        venueRepository.flush();

        assertThat(venueRepository.findById(3L)).isEmpty();
        assertThat(venueRepository.count()).isEqualTo(2);
    }
}
