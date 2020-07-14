package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.model.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findBySerialNumber(String serialNumber);



}
