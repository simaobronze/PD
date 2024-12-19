package ServidorPrincipal;

public class Expense {
    private String groupName;
    private String receiverName;
    private String payerName;
    private double value;
    private String description;
    private String status; // paid ou pending

    public Expense(String groupName, String receiverName, String payerName, double value, String description, String status) {
        this.groupName = groupName;
        this.receiverName = receiverName;
        this.payerName = payerName;
        this.value = value;
        this.description = description;
        this.status = status;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
