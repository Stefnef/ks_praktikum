package experiments.experiment1.hosts.router

import experiments.experiment1.hosts.router.Router

/**
 * Created by root on 09.01.15.
 */
class Router1 extends Router{


    //------------------------------------------------------------------------------
    /**
     * Start der Anwendung
     */
    static void main(String[] args) {
        // Router-Klasse instanziieren
        Router1 application = new Router1()
        // Routernummer
        application.routerNr = "router1"
        // und starten
        application.router()


    }
    //------------------------------------------------------------------------------

}


