package com.practice.data.currency;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.List;

public class CurrencyData {
    @XmlElement
    private String date;
    @XmlElement
    private String bank;

    @XmlElement(name = "exchangeRate")
    private List<ExchangeRate> exchangeRates;

    public String getDate() {
        return date;
    }

    public String getBank() {
        return bank;
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }
}
