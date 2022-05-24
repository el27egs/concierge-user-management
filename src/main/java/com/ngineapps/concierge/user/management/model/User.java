package com.ngineapps.concierge.user.management.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User {
    @Id
    @Column(name = "user_id")
    private int userId;

    @Column(name = "email")
    @EqualsAndHashCode.Include
    private String email;

    @OneToMany(
            mappedBy = "user",
            targetEntity = Management.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("managementId DESC")
    private List<Management> management;

    @ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_locations",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "location_id") }
    )
    List<Location> locations = new ArrayList<>();


}
