import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    private Theme currentTheme;

    public WeatherAppGui() {
        super("Weather App");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);

        // Set default theme
        setTheme(createDefaultTheme());

        addGuiComponents();
    }

    private void addGuiComponents() {
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");
        windSpeedText.setBounds(310, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                // Get weather data
                JSONObject weatherData = WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    // Handle null weatherData (e.g., show an error message)
                    System.out.println("Error: Weather data is missing or empty.");
                    return;
                }

                String weatherCondition = (String) weatherData.get("weather_condition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                weatherConditionDesc.setText(weatherCondition);

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);

        String[] themeOptions = {"Default", "Green", "Light"};
        JComboBox<String> themeDropdown = new JComboBox<>(themeOptions);
        themeDropdown.setBounds(15, 60, 100, 25);
        themeDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTheme = (String) themeDropdown.getSelectedItem();
                applySelectedTheme(selectedTheme);
            }
        });
        add(themeDropdown);
    }

    private void applySelectedTheme(String selectedTheme) {
        // Logic to determine and apply the selected theme
        Theme theme;
        switch (selectedTheme) {
            case "Green":
                theme = createGreenTheme();
                break;
            case "Light":
                theme = createLightTheme();
                break;
            default:
                theme = createDefaultTheme();
                break;
        }

        setTheme(theme);
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            // read the image file
            BufferedImage image = ImageIO.read(new File(resourcePath));
            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not find resource");
        return null;
    }

    public void setTheme(Theme theme) {
        currentTheme = theme;

        // Apply theme to GUI components
        getContentPane().setBackground(theme.getBackgroundColor());
        // Apply theme to other components as needed
        // For example, set text color, font styles, etc.
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public Theme createDefaultTheme() {
        Theme theme = new Theme();
        theme.setBackgroundColor(Color.WHITE);
        theme.setTextColor(Color.BLACK);
        return theme;
    }

    private Theme createGreenTheme() {
        Theme theme = new Theme();
        theme.setBackgroundColor(new Color(173, 216, 173)); // Warmer and friendlier green background color
        theme.setTextColor(Color.WHITE);
        return theme;
    }

    private Theme createLightTheme() {
        Theme theme = new Theme();
        theme.setBackgroundColor(Color.LIGHT_GRAY);
        theme.setTextColor(Color.BLACK);
        return theme;
    }
}
