package Handlers;

import Entity.Coctails;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class GetHistoryHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String requestMethod = exchange.getRequestMethod();
        String str = "";
        int rCode = 200;
        String response = "";
        String path = exchange.getRequestURI().getPath();
        String name = path.substring(path.lastIndexOf("/") + 1);
        name = name.replace("+"," ");

        System.out.println(name);

        if (requestMethod.equalsIgnoreCase("GET")) {
            //ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder requestBody = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            try {
                // Запрос, чтобы узнать дату
                TypedQuery<Coctails> queryDate = session.createQuery(
                        "SELECT d.history\n" +
                                "FROM Description d \n"+
                                "JOIN Coctails c ON c.des_id = d.des_id\n" +
                                "WHERE c.name = :name"
                );
                queryDate.setParameter("name",name);
                List<String> result = new ArrayList<>();

                response = queryDate.getResultList().toString();

                transaction.commit();
            } catch (Exception e){
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
                rCode = 400;
                str = e.getMessage();
            } finally {
                if (session != null) {
                    session.close();
                    sessionFactory.close();
                }

            }


        } else {
            session.close();
            sessionFactory.close();
            rCode = 400;
            str = "method of requrest is wrong";
        }

        //response.put("message", str);
        byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(rCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
