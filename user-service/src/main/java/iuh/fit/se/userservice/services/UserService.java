package iuh.fit.se.userservice.services;

import iuh.fit.se.userservice.entities.User;

public interface UserService {
    User findByUserName(String userName);
    User saveUser(User user);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    User findById(Long id);

    boolean findByUserAdmin(String userName);
}
