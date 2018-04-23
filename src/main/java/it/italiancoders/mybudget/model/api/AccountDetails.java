package it.italiancoders.mybudget.model.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetails  extends Account{


    @JsonProperty("incomingCategoriesAvailable")
    private List<Category> incomingCategoriesAvailable ;

    @JsonProperty("expenseCategoriesAvailable")
    private List<Category> expenseCategoriesAvailable;

    @JsonProperty("totalMonthlyIncoming")
    private Double totalMonthlyIncoming;

    @JsonProperty("totalMonthlyExpense")
    private Double totalMonthlyExpense;

    @JsonProperty("incomingOverviewMovement")
    private Map<String, Double> incomingOverviewMovement;

    @JsonProperty("expenseOverviewMovement")
    private Map<String, Double> expenseOverviewMovement;


    @JsonProperty("lastMovements")
    private List<Movement> lastMovements;

    @JsonProperty("members")
    private List<User> members;

    @JsonProperty("administrators")
    private List<String> administrators;


    @JsonProperty("numberOfPendingAccountInvites")
    private Integer numberOfPendingAccountInvites;


    public AccountDetails(Account account){
        this.setId(account.getId());
        this.setName(account.getName());
        this.setDescription(account.getDescription());
        this.setStatus(account.getStatus());
        this.setDefaultUsername(account.getDefaultUsername());
        this.setNumberOfUsers(account.getNumberOfUsers());

    }
    @Builder(builderMethodName = "newBuilderExt")
    public AccountDetails(String id, @NotNull String name, String description, AccountStatusEnum status, Integer numberOfUsers, String defaultUsername, List<Category> incomingCategoriesAvalaible, List<Category> expenseCategoriesAvalaible, Double totalMonthlyIncoming, Double totalMonthlyExpense, Map<String, Double> incomingOverviewMovement, Map<String, Double> expenseOverviewMovement, List<Movement> lastMovements, List<User> members, List<String> administrators, Integer numberOfPendingAccountInvites) {
        super(id, name, description, status, numberOfUsers, defaultUsername);
        this.incomingCategoriesAvailable = incomingCategoriesAvalaible;
        this.expenseCategoriesAvailable = expenseCategoriesAvalaible;
        this.totalMonthlyIncoming = totalMonthlyIncoming;
        this.totalMonthlyExpense = totalMonthlyExpense;
        this.incomingOverviewMovement = incomingOverviewMovement;
        this.expenseOverviewMovement = expenseOverviewMovement;
        this.lastMovements = lastMovements;
        this.members = members;
        this.administrators = administrators;
        this.numberOfPendingAccountInvites = numberOfPendingAccountInvites;
    }
}
