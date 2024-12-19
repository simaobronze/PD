package ServidorPrincipal;

public class Payment {
    private String groupName;
    private String payerName;
    private String receiverName;
    private double amount;

    public Payment (String groupName, String payerName, String receiverName, double amount) {
        this.groupName = groupName;
        this.payerName = payerName;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
