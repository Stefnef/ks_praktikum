package experiments.experiment1.stack.fsm

/**
 * Definition der möglichen Ereignisse, die zu Zustandänderungen der FSM führen können.
 */
class Event {
    /** Verbindungseröffnung einleiten*/
    static final int E_CONN_REQ = 100

    /** SYN senden */
    static final int E_SEND_SYN = 110

    /** SYN empfangen */
    static final int E_RCVD_SYN = 120

    /** FIN senden */
    static final int E_SEND_FIN = 130

    /** FIN+ACK senden */
    static final int E_SEND_FIN_ACK = 135

    /** SYN+ACK senden */
    static final int E_SEND_SYN_ACK = 140

    /** Daten senden */
    static final int E_SEND_DATA = 150

    /** SYN+ACK wurde empfangen */
    static final int E_RCVD_SYN_ACK = 160

    /** SYN+ACK+ACK wurde empfangen */
    static final int E_RCVD_SYN_ACK_ACK = 165

    /** Daten wurden empfangen */
    static final int E_RCVD_DATA = 170

    /** ACK wurde empfangen */
    static final int E_RCVD_ACK = 180

    /** ACK zur Verbindungseröffnung wurde gesendet */
    static final int E_SYN_ACK_ACK_SENT = 190

    /** FIN+ACK+ACK wurde empfangen */
    static final int E_RCVD_FIN_ACK_ACK = 200

    /** FIN+ACK wurde empfangen */
    static final int E_FIN_ACK_ACK_SENT = 210

    /** Bereitschaft */
    static final int E_READY = 220

    /** Verbindungsbeendigung einleiten */
    static final int E_DISCONN_REQ = 230

    /** Daten wurden gesendet */
    static final int E_DATA_SENT = 240

    /** FIN empfangen */
    static final int E_RCVD_FIN = 280

    static String s(int event) {
        switch (event) {
            case (E_CONN_REQ) : return "E_CONN_REQ"
            case (E_SEND_SYN) : return "E_SEND_SYN"
            case (E_RCVD_SYN) : return "E_RCVD_SYN"
            case (E_SEND_FIN) : return "E_SEND_FIN"
            case (E_SEND_FIN_ACK) : return "E_SEND_FIN_ACK"
            case (E_SEND_SYN_ACK) : return "E_SEND_SYN_ACK"
            case (E_SEND_DATA) : return "E_SEND_DATA"
            case (E_RCVD_SYN_ACK) : return "E_RCVD_SYN_ACK"
            case (E_RCVD_SYN_ACK_ACK) : return "E_RCVD_SYN_ACK_ACK"
            case (E_RCVD_DATA) : return "E_RCVD_DATA"
            case (E_RCVD_ACK ) : return "E_RCVD_ACK"
            case (E_SYN_ACK_ACK_SENT) : return "E_SYN_ACK_ACK_SENT"
            case (E_RCVD_FIN_ACK_ACK) : return "E_RCVD_FIN_ACK_ACK"
            case (E_FIN_ACK_ACK_SENT) : return "E_FIN_ACK_ACK_SENT"
            case (E_READY) : return "E_READY"
            case (E_DISCONN_REQ) : return "E_DISCONN_REQ"
            case (E_DATA_SENT) : return "E_DATA_SENT"
            case (E_RCVD_FIN): return "E_RCVD_FIN"
        }
        return "Event unbekannt"
    }

}
