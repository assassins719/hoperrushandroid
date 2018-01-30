package com.hoperrush.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Casperon Technology on 1/4/2016.
 */
public class CurrencySymbolConverter {
    public static String getCurrencySymbol(String code) {
        return code2symbol.get(code.toUpperCase());
    }

    public static Map<String, String> getAll() {
        return code2symbol;
    }

    private static Map<String, String> code2symbol = new HashMap<String, String>();

    static {
        code2symbol.put("USD", "$");
        code2symbol.put("CAD", "CA$");
        code2symbol.put("EUR", "€");
        code2symbol.put("AED", "AED");
        code2symbol.put("AFN", "Af");
        code2symbol.put("ALL", "ALL");
        code2symbol.put("AMD", "AMD");
        code2symbol.put("ARS", "AR$");
        code2symbol.put("AUD", "AU$");
        code2symbol.put("AZN", "man.");
        code2symbol.put("BAM", "KM");
        code2symbol.put("BDT", "Tk");
        code2symbol.put("BGN", "BGN");
        code2symbol.put("BHD", "BD");
        code2symbol.put("BIF", "FBu");
        code2symbol.put("BND", "BN$");
        code2symbol.put("BOB", "Bs");
        code2symbol.put("BRL", "R$");
        code2symbol.put("BWP", "BWP");
        code2symbol.put("BYR", "BYR");
        code2symbol.put("BZD", "BZ$");
        code2symbol.put("CDF", "CDF");
        code2symbol.put("CHF", "CHF");
        code2symbol.put("CLP", "CL$");
        code2symbol.put("CNY", "CN¥");
        code2symbol.put("COP", "CO$");
        code2symbol.put("CRC", "₡");
        code2symbol.put("CVE", "CV$");
        code2symbol.put("CZK", "Kč");
        code2symbol.put("DJF", "Fdj");
        code2symbol.put("DKK", "Dkr");
        code2symbol.put("DOP", "RD$");
        code2symbol.put("DZD", "DA");
        code2symbol.put("EEK", "Ekr");
        code2symbol.put("EGP", "EGP");
        code2symbol.put("ERN", "Nfk");
        code2symbol.put("ETB", "Br");
        code2symbol.put("GBP", "£");
        code2symbol.put("GEL", "GEL");
        code2symbol.put("GHS", "GH₵");
        code2symbol.put("GNF", "FG");
        code2symbol.put("GTQ", "GTQ");
        code2symbol.put("HKD", "HK$");
        code2symbol.put("HNL", "HNL");
        code2symbol.put("HRK", "kn");
        code2symbol.put("HUF", "Ft");
        code2symbol.put("IDR", "Rp");
        code2symbol.put("ILS", "₪");
        code2symbol.put("INR", "₹");
        code2symbol.put("IQD", "IQD");
        code2symbol.put("IRR", "IRR");
        code2symbol.put("ISK", "Ikr");
        code2symbol.put("JMD", "J$");
        code2symbol.put("JOD", "JD");
        code2symbol.put("JPY", "¥");
        code2symbol.put("KES", "Ksh");
        code2symbol.put("KHR", "KHR");
        code2symbol.put("KMF", "CF");
        code2symbol.put("KRW", "₩");
        code2symbol.put("KWD", "KD");
        code2symbol.put("KZT", "KZT");
        code2symbol.put("LBP", "LB£");
        code2symbol.put("LKR", "SLRs");
        code2symbol.put("LTL", "Lt");
        code2symbol.put("LVL", "Ls");
        code2symbol.put("LYD", "LD");
        code2symbol.put("MAD", "MAD");
        code2symbol.put("MDL", "MDL");
        code2symbol.put("MGA", "MGA");
        code2symbol.put("MKD", "MKD");
        code2symbol.put("MMK", "MMK");
        code2symbol.put("MOP", "MOP$");
        code2symbol.put("MUR", "MURs");
        code2symbol.put("MXN", "MX$");
        code2symbol.put("MYR", "RM");
        code2symbol.put("MZN", "MTn");
        code2symbol.put("NAD", "N$");
        code2symbol.put("NGN", "₦");
        code2symbol.put("NIO", "C$");
        code2symbol.put("NOK", "Nkr");
        code2symbol.put("NPR", "NPRs");
        code2symbol.put("NZD", "NZ$");
        code2symbol.put("OMR", "OMR");
        code2symbol.put("PAB", "B/.");
        code2symbol.put("PEN", "S/.");
        code2symbol.put("PHP", "₱");
        code2symbol.put("PKR", "PKRs");
        code2symbol.put("PLN", "zł");
        code2symbol.put("PYG", "₲");
        code2symbol.put("QAR", "QR");
        code2symbol.put("RON", "RON");
        code2symbol.put("RSD", "din.");
        code2symbol.put("RUB", "RUB");
        code2symbol.put("RWF", "RWF");
        code2symbol.put("SAR", "SR");
        code2symbol.put("SDG", "SDG");
        code2symbol.put("SEK", "Skr");
        code2symbol.put("SGD", "S$");
        code2symbol.put("SOS", "Ssh");
        code2symbol.put("SYP", "SY£");
        code2symbol.put("THB", "฿");
        code2symbol.put("TND", "DT");
        code2symbol.put("TOP", "T$");
        code2symbol.put("TRY", "TL");
        code2symbol.put("TTD", "TT$");
        code2symbol.put("TWD", "NT$");
        code2symbol.put("TZS", "TSh");
        code2symbol.put("UAH", "₴");
        code2symbol.put("UGX", "USh");
        code2symbol.put("UYU", "$U");
        code2symbol.put("UZS", "UZS");
        code2symbol.put("VEF", "Bs.F.");
        code2symbol.put("VND", "₫");
        code2symbol.put("XAF", "FCFA");
        code2symbol.put("XOF", "CFA");
        code2symbol.put("YER", "YR");
        code2symbol.put("ZAR", "R");
        code2symbol.put("ZMK", "ZK");
    }
}
