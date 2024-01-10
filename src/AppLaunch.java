import javax.swing.*;

public class AppLaunch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                WeatherAppGui weatherAppGui = new WeatherAppGui();
                weatherAppGui.setTheme(weatherAppGui.createDefaultTheme());
                weatherAppGui.setVisible(true);

                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
