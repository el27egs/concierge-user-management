package com.ngineapps.concierge.user.management.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAccountValidationDTO implements Serializable {

    /** */
    private static final long serialVersionUID = -1790553831355238747L;

    private int userId;
    private String accountId;
    private boolean isUserOwnerAccount;

}
