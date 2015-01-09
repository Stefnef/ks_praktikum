package experiments.experiment1.hosts.router

/**
 * Created by root on 09.01.15.
 */
class Router3 extends Router{


    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Router-Klasse instanziieren
        Router3 application = new Router3()
        // Routernummer
        application.routerNr = "router3"
        // und starten
        application.router()


    }
    //------------------------------------------------------------------------------

}