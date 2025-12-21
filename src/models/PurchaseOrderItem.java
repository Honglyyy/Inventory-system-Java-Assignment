package models;

public class PurchaseOrderItem {
    private int poItemId;
    private int poId;
    private int productId;
    private String productCode;
    private String productName;
    private int quantityOrdered;
    private double unitCost;
    private double subtotal;

    public PurchaseOrderItem() {
    }

    public int getPoItemId() {
        return poItemId;
    }

    public void setPoItemId(int poItemId) {
        this.poItemId = poItemId;
    }

    public int getPoId() {
        return poId;
    }

    public void setPoId(int poId) {
        this.poId = poId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "PurchaseOrderItem{" +
                "poItemId=" + poItemId +
                ", poId=" + poId +
                ", productId=" + productId +
                ", productCode='" + productCode + '\'' +
                ", productName='" + productName + '\'' +
                ", quantityOrdered=" + quantityOrdered +
                ", unitCost=" + unitCost +
                ", subtotal=" + subtotal +
                '}';
    }
}