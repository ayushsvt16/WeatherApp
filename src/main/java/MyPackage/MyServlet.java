package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // API Key
        String apiKey = "924fc1791af5f5743a2c3e28974ce2b0";
        // Get the city from the form input
        String city = request.getParameter("city");

        // Validate the city input
        if (city == null || city.trim().isEmpty()) {
            response.getWriter().println("Error: City name cannot be empty.");
            return;
        }

        // Create the URL for the OpenWeatherMap API request
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

        try {
            // API interaction
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Reading data from the network
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            Scanner scanner = new Scanner(reader);
            StringBuilder responseContent = new StringBuilder();

            while (scanner.hasNext()) {
                responseContent.append(scanner.nextLine());
            }
            scanner.close();

            // Parse JSON
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

            if (jsonObject.has("main")) {
                long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
                String date = new Date(dateTimestamp).toString();

                double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
                double temperatureCelsius = temperatureKelvin - 273.15;

                // Format temperature to two decimal places
                String formattedTemperature = String.format("%.2f", temperatureCelsius);

                int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
                double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
                String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

                request.setAttribute("error", null);
                request.setAttribute("date", date);
                request.setAttribute("city", city);
                request.setAttribute("temperature", formattedTemperature);
                request.setAttribute("weatherCondition", weatherCondition);
                request.setAttribute("humidity", humidity);
                request.setAttribute("windSpeed", windSpeed);
            } else {
                request.setAttribute("error", "No data found for the specified city.");
            }

            connection.disconnect();
        } catch (Exception e) {
            request.setAttribute("error", "Error: Unable to fetch weather data. Please try again.");
            e.printStackTrace();
        }

        // Forward the request to the JSP page
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
