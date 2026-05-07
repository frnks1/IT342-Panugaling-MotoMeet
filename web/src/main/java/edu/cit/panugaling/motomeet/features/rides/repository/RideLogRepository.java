package edu.cit.panugaling.motomeet.features.rides.repository;

import edu.cit.panugaling.motomeet.features.rides.model.RideLog;
import edu.cit.panugaling.motomeet.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideLogRepository extends JpaRepository<RideLog, Long> {
    List<RideLog> findByOwnerOrderByRideDateDesc(User owner);
}
