package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Builder(builderMethodName = "newBuilder")
@Getter
@Setter
@AllArgsConstructor
public class ScheduledMovementSettings {

    @JsonProperty("id")
    private String id;

    @NotNull
    private String name;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("description")
    private String description;

    @JsonProperty("start")
    private Long start;

    @JsonProperty("end")
    private Long end;

    @JsonIgnore
    private Long nextMovementExecution;

    @JsonProperty("frequency")
    @NotNull
    private ScheduledFrequencyEnum frequency;

    @JsonProperty(value ="account" ,access = JsonProperty.Access.WRITE_ONLY)
    private Account account;

    @JsonProperty("amount")
    @NotNull
    private Double amount;

    @JsonProperty("category")
    private Category category;

    @JsonProperty("user")
    private User user;

    @JsonProperty("movementType")
    @NotNull
    private MovementType type;

    public ScheduledMovementSettings(){}

}
