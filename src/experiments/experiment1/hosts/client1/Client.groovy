#!/usr/bin/env groovy

package experiments.experiment1.hosts.client1

import common.utils.Utils
import experiments.experiment1.stack.Stack
import java.util.regex.Matcher

/**
 * Einfacher HTTP-Client.<br/>
 * Sendet einen HTTP-GET-Request an einen HTTP-Server und gibt das empfangene Dokument an das Terminal aus.<br/>
 * Zum Transport wird UDP verwendet. Der Client ist dadurch nicht für die Kommunikation mit realen HTTP-Servern <br/>
 * verwendbar, da diese stets TCP verwenden!
 */
class Client {

    //========================================================================================================
    // Vereinbarungen ANFANG
    //========================================================================================================

    /** Der Netzwerk-Protokoll-Stack */
    Stack stack

    /** Konfiguration des Geräts.<br/>
      *  Erster Parameter: der Name des Verzeichnisses, der den Versuch enthält<br/>
      *  Zweiter Parameter: Name der Konfiguation fuer dieses Gerät in der Konfigurationsdatei
      */
    ConfigObject config = Utils.getConfig("experiment1", "client")

    /** Stoppen der Threads wenn false */
    Boolean run = true

    //------------------------------------------------------------------------------

    /** Ziel-IP-Adresse */
    String serverIpAddr
    String nameServerIpAddr

    /** Zielportadresse */
    int serverPort
    int nameServerPort

    /** HTTP-Header fuer GET-Request **/
    String request =
            """\
GET /${config.document} HTTP/1.1
Host: www.sesam-strasse.com

"""
    /** DNS-Request **/
    String dns_request = config.serverName

    /** Eigene Portadresse */
    int ownPort
    int ownDnsPort

    /** Anwendungs-PDU */
    String apdu

    /** Anwendungsprotokolldaten als String */
    String data

    /** Länge des HTTP-Body's */
    int bodyLength = 0

    /** Beginn des HTTP-Body's */
    int bodyStart = 0

    /** Aktuelle Länge des HTTP-Body's */
    int curBodyLength = -1

    // Zustände der Protokollmaschine
    /** Zustand: Datenlänge bestimmen */
    final int WAIT_LENGTH = 100
    /** Zustand: Leerzeile (Double NewLine) feststellen */
    final int WAIT_DNL = 200
    /** Zustand: restliche Daten erwarten */
    final int WAIT_DATA = 300
    /** Zustand der Protokollmaschine */
    int state

    /** Behandlung Regulärer Ausdruecke */
    Matcher matcher

    /** ID der TCP-Verbindung, wenn 0: Fehler */
    int connId = 0

    //========================================================================================================
    // Hauptprogramm ANFANG
    //========================================================================================================

    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Client-Klasse instanziieren
        Client application = new Client()
        // und starten
        application.client()
    }

    //------------------------------------------------------------------------------

    /**
     * Ein HTTP-Client mit rudimentärer Implementierung des Protokolls HTTP (Hypertext Transfer Protocol).<br/>
     * Verwendet das TCP-Protokoll.
     */
    void client() {

        // Empfangene Daten
        String rdata

        // ------------------------------------------------------------

        // IPv4-Adresse und Portnummer des HTTP-Dienstes
        serverIpAddr = config.serverIpAddr
        serverPort = config.serverPort

        // Eigener UDP-Port
        ownPort = config.ownPort

        // IPv4-Adresse und Portnummer des DNS
        nameServerIpAddr = config.nameServerIpAddr
        nameServerPort = config.nameServerPort


        // Netzwerkstack initialisieren
        stack = new Stack()
        stack.start(config)

        // ------------------------------------------------------------

        Utils.writeLog("Client", "client", "startet", 1)

        // ------------------------------------------------------------




        // Datenempfang vorbereiten
        data = ""
        state = WAIT_LENGTH

        String d1, d2
        // dummies

        if (nameServerIpAddr) {
            //sende DNS-Anfrage an Nameserver
            stack.udpSend(dstIpAddr: nameServerIpAddr, dstPort: nameServerPort, srcPort: ownPort, sdu: dns_request)

            //empfange IP-Adresse vom Nameserver

            (d1, d2, rdata) = stack.udpReceive()

            Utils.writeLog("Client", "client", "empfängt IP-Adresse: $rdata", 1)

            //ersetze serverIpAress mit IP-Adresse vom Nameserver
            if (rdata) serverIpAddr = rdata
        }

        //return

        Utils.writeLog("Client", "client", "sendet HTTP-Request: ${request}", 1)
        // HTTP-GET-Request absenden
        stack.udpSend(dstIpAddr: serverIpAddr, dstPort: serverPort, srcPort: ownPort, sdu: request)

        // Empfang
        while (curBodyLength < bodyLength) {

            // Auf Empfang warten
            (d1, d2, rdata) = stack.udpReceive()

            Utils.writeLog("Client", "client", "empfängt: $rdata", 1)

            // Daten ergänzen
            data += rdata

            // Empfangene Daten verarbeiten
            handleData()

        } // while

        if (data) Utils.writeLog("Client", "client", "HTTP-Body empfangen: ${data[bodyStart..-1]}", 1)
    }

    //------------------------------------------------------------------------------

    /**
     * Fügt empfangene Daten zusammen
     */
    void handleData() {
        if ( state == WAIT_LENGTH ) {
            // Suchen nach Header-Feld "Content-Length"
            matcher = (data =~ /Content-Length:\s*(\d+)\D/)

            // Wurde das Header-Feld gefunden?
            if (matcher) {
                // Ja
                // Länge des HTTP-Body's holen
                bodyLength = (matcher[0] as List<String>)[1].toInteger()
                state = WAIT_DNL
            }
        }

        if ( state == WAIT_DNL ) {

            // Suchen nach Leerzeile (HTTP-Header-Ende)
            matcher = (data =~ /\n\n|\r\r|\r\n\r\n/)

            // Wurde die Leerzeile gefunden?
            if (matcher) {
                // Ja, Beginn des HTTP-Body's gefunden

                bodyStart = matcher.start() + 2 // Index (Anfang) des HTTP-Body's
                // Bei UTF-8 Encoding anstatt data.size() besser: data.bytes.size()
                curBodyLength = data.bytes.size() - bodyStart
                state = WAIT_DATA
            }
        } else if ( state == WAIT_DATA ) {
            curBodyLength = data.bytes.size() - bodyStart
        }
    }
}
