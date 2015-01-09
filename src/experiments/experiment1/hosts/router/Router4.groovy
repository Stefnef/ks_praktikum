package experiments.experiment1.hosts.router

/**
 * Created by root on 09.01.15.
 */
class Router4 extends Router{


    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Router-Klasse instanziieren
        Router4 application = new Router4()
        // Routernummer
        application.routerNr = "router4"
        // und starten
        application.router()


    }
    //------------------------------------------------------------------------------

}
