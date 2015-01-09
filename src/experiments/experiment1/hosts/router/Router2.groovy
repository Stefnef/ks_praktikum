package experiments.experiment1.hosts.router

/**
 * Created by root on 09.01.15.
 */
class Router2 extends Router{


    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Router-Klasse instanziieren
        Router2 application = new Router2()
        // Routernummer
        application.routerNr = "router2"
        // und starten
        application.router()


    }
    //------------------------------------------------------------------------------

}