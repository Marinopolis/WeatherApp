import javax.swing.*;

public class AppLaunch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //display weather app gui
                new WeatherAppGui().setVisible(true);

                System.out.println(WeatherApp.getCurrentTime());
            }
        });
    }
}
