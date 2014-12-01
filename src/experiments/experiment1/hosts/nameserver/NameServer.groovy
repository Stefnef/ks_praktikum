package experiments.experiment1.hosts.nameserver

import common.utils.Utils
import java.util.regex.Matcher

/**
 * Ein Server der Gerätenamen in IPv4-Adressen auflöst. Als Transport-Protokoll wird UDP verwendet.
 */
class NameServer {

    //========================================================================================================
    // Vereinbarungen ANFANG
    //========================================================================================================

    /** Der Netzwerk-Protokoll-Stack */
    experiments.experiment1.stack.Stack stack

    /** Konfigurations-Objekt */
    ConfigObject config

    /** Stoppen der Threads wenn false */
    Boolean run = true

    /** Tabelle zur Umsetzung von Namen in IP-Adressen */
    Map<String, String> nameTable = [
            "webserver.local": "192.168.1.80",
            "alice": "192.168.1.120",
            "bob": "192.168.1.110",
    ]

    /** IP-Adresse und Portnummer des client */
    String srcIpAddr
    int srcPort

    /** Eigene Portnummer */
    int ownPort

    /** Anwendungsprotokolldaten als String */
    String data

    /** Ein Matcher-Objekt zur Verwendung regulärer Ausdruecke */
    Matcher matcher


    //========================================================================================================
    // Methoden ANFANG
    //========================================================================================================

    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Client-Klasse instanziieren
        NameServer application = new NameServer()
        // und starten
        application.nameserver()
    }
    //------------------------------------------------------------------------------

    /**
     * Der Namens-Dienst
     */
    void nameserver() {

        //------------------------------------------------

        // Konfiguration holen
        config = Utils.getConfig("experiment1", "nameserver")

        // ------------------------------------------------------------

        // Netzwerkstack initialisieren
        stack = new experiments.experiment1.stack.Stack()
        stack.start(config)

        Utils.writeLog("NameServer", "nameserver", "startet", 1)

        while (run) {
            // Hier Protokoll implementieren:
            // auf Empfang ueber UDP warten
            // Namen über nameTable in IP-Adresse aufloesen
            // IP-Adresse ueber UDP zuruecksenden
            // Auf Empfang warten

            (srcIpAddr, srcPort, data) = stack.udpReceive()



            // Abbruch wenn Länge der empfangenen Daten == 0
            if (!data)
                break
            Utils.writeLog("NameServer", "nameserver", "empfängt von $srcIpAddr:$srcPort: $data ", 3)
            //matcher = (data =~ /(.*?)\s*/)
            //Utils.writeLog("NameServer", "nameserver", "empfängt: $matcher", 1)

            String reply;


            reply = nameTable[data]

            Utils.writeLog("NameServer", "nameserver", "sendet: $reply", 3)

            // Antwort senden
            stack.udpSend(dstIpAddr: srcIpAddr, dstPort: srcPort, srcPort: ownPort, sdu: reply)
        }
    }
    //------------------------------------------------------------------------------
}
