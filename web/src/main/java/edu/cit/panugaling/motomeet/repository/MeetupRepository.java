package edu.cit.panugaling.motomeet.repository;

import edu.cit.panugaling.motomeet.model.Meetup;
import edu.cit.panugaling.motomeet.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {
    List<Meetup> findByOwnerOrderByMeetupDateAscMeetupTimeAsc(User owner);
}
