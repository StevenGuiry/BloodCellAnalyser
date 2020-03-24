package sample;

import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class TriColorController {

    @FXML
    private ImageView triColorImageView;
    @FXML
    private JFXSlider hueSlider;
    public Image triColorImage;

    @SuppressWarnings("DuplicatedCode")
    public void initialize() {
        colorAdjust();

        if (MainController.file != null) {
            String path = MainController.file.toURI().toString();
            triColorImage = new Image(path);

            PixelReader pixelReader = triColorImage.getPixelReader();

            int width = (int) triColorImage.getWidth();
            int height = (int) triColorImage.getHeight();

            WritableImage image = new WritableImage(width, height);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {

                    int pixel = pixelReader.getArgb(x, y);

                    int R = (pixel >> 16) & 0xff;
                    int G = (pixel >> 8) & 0xff;
                    int B = (pixel) & 0xff;

                    //convert to hsb to get hue and saturation values of each pixel
                    float[] hsb = java.awt.Color.RGBtoHSB(R, G, B, null);

                    float hue = hsb[0];
                    float saturation = hsb[1];
                    float brightness = hsb[2];

                    //System.out.println("Hue: " + hue + "Sat: " + saturation);
                    if (hue > 0.85 && saturation > 0.175) image.getPixelWriter().setColor(x, y, Color.RED);
                    if (hue < 0.85 && saturation > 0.60) image.getPixelWriter().setColor(x, y, Color.PURPLE);

                }
            }
            triColorImageView.setImage(image);
        }
    }

    public void colorAdjust() {
        ColorAdjust colorAdjust = new ColorAdjust();

        hueSlider.valueProperty().addListener(
                (observableValue, number, t1) -> colorAdjust.setHue(hueSlider.getValue() / 10));
        triColorImageView.setEffect(colorAdjust);
    }
}
