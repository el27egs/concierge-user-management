package com.ngineapps.concierge.user.management.repositories;

import com.ngineapps.concierge.user.management.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository public interface UserRepository  extends CrudRepository<User, String> {
    @Query(
            value =
                    "select distinct u FROM User u LEFT JOIN FETCH u.management m where u.userId = :userId and m" +
                            ".accountId = :accountId")
    Optional<User> findUserByUserIdAndAccountId(int userId, String accountId);
}
