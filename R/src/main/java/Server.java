//import Handlers.*;
import Handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private final int PORT = 5000;
    private final int THREADS = 3;
    private HttpServer httpServer;

    public Server() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), THREADS);
            httpServer.createContext("/coctails/getCoctails", new GetCoctailsHandler());
            httpServer.createContext("/coctails/getRecipe", new GetRecipeHandler());
            httpServer.createContext("/coctails/getHistory", new GetHistoryHandler());
            httpServer.createContext("/user/postUser", new postUserHandler());
            httpServer.createContext("/user/getUser", new getUserHandler());
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
