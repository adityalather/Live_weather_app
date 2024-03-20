import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.json.simple.JSONObject;

import java.awt.Cursor;
import java.awt.Font;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGui(){
        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450,650);

        setLocationRelativeTo(null);

        setLayout(null);

        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();

        searchTextField.setBounds(15, 15, 351, 45);

        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        
        add(searchTextField);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //TEMPERATURE TEXTS
        JLabel temperatureText = new JLabel("10 c");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //align the text to center
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText); 

        //weather condition descriptopm
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //Humidity Image
        JLabel humidityImage = new JLabel(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //Windspeed Image
        JLabel windspeedImage = new JLabel(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        //Add Windspeed text;
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 10km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        //search button
        JButton searchButton = new JButton(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\search.png"));

        //change cursor to hand cursor when hovering over the button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                String userInput = searchTextField.getText();

                //validate i    nput - remove whitespace to ensure non empty text
                if(userInput.replaceAll("//s", "").length()<=0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //update gui

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                //depending on the condition, update weather image
                switch(weatherCondition){
                    case "Clear":
                    weatherConditionImage.setIcon(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\clear.png"));
                    break;

                    case "Cloudy":
                    weatherConditionImage.setIcon(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\cloudy.png"));
                    break;

                    case "Rain":
                    weatherConditionImage.setIcon(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\rain.png"));
                    break;

                    case "Snow":
                    weatherConditionImage.setIcon(loadImage("C:\\Users\\adity\\OneDrive\\Desktop\\WEATHER APP\\src\\assets\\snow.png"));
                    break;

                }

                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                //update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                //update humidity
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //update windspeed
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");


            }
        });
        add(searchButton);

    }

    //used to create image in gui

    private ImageIcon loadImage(String resourcePath){
        try{

            //read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //returns an image icon so thatour component can render it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }

}