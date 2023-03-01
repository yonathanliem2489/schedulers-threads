package demo.clean.code.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RedBusSeatLayoutResponse implements Serializable {

  private Integer departureTime;
  private String currencyType;
  private Integer arrivalTime;
  private String agentCurrency;
  private CategoryDetails categoryDetails;
  private List<Amenities> busAmenities;
  private List<SeatsItem> seats;

  @JsonCreator
  @Builder(toBuilder = true)
  public RedBusSeatLayoutResponse(@JsonProperty("departureTime") Integer departureTime,
      @JsonProperty("currencyType") String currencyType,
      @JsonProperty("arrivalTime") Integer arrivalTime,
      @JsonProperty("agentCurrency") String agentCurrency,
      @JsonProperty("categoryDetails") CategoryDetails categoryDetails,
      @JsonProperty("busAmenities") List<Amenities> busAmenities,
      @JsonProperty("seats") List<SeatsItem> seats) {
    this.departureTime = departureTime;
    this.currencyType = currencyType;
    this.arrivalTime = arrivalTime;
    this.agentCurrency = agentCurrency;
    this.categoryDetails = categoryDetails;
    this.busAmenities = busAmenities;
    this.seats = seats;
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  public static class SeatsItem {

    private BigDecimal fare;
    private BigDecimal serviceTaxAbsolute;
    private BigDecimal baseFare;
    private Integer column;
    private Integer length;
    private Boolean available;
    private Integer operatorServiceChargeAbsolute;
    private BigDecimal serviceTaxPercentage;
    private Boolean ladiesSeat;
    private String seatType;
    private String name;
    private Integer width;
    private BigDecimal operatorServiceChargePercent;
    private Integer row;
    private Integer ziIndex;

    @JsonCreator
    @Builder(toBuilder = true)
    public SeatsItem(@JsonProperty("fare") BigDecimal fare,
        @JsonProperty("serviceTaxAbsolute") BigDecimal serviceTaxAbsolute,
        @JsonProperty("baseFare") BigDecimal baseFare,
        @JsonProperty("column") Integer column,
        @JsonProperty("length") Integer length,
        @JsonProperty("available") Boolean available,
        @JsonProperty("operatorServiceChargeAbsolute") Integer operatorServiceChargeAbsolute,
        @JsonProperty("serviceTaxPercentage") BigDecimal serviceTaxPercentage,
        @JsonProperty("ladiesSeat") Boolean ladiesSeat,
        @JsonProperty("seatType") String seatType, @JsonProperty("name") String name,
        @JsonProperty("width") Integer width,
        @JsonProperty("operatorServiceChargePercent") BigDecimal operatorServiceChargePercent,
        @JsonProperty("row") Integer row, @JsonProperty("zIndex") Integer ziIndex) {
      this.fare = fare;
      this.serviceTaxAbsolute = serviceTaxAbsolute;
      this.baseFare = baseFare;
      this.column = column;
      this.length = length;
      this.available = available;
      this.operatorServiceChargeAbsolute = operatorServiceChargeAbsolute;
      this.serviceTaxPercentage = serviceTaxPercentage;
      this.ladiesSeat = ladiesSeat;
      this.seatType = seatType;
      this.name = name;
      this.width = width;
      this.operatorServiceChargePercent = operatorServiceChargePercent;
      this.row = row;
      this.ziIndex = ziIndex;
    }
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor
  public static class CategoryDetails {

    private List<Object> categories;

    @JsonCreator
    @Builder(toBuilder = true)
    public CategoryDetails(@JsonProperty("categories") List<Object> categories) {
      this.categories = categories;
    }
  }

  @Data
  @Builder(toBuilder = true)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Amenities {

    private Integer row;
    private Integer column;
    private Integer width;
    private Integer length;
    @JsonProperty("zIndex")
    private Integer ziIndex;
    private String amenityCode;

    @JsonCreator
    @Builder(toBuilder = true)
    public Amenities(@JsonProperty("row") Integer row, @JsonProperty("column") Integer column,
        @JsonProperty("width") Integer width, @JsonProperty("length") Integer length,
        @JsonProperty("zIndex") Integer ziIndex, @JsonProperty("amenityCode") String amenityCode) {
      this.row = row;
      this.column = column;
      this.width = width;
      this.length = length;
      this.ziIndex = ziIndex;
      this.amenityCode = amenityCode;
    }
  }
}