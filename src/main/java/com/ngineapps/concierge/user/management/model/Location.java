package com.ngineapps.concierge.user.management.model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "locations")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Location {

    @Id
    @Column(name = "location_id")
    private int locationId;

    @Column(name = "name")
    @EqualsAndHashCode.Include
    private String name;

    @Column(name = "address")
    private String address;

    @ManyToMany(mappedBy = "locations", fetch = FetchType.LAZY)
    List<User> users = new ArrayList<>();

    @OneToMany(
            mappedBy = "location",
            targetEntity = Management.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("managementId DESC")
    private List<Management> management;


}
