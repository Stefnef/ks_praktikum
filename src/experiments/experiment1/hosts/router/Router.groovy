package experiments.experiment1.hosts.router

import common.utils.Utils

/**
 * Created by stantsch on 09.01.15.

 * Ein IPv4-Router.<br/>
 * Nur als Ausgangspunkt für eigene Implementierung zu verwenden!
 * Verwendet UDP zur Verteilung der Routinginformationen.
 *
 */

class Router {

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

    // Nummer des Routers
    String routerNr

    int lifeTime

    //========================================================================================================
    // Methoden ANFANG
    //========================================================================================================



    //------------------------------------------------------------------------------
    /**
     * Einfacher IP-v4-Forwarder.<br/>
     * Ist so schon funktiionsfähig, da die Wegewahl im Netzwerkstack erfolgt<br/>
     * Hier wird im Laufe des Versuchs ein Routing-Protokoll implementiert.
     */
    void router() {

        // Konfiguration holen
        config = Utils.getConfig("experiment1", routerNr)
        neighborTable = config.neighborTable
        lifeTime = config.lifeTime

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

        Utils.writeLog("Router", routerNr, "startet", 1)

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

            Utils.writeLog("Router", routerNr, "    |<<<|$iPAddr:$port| Info: $rInfo ", 2)

            // Nachbar Counter zurücksetzen
            for ( List nt in neighborTable){
                if (nt[0] == iPAddr) {
                    nt[3] = lifeTime
                }
            }
            Utils.writeLog("Router", routerNr, "    |***| $neighborTable ", 2)


            //Jetzt aktuelle Routingtablle holen:
            List< List<String> > rt = stack.getRoutingTable()
            List<List<String>> tempRoutingTable = stack.getRoutingTable()
            //Utils.writeLog("Router", routerNr, "holen aus Routingtabelle: $rt", 3)
            //neue Routinginformationen bestimmen

            //ist der nextHop der empf. Route Teil meiner Netze
            //nein, verwerfe Route
            //ja, füge Route zur Routingtabelle hinzu

            //zum Zerlegen einer Zeichenkette siehe "tokenize()"
            def rInfoList = rInfo.tokenize(";")
            //Utils.writeLog("Router", routerNr, "tokenize: $rInfoList", 3)
            for(String newRoutingRowString : rInfoList) {
                def newRoutingRow = newRoutingRowString.tokenize(",")
                //Utils.writeLog("Router", routerNr, "...newRoutingRow: $newRoutingRow ", 3)

                for (List route in rt) {
                    // passt der nextHop zu einem unserer Netze?
                    if (Utils.getNetworkId(newRoutingRow[2], route[1] as String) == route[0]){
                        // finde alle Einträge die in der aktuellen RT, die zu der propagierten Route passen
                        List<List<String>> foundRoutingRows = rt.findAll({
                            routeEntry -> routeEntry[0] == newRoutingRow[0] &&
                                    routeEntry[1] == newRoutingRow[1] })

                        // wenn Eintrag in Routingtabelle bereits vorhanden, dann Metriken vergleichen
                        if(!foundRoutingRows.isEmpty()) {
                            // gehe durch alle passenden Routen in der akt. RT durch
                            for(List<String> oldRoutingRow : foundRoutingRows) {
                                //ist die Metrik aus dem Eintrag in der Routingtabelle schlechter?
                                if (oldRoutingRow[4] > newRoutingRow[4]) {
                                    tempRoutingTable.remove(oldRoutingRow)
                                    tempRoutingTable.add(newRoutingRow)
                                    Utils.writeLog("Router", routerNr, "ersetze $oldRoutingRow durch $newRoutingRow ", 2)
                                } else if ( (oldRoutingRow[4] == newRoutingRow[4])
                                        && (oldRoutingRow != newRoutingRow)){
                                    //sind Metriken identisch und ist es nicht der gleiche Eintrag?
                                    //-> ja => Eintrag zur Routingtabelle hinzufügen
                                    tempRoutingTable.add(newRoutingRow)
                                    Utils.writeLog("Router", routerNr, "|+++| $newRoutingRow (gleiche Metrik)", 2)
                                }
                            }
                        } else {
                            tempRoutingTable.add(newRoutingRow)
                            Utils.writeLog("Router", routerNr, "    |+++| $newRoutingRow ", 2)
                        }
                    }
                }
                tempRoutingTable.unique()
            }

            Utils.writeLog("Router", routerNr, "    |###| $tempRoutingTable", 2)
            stack.setRoutingTable(tempRoutingTable)
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

    /** Periodisches Senden der Routinginformationen */
    void sendPeriodical() {
        List neigbr
        String rInfo, bbip, lp
        int metric
        def foundRoutingRows
        // Paket mit Routinginformationen packen
        // ... z.B.
        routingTable = stack.getRoutingTable()
        Utils.writeLog("Router", routerNr+"sP", "|###| $routingTable", 1)
        Utils.writeLog("Router", routerNr+"sP", "|***| $neighborTable", 1)
        //lösche offline Nachbarn
        // für alle Nachbarn in NT
        for (List neighbour in neighborTable) {
            // ist Nachbar tot?
            //Utils.writeLog("Router", routerNr, "neighbour[3]: ${neighbour[3]}", 2)
            if (!neighbour[3]){
                //ja, Routen löschen
                foundRoutingRows = routingTable.findAll({
                    // alle Einträge mit Anschluss lpX
                    rrow -> rrow[3] == neighbour[2] &&
                    // die nicht Backbone sind
                    Utils.getNetworkId(neighbour[0], rrow[1] as String) != rrow[0]
                })
                Utils.writeLog("Router", routerNr+"sP", "    |---| $foundRoutingRows", 2)
                routingTable.removeAll(foundRoutingRows)
                stack.setRoutingTable(routingTable)

            } else { // nein, runterzählen
                neighbour[3]--
            }
        }

        rInfo = ""
        // für alle Einträge in Routing Tabelle
        for (List route in routingTable) {
            //Utils.writeLog("Router1", "router1", " Routing-Eintrag: ${route[0]} - ${route[1]} - ${route[2]} - ${route[3]}", 3)
            //für jede Route prüfe, ob Nachbar existiert
            neigbr = neighborTable.find {
                entry -> Utils.getNetworkId(entry[0], route[1] as String) == route[0]
            }
            //ja ist Backbone, nicht propagieren
            //nein, kein Backbone - Route senden (und damit nur Netz1 und Netz2 propagieren aber kein Backbone)
            if (!neigbr) {
                Utils.writeLog("Router", routerNr+"sP", "    |ooo| ${route}", 2)
                //senden, Router ist nextHop für dieses jeweilige Netz
                //192.168.1.0/24 10.10.1.1 lp2
                //192.168.1.0/24 10.10.4.2 lp5
                //192.168.2.0/24 10.10.1.1 lp5
                //192.168.2.0/24 10.10.4.2 lp2

                for (List neigbor in neighborTable) {
                    //String test = ownIpAddrs
                    //Utils.writeLog("Router", "test", " >> ${test}", 3)

                    // Anschluss lpX
                    lp = neigbor[2]
                    // Backbone IP-Adress behind
                    bbip = ownIpAddrs[lp]
                    //metric
                    metric = (route[4] as int)+1
                    if (rInfo)
                        rInfo += ";"
                    rInfo += "${route[0]},${route[1]},${bbip},${lp},${metric}"
                }
            }


        }

        // extrahieren von Information, dann iInfo als !Zeichenkette! erzeugen ...
        //rInfo = "inf1a, inf1b, ..., inf2a, inf2b, ..."

        // Zum Senden uebergeben
        if (rInfo)
            sendToNeigbors(rInfo)
        else
            Utils.writeLog("Router", routerNr+"sP", "    |ooo| ...", 2)
    }

    // ------------------------------------------------------------

    /** Senden von Routinginformationen an alle Nachbarrouter
     *
     * @param rInfo - Routing-Informationen
     */

    void sendToNeigbors(String rInfo) {
        // rInfo an alle Nachbarrouter versenden
        for (List neigbor in neighborTable) {
            Utils.writeLog("Router", routerNr, "    |>>>|${neigbor[0]}:${neigbor[1]}| Info: $rInfo", 2)
            stack.udpSend(dstIpAddr: neigbor[0], dstPort: neigbor[1], srcPort: config.ownPort, sdu: rInfo)
        }
    }
    //------------------------------------------------------------------------------


}
