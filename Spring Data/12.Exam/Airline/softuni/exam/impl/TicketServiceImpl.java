package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.constant.GlobalConstants;
import softuni.exam.model.dto.TicketRootSeedDto;
import softuni.exam.model.dto.TicketSeedDto;
import softuni.exam.model.Passenger;
import softuni.exam.model.Plane;
import softuni.exam.model.Ticket;
import softuni.exam.model.Town;
import softuni.exam.repository.TicketRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.PlaneService;
import softuni.exam.service.TicketService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class TicketServiceImpl implements TicketService {

    private final ModelMapper modelMapper;
    private final TicketRepository ticketRepository;
    private final ValidationUtil validator;
    private final XmlParser xmlParser;
    private final TownService townService;
    private final PassengerService passengerService;
    private final PlaneService planeService;

    public TicketServiceImpl(ModelMapper modelMapper,
                             TicketRepository ticketRepository, ValidationUtil validator,
                             XmlParser xmlParser, TownService townService,
                             PassengerService passengerService, PlaneService planeService) {
        this.modelMapper = modelMapper;
        this.ticketRepository = ticketRepository;
        this.validator = validator;
        this.xmlParser = xmlParser;
        this.townService = townService;
        this.passengerService = passengerService;
        this.planeService = planeService;
    }

    @Override
    public boolean areImported() {
        return this.ticketRepository.count() > 0;
    }

    @Override
    public String readTicketsFileContent() throws IOException {
        return Files.readString(Path.of(GlobalConstants.TICKETS_FILE_PATH));
    }

    @Override
    public String importTickets() throws JAXBException, FileNotFoundException {
        StringBuilder stringBuilder = new StringBuilder();
        TicketRootSeedDto ticketRootSeedDto = this.xmlParser.parseXml(TicketRootSeedDto.class,
                GlobalConstants.TICKETS_FILE_PATH);

        List<TicketSeedDto> ticketSeedDtos = ticketRootSeedDto.getTickets();
        for (TicketSeedDto ticketSeedDto : ticketSeedDtos) {

            if (this.validator.isValid(ticketSeedDto)) {
                if (this.ticketRepository.findBySerialNumber(
                        (ticketSeedDto.getSerialNumber())) == null) {
                    Ticket ticket = this.modelMapper.map(ticketSeedDto, Ticket.class);
                    Town fromTown = this.townService.getTownByName(ticketSeedDto.getFromTown().getName());
                    Town toTown = this.townService.getTownByName(ticketSeedDto.getToTown().getName());
                    Passenger passenger = this.passengerService.getPassengerByEmail
                            (ticketSeedDto.getPassenger().getEmail());
                    Plane plane = this.planeService.getPlaneByRegisterNumber(ticketSeedDto.getPlane().getRegisterNumber());

                    if(fromTown == null || toTown == null || passenger == null || plane == null){
                        stringBuilder.append(GlobalConstants.INVALID_MESSAGE).append("Ticket");
                    }else {
                        ticket.setFromTown(fromTown);
                        ticket.setToTown(toTown);
                        ticket.setPlane(plane);
                        ticket.setPassenger(passenger);
                        this.ticketRepository.saveAndFlush(ticket);
                        stringBuilder.append(GlobalConstants.SUCCESSFUL_MESSAGE)
                                .append(String.format("Ticket %s - %s", ticket.getFromTown().getName(), ticket.getToTown().getName()));
                    }
                } else {
                    stringBuilder.append("This ticket is already in DB");
                }
            } else {
                stringBuilder.append(GlobalConstants.INVALID_MESSAGE).append("Ticket");
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }


    @Override
    public Ticket getTicketFromSerialNumber(String serialNumber) {
        return this.ticketRepository.findBySerialNumber(serialNumber);
    }
}
