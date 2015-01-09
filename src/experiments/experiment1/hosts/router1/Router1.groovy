package experiments.experiment1.hosts.router1

import common.utils.Utils

/**
 * Ein IPv4-Router.<br/>
 * Nur als Ausgangspunkt für eigene Implementierung zu verwenden!<br/>
 * Verwendet UDP zur Verteilung der Routinginformationen.
 *
 */
class Router1 {

    //========================================================================================================
    // Vereinbarungen ANFANG
    //========================================================================================================

    /** Der Netzwerk-Protokoll-Stack */
    experiments.experiment1.stack.Stack stack

    /** Konfigurations-Objekt */
    ConfigObject config

    /** Stoppen der Threads wenn false */
    Boolean run = true

    /** Tabelle der IP-Adressen und UDP-Ports der Nachbarrouter */
    /*  z.B. [["1.2.3.4", 11],["5,6,7.8", 20]]
     */
    List<List> neighborTable

    /** Eine Arbeitskopie der Routingtabelle der Netzwerkschicht */
    List routingTable

    /** Eigene IP-Adressen (eine IPv4-Adresse je Anschluss) */
    Map<String, String> ownIpAddrs = [:]

    //========================================================================================================
    // Methoden ANFANG
    //========================================================================================================

    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Router-Klasse instanziieren
        Router1 application = new Router1()
        // und starten
        application.router()


    }
    //------------------------------------------------------------------------------

    //------------------------------------------------------------------------------
    /**
     * Einfacher IP-v4-Forwarder.<br/>
     * Ist so schon funktiionsfähig, da die Wegewahl im Netzwerkstack erfolgt<br/>
     * Hier wird im Laufe des Versuchs ein Routing-Protokoll implementiert.
     */
    void router() {

        // Konfiguration holen
        config = Utils.getConfig("experiment1", "router1")
        neighborTable = config.neighborTable

        config.networkConnectors.each { conn ->
            ownIpAddrs[conn.lpName] = conn.ipAddr

        }
        // ------------------------------------------------------------

        // Netzwerkstack initialisieren
        stack = new experiments.experiment1.stack.Stack()
        stack.start(config)

        // ------------------------------------------------------------

        // Thread zum Empfang von Routinginformationen erzeugen
        Thread.start{receiveFromNeigbor()}

        // ------------------------------------------------------------

        Utils.writeLog("Router1", "router1", "startet", 1)

        while (run) {
            // Periodisches Versenden von Routinginformationen
            sendPeriodical()
            sleep(config.periodRInfo)
        }
    }

    // ------------------------------------------------------------

    /**
     * Wartet auf Empfang von Routinginformationen
     *
     */
    void receiveFromNeigbor() {
        /** IP-Adresse des Nachbarrouters */
        String iPAddr

        /** UDP-Portnummer des Nachbarrouters */
        int port

        /** Empfangene Routinginformationen */
        String rInfo

        while (true) {
            // Auf UDP-Empfang warten
            (iPAddr, port, rInfo) = stack.udpReceive()

            Utils.writeLog("Router", "router1", "empfängt von $iPAddr:$port: $rInfo ", 3)

            //Jetzt aktuelle Routingtablle holen:
            //rt = stack.getRoutingtable()
            //Utils.writeLog("Router", "router1", "holen aus Routingtabelle: $rt", 3)
            //neue Routinginformationen bestimmen
            //ist der nextHop der empf. Route Teil meiner Netze
            //nein, verwerfe Route
            //ja, füge Route zur Routingtabelle hinzu

            //zum Zerlegen einer Zeichenkette siehe "tokenize()"

            //extrahieren von Information, dann iInfo als !Zeichenkette! erzeugen ...
            //Routingtabelle an Vermittlungsschicht uebergeben:
            //stack.setRoutingtable(rt)
            //und neue Routinginformationen verteilen:
            //rInfo = ...
            //sendToNeigbors(rInfo)
            //oder periodisch verteilen lassen
        }
    }
    // ------------------------------------------------------------

    //stantsch: ermittelt ob keine direkte Route existiert
    boolean existsDirectRouting(String ipAdress){

        return true
    }

    // ------------------------------------------------------------

    /** Periodisches Senden der Routinginformationen */
    void sendPeriodical() {
        List neigbr
        String rInfo
        // Paket mit Routinginformationen packen
        // ... z.B.
        routingTable = stack.getRoutingTable()
        Utils.writeLog("Router", "router1", " hat Routing TABELLE $routingTable", 3)

        rInfo = ""
        for (List route in routingTable) {
            //Utils.writeLog("Router1", "router1", " Routing-Eintrag: ${route[0]} - ${route[1]} - ${route[2]} - ${route[3]}", 3)
            //für jede Route prüfe, ob Nachbar existiert
            neigbr = neighborTable.find {
                entry -> Utils.getNetworkId(entry[0], route[1] as String) == route[0]
            }
            //ja ist Backbone, nicht propagieren
            //nein, kein Backbone - Route senden (und damit nur Netz1 und Netz2 propagieren aber kein Backbone)
            if (!neigbr) {
                Utils.writeLog("Router1", "router1", " Propagiere Route: ${route}", 3)
                //senden, Router ist nextHop für dieses jeweilige Netz
                //192.168.1.0/24 10.10.1.1 lp2
                //192.168.1.0/24 10.10.4.2 lp5
                //192.168.2.0/24 10.10.1.1 lp5
                //192.168.2.0/24 10.10.4.2 lp2

                for (List neigbor in neighborTable) {
                        rInfo += "[${route[0]}, ${route[1]}, bbIPs, lpX],"
                }
            }


        }

        // extrahieren von Information, dann iInfo als !Zeichenkette! erzeugen ...
        //rInfo = "inf1a, inf1b, ..., inf2a, inf2b, ..."

        // Zum Senden uebergeben
        sendToNeigbors(rInfo)
    }

    // ------------------------------------------------------------

    /** Senden von Routinginformationen an alle Nachbarrouter
     *
     * @param rInfo - Routing-Informationen
     */

    void sendToNeigbors(String rInfo) {
        // rInfo an alle Nachbarrouter versenden
        for (List neigbor in neighborTable) {
            Utils.writeLog("Router", "router1", "sende an Nachbar: ${neigbor[0]} Info: $rInfo", 3)
            stack.udpSend(dstIpAddr: neigbor[0], dstPort: neigbor[1], srcPort: config.ownPort, sdu: rInfo)
        }
    }
    //------------------------------------------------------------------------------
}

