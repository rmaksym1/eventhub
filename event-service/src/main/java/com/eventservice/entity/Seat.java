package com.eventservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seat_section_row_number",
                columnNames = {"section_id", "row_number", "seat_number"}
        )
)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rowNumber;

    @Column(nullable = false)
    private Integer seatNumber;

    @JoinColumn(name = "section_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Section section;
}
