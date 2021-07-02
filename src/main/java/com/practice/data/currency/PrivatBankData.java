package com.practice.data.currency;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "privatbankdata")
public class PrivatBankData {
    @XmlElement(name = "currencydata")
    private List<CurrencyData> currencyDataList;

    public List<CurrencyData> getCurrencyDataList() {
        return currencyDataList;
    }
}
