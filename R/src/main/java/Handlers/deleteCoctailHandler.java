package Handlers;

import Entity.Description;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.TypedQuery;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class deleteCoctailHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String id = path.substring(path.lastIndexOf("/") + 1);
        String str = "";
        int rCode = 200;
        Integer response = 0;
        if (requestMethod.equalsIgnoreCase("delete")) {
            ObjectMapper mapper = new ObjectMapper();
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder requestBody = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            System.out.println(requestBody.toString());
            try {
                TypedQuery<String> queryDate = session.createQuery(
                        "SELECT d.des_id, d.rec, d.history \n" +
                                "FROM Description d\n" +
                                "JOIN Coctails c ON c.des_id = d.des_id\n"+
                                "WHERE c.name = :name"
                );
                queryDate.setParameter("name",id);
                List<String> result = new ArrayList<>();
                Iterator itr = queryDate.getResultList().iterator();
                while(itr.hasNext()) {
                    Object[] arrObj = (Object[])itr.next();
                    for(Object obj:arrObj) {
                        result.add(String.valueOf(obj));
                    }
                }
                String json = "{\"des_id\":" + result.get(0)
                        + ",\"rec\":\"" +result.get(1).replace("\n"," ") +
                        "\",\"history\":\"" + result.get(2).replace("\n"," ") +
                        "\"}";
                System.out.println(json);
                try {
                    Description order = mapper.readValue(json, Description.class);
                    session.delete(order);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                str = "Успешно!";
            } catch (ConstraintViolationException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                rCode = 400;
                str = "Пользователь с таким именем уже существует";
            }
            catch (Exception e){
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
        System.out.println(rCode);

        byte[] bytes = response.toString().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(rCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}
