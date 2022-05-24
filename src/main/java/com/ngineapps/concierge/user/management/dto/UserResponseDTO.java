package com.ngineapps.concierge.user.management.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class UserResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String email;
}
