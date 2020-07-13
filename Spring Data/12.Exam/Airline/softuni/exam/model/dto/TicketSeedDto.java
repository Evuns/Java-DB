package softuni.exam.model.dto;

import org.hibernate.validator.constraints.Length;
import softuni.exam.adaptor.LocalDateTimeAdaptor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@XmlRootElement(name = "ticket")
@XmlAccessorType(XmlAccessType.FIELD)
public class TicketSeedDto {

    @XmlElement(name = "serial-number")
    private String serialNumber;
    @XmlElement
    private BigDecimal price;
    @XmlElement(name  = "take-off")
    @XmlJavaTypeAdapter(LocalDateTimeAdaptor.class)
    private LocalDateTime takeoff;
    @XmlElement(name = "from-town")
    private TownForTicketDto fromTown;
    @XmlElement(name= "to-town")
    private TownForTicketDto toTown;
    @XmlElement(name = "passenger")
    private PassengerForTicketDto passenger;
    @XmlElement
    private PlaneForTicketDto plane;

    public TicketSeedDto() {
    }

    @Length(min = 2)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @DecimalMin("0")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @NotNull
    public LocalDateTime getTakeoff() {
        return takeoff;
    }

    public void setTakeoff(LocalDateTime takeoff) {
        this.takeoff = takeoff;
    }

    @NotNull
    public TownForTicketDto getFromTown() {
        return fromTown;
    }

    public void setFromTown(TownForTicketDto fromTown) {
        this.fromTown = fromTown;
    }

    @NotNull
    public TownForTicketDto getToTown() {
        return toTown;
    }

    public void setToTown(TownForTicketDto toTown) {
        this.toTown = toTown;
    }

    @NotNull
    public PassengerForTicketDto getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerForTicketDto passenger) {
        this.passenger = passenger;
    }

    @NotNull
    public PlaneForTicketDto getPlane() {
        return plane;
    }

    public void setPlane(PlaneForTicketDto plane) {
        this.plane = plane;
    }
}
