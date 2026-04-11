package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.RideLog;
import edu.cit.panugaling.motomeet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RideLogRepository extends JpaRepository<RideLog, Long> {
    List<RideLog> findByOwnerOrderByRideDateDesc(User owner);
}
