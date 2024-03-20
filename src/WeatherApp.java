//retrieve the data from API - this backend logic will fetch the data from api
//And return it to the user.
//The gui will display the data to the user

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    //fetch data from gicen location

    @SuppressWarnings("unchecked")
    public static JSONObject getWeatherData(String locationName){
        //get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        //extract lat and long for weather api
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build api request for weather forecast uisng now retrieved coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +            
                    "latitude=" + latitude + "&longitude=" + longitude +            
                    "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m&timezone=America%2FLos_Angeles";
        
        try{
            //call api and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status 
            //200 means ok
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                //read and store into string builder
                resultJson.append(scanner.nextLine());
            }

            //close scanner
            scanner.close();
            conn.disconnect();
            
            //parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //we want to get current hours data so we need the time right now
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            //get Windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weather JSON data that we will access in our frontend
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();
        }


        return null;
    }

    //retrieves coordinates for any given gegraphic location name
    public static JSONArray getLocationData(String locationName){
        //replace any whitespace in location name with + to adhere to the format of the API
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location name
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +            
                    locationName + "&count=10&language=en&format=json";
        try{
            //call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check reponse status
            //200 means ok 
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                //store api results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the resulting json data into our string builder
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner and disconnect url.
                scanner.close();
                conn.disconnect();

                //parse json string into json object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data near api generated from the location name.
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        //couldnt find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set req to get
            conn.setRequestMethod("GET");
            
            //Connect to api
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }
        //could not make connection
        return null;
    }
    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();
        
        //iterate and see which matches our time
        for(int i = 0; i<timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return the indec
                return i;
            }
        }
        return 0;
    }
    private static String getCurrentTime(){
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format date and time to be of format (2024-03-20T00:00)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    //covert weathre code to something more readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            weatherCondition = "Clear";
        }else if(weathercode >0L && weathercode<=3L){
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <=67L)
                    || (weathercode >= 80L && weathercode <=99L)){
            weatherCondition = "Rain";  
        }else if((weathercode >= 71L && weathercode <=77L)){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }

}
