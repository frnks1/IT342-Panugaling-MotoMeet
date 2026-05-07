package edu.cit.panugaling.motomeet.features.meetups.repository;

import edu.cit.panugaling.motomeet.features.meetups.model.Meetup;
import edu.cit.panugaling.motomeet.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {
    List<Meetup> findByOwnerOrderByMeetupDateAscMeetupTimeAsc(User owner);
}
