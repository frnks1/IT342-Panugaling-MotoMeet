package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.Bike;
import edu.cit.panugaling.motomeet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByOwnerOrderByActiveDescDisplayNameAsc(User owner);

    Optional<Bike> findByOwnerAndActiveTrue(User owner);
}
