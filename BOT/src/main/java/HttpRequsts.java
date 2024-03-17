import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

public class HttpRequsts {

    private final String URL = "http://localhost:5000/";

    public String doGetRequset(String path) {
        StringBuffer response = new StringBuffer();
        try {
            String requestUrl = URL+path;

            java.net.URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Устанавливаем метод запроса (GET, POST и т.д.)
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Включаем возможность отправки данных в тело запроса
            connection.setDoOutput(false);


            // Получаем ответ от сервера
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            System.out.println(response);

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public void doPostRequest(String path, String body) {
        try {

            String requestUrl = URL+path;
            java.net.URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            System.out.println(body);

            // Устанавливаем метод запроса (GET, POST и т.д.)
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Включаем возможность отправки данных в тело запроса
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                // Дано: данные, которые нужно отправить в теле запроса
                // Записать данные в выходной поток
                os.write(body.getBytes());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String inputLine;

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
