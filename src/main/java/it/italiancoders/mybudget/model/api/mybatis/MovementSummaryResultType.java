package it.italiancoders.mybudget.model.api.mybatis;

import it.italiancoders.mybudget.model.api.MovementType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder(builderMethodName = "newBuilder")
@Getter
@Setter
@AllArgsConstructor
public class MovementSummaryResultType {
    private String categoryId;

    private Double total;

    private MovementType type;

    public MovementSummaryResultType(){}

}
