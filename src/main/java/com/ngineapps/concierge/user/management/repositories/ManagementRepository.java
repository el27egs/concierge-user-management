package com.ngineapps.concierge.user.management.repositories;

import com.ngineapps.concierge.user.management.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface ManagementRepository extends CrudRepository<User, String> {
    Optional<User> getUserByEmail(String email);
}
