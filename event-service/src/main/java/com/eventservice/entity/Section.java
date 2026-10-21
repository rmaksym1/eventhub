package com.eventservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "venue_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Venue venue;

    @OneToMany(mappedBy = "section")
    private Set<Seat> seats = new HashSet<>();
}
