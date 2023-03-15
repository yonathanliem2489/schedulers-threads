package demo.clean.code.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tiket.tix.bus.model.common.models.dao.Schedule.AvailableTrip;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @author nandapratama
 */
@SuperBuilder
@ToString
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailableTripsResponse implements Serializable {

  private List<AvailableTrip> availableTrips;
}
