package com.breadwallet.tools.util;

public enum CustomEvent {
    _20191105_AL("APP_LAUNCHED")
    , _20191105_VSC("VISIT_SEND_CONTROLLER")
    , _20202116_VRC("VISIT_RECEIVE_CONTROLLER")
    , _20191105_DSL("DID_SEND_LTC")
    , _20191105_DULP("DID_UPDATE_LTC_PRICE")
    , _20191105_DTBT("DID_TAP_BUY_TAB")
    , _20200111_DEDG("DID_ENTER_DISPATCH_GROUP")
    , _20200111_DLDG("DID_LEAVE_DISPATCH_GROUP")
    , _20200111_RNI("RATE_NOT_INITIALIZED")
    , _20200111_FNI("FEEPERKB_NOT_INITIALIZED")
    , _20200111_TNI("TRANSACTION_NOT_INITIALIZED")
    , _20200111_WNI("WALLET_NOT_INITIALIZED")
    , _20200111_PNI("PHRASE_NOT_INITIALIZED")
    , _20200111_UTST("UNABLE_TO_SIGN_TRANSACTION")
    , _20200112_ERR("ERROR")
    , _20200112_DSR("DID_START_RESYNC")
    , _20200125_DSRR("DID_SHOW_REVIEW_REQUEST")
    , _20200217_DLWP("DID_LOGIN_WITH_PIN")
    , _20200217_DLWB("DID_LOGIN_WITH_BIOMETRICS")
    , _20200223_DD("DID_DONATE")
    , _20200225_DCD("DID_CANCEL_DONATE")
    , _20200301_DUDFPK("DID_USE_DEFAULT_FEE_PER_KB");

    private final String text;

    /**
     * @param text
     */
    private CustomEvent(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}