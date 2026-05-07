package edu.cit.panugaling.motomeet.features.garage.repository;

import edu.cit.panugaling.motomeet.features.garage.model.Bike;
import edu.cit.panugaling.motomeet.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByOwnerOrderByActiveDescDisplayNameAsc(User owner);

    Optional<Bike> findByOwnerAndActiveTrue(User owner);
}
