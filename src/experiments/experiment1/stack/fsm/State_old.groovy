package experiments.experiment1.stack.fsm

/**
 * Definition der möglichen Zustände der FSM.
 */
class State_old {
    /** Leerlauf */
    static final int S_IDLE = 100

    /** Auf SYN+ACK wartend */
    static final int S_WAIT_SYN_ACK = 110

    /** Wartend */
    static final int S_READY = 120

    /** Senden von ACK als Abschluß der Verbindungseröffnung */
    static final int S_SEND_SYN_ACK_ACK = 180

    /** Auf FIN+ACK wartend */
    static final int S_WAIT_FIN_ACK = 190

    /** Senden von ACK als Abschluß der Verbindungbeendigung */
    static final int S_SEND_FIN_ACK_ACK = 200

    /** Daten wurden empfangen */
    static final int S_RCVD_DATA = 210

    /** ACK wurde empfangen */
    static final int S_RCVD_ACK = 220

    /** SYN zur Verbindungseröffnung wird gesendet */
    static final int S_SEND_SYN = 230

    /** SYN+ACK wurde gesendet*/
    static final int S_SEND_SYN_ACK = 235

    /** SYN+ACK+ACK warten auf*/
    static final int S_WAIT_SYN_ACK_ACK = 238

    /** SYN zur Verbindungsbeendigung wird gesendet */
    static final int S_SEND_FIN = 240

    /** FIN+ACK senden*/
    static final int S_SEND_FIN_ACK = 245

    /** FIN+ACK+ACk warten*/
    static final int S_WAIT_FIN_ACK_ACK = 248

    /** Daten werden gesendet */
    static final int S_SEND_DATA = 250

    static String s(int state) {
        switch (state) {
            case (S_IDLE) : return "S_IDLE"
            case (S_WAIT_SYN_ACK) : return "S_WAIT_SYN_ACK"
            case (S_READY) : return "S_READY"
            case (S_SEND_SYN_ACK_ACK) : return "S_SEND_SYN_ACK_ACK"
            case (S_WAIT_FIN_ACK) : return "S_WAIT_FIN_ACK"
            case (S_SEND_FIN_ACK_ACK) : return "S_SEND_FIN_ACK_ACK"
            case (S_RCVD_DATA) : return "S_RCVD_DATA"
            case (S_RCVD_ACK ) : return "S_RCVD_ACK"
            case (S_SEND_SYN): return "S_SEND_SYN"
            case (S_SEND_SYN_ACK) : return "S_SEND_SYN_ACK"
            case (S_WAIT_SYN_ACK_ACK) : return "S_WAIT_SYN_ACK_ACK"
            case (S_SEND_FIN) : return "S_SEND_FIN"
            case (S_SEND_FIN_ACK) : return "S_SEND_FIN_ACK"
            case (S_WAIT_FIN_ACK_ACK) : return "S_WAIT_FIN_ACK_ACK"
            case (S_SEND_DATA) : return "S_SEND_DATA"
        }
        return "State unbekannt"
    }

}
