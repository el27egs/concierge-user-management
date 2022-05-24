package com.ngineapps.concierge.user.management.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "managements")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Management {

    @Id
    @Column(name = "management_id")
    @EqualsAndHashCode.Include
    private String managementId;

    @Column(name = "name")
    private String name;

    @Column(name = "account_id")
    private String accountId;

    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", referencedColumnName = "location_id")
    @ToString.Exclude
    private Location location;

}
