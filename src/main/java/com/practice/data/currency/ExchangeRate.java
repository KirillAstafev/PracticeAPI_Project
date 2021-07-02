package com.practice.data.currency;

import javax.xml.bind.annotation.XmlElement;

public class ExchangeRate {
    @XmlElement
    private String baseCurrency;
    @XmlElement
    private String currency;
    @XmlElement
    private Double saleRateNB;
    @XmlElement
    private Double purchaseRateNB;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getCurrency() {
        return currency;
    }

    public Double getSaleRateNB() {
        return saleRateNB;
    }

    public Double getPurchaseRateNB() {
        return purchaseRateNB;
    }
}
