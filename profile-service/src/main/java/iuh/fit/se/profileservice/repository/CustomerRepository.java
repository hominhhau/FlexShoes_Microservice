package iuh.fit.se.profileservice.repository;

import iuh.fit.se.profileservice.entities.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerProfile, Integer> {
}
