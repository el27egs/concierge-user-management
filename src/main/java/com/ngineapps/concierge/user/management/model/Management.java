/*
 *     Copyright 2022-Present Ngine Apps @ http://www.ngingeapps.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ngineapps.concierge.user.management.model;

import jakarta.persistence.*;
import lombok.*;

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
