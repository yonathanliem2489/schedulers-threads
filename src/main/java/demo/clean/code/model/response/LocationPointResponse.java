package demo.clean.code.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@ToString
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationPointResponse implements Serializable {
  List<LocationPointsData> boardingPoints;
  List<LocationPointsData> droppingPoints;
}
