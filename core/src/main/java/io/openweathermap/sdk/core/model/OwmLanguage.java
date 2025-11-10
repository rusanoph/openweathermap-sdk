package io.openweathermap.sdk.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwmLanguage {
    SQ("Albanian"),
    AF("Afrikaans"),
    AR("Arabic"),
    AZ("Azerbaijani"),
    EU("Basque"),
    BE("Belarusian"),
    BG("Bulgarian"),
    CA("Catalan"),
    ZH_CN("Chinese Simplified"),
    ZH_TW("Chinese Traditional"),
    HR("Croatian"),
    CZ("Czech"),
    DA("Danish"),
    NL("Dutch"),
    EN("English"),
    FI("Finnish"),
    FR("French"),
    GL("Galician"),
    DE("German"),
    EL("Greek"),
    HE("Hebrew"),
    HI("Hindi"),
    HU("Hungarian"),
    IS("Icelandic"),
    ID("Indonesian"),
    IT("Italian"),
    JA("Japanese"),
    KR("Korean"),
    KU("Kurmanji (Kurdish)"),
    LA("Latvian"),
    LT("Lithuanian"),
    MK("Macedonian"),
    NO("Norwegian"),
    FA("Persian (Farsi)"),
    PL("Polish"),
    PT("Portuguese"),
    PT_BR("Português Brasil"),
    RO("Romanian"),
    RU("Russian"),
    SR("Serbian"),
    SK("Slovak"),
    SL("Slovenian"),
    SP("Spanish"),
    ES("Spanish"),
    SV("Swedish"),
    SE("Swedish"),
    TH("Thai"),
    TR("Turkish"),
    UA("Ukrainian"),
    UK("Ukrainian"),
    VI("Vietnamese"),
    ZU("Zulu")
    ;

    private final String displayName;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
