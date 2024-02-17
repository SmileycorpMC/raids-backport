package net.smileycorp.raids.common.interfaces;

public interface ITradeDiscount {
    
    int getDiscountedPrice();
    
    void setDiscountedPrice(int price);
    
    boolean hasDiscount();
    
}
