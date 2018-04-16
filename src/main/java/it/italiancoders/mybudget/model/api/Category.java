package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(builderMethodName = "newBuilder")
@Getter
@Setter
@AllArgsConstructor
public class Category {

    @JsonProperty("type")
    private MovementType type;

    @JsonProperty("id")
    private String id;

    @JsonProperty("value")
    private String value;

    @JsonProperty("isEditable")
    private Boolean isEditable;

    private Integer iconId;

    public Category(){}

}
