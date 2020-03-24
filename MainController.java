package sample;

import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class MainController {
    public Image img;
    @FXML
    private ImageView imageView;
    @FXML
    private Button exitBtn;
    @FXML
    private JFXToggleButton toggleButton;
    @FXML
    private TextField redCellCounter, whiteCellCounter;
    public static File file;

    private int imageWidth = 400, imageHeight = 300;
    DisjointSet disjointSet = new DisjointSet(imageWidth * imageHeight);
    public int[] array = new int[imageWidth * imageHeight];
    Rectangle r[] = new Rectangle[100];
    int rectIndex = 0;
    Label numberLabel[] = new Label[r.length];
    Label clusterLabel[] = new Label[r.length];

    //TODO Count red blood cells within clusters
    //TODO Junit Testing
    //TODO JMH Benchmarking

    public void initialize() {
        toggleButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (toggleButton.isSelected() && imageView.getImage() != null) {

                toggleButton.setText("Tri-Color ON");
                imageView.setImage(triColor());

            } else if (!toggleButton.isSelected() && file != null) {

                toggleButton.setText("Tri-Color OFF");
                String path = file.toURI().toString();
                img = new Image(path, imageWidth, imageHeight, false, true);
                imageView.setImage(img);
            } else {
                toggleButton.setText("No Image Selected");
            }
        });
    }

    @SuppressWarnings("DuplicatedCode")
    public Image triColor() {
        PixelReader pixelReader = img.getPixelReader();
        WritableImage image = new WritableImage(imageWidth, imageHeight);

        int id = 0;
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {

                int pixel = pixelReader.getArgb(x, y);

                int R = (pixel >> 16) & 0xff;
                int G = (pixel >> 8) & 0xff;
                int B = (pixel) & 0xff;

                //convert to hsb to get hue and saturation values of each pixel
                float[] hsb = java.awt.Color.RGBtoHSB(R, G, B, null);

                float hue = hsb[0];
                float saturation = hsb[1];
                float brightness = hsb[2];

                if (hue > 0.85 && saturation > 0.175) {
                    image.getPixelWriter().setColor(x, y, Color.RED);
                    array[id++] = id;
                } else if (hue < 0.85 && saturation > 0.50) {
                    image.getPixelWriter().setColor(x, y, Color.PURPLE);
                    array[id++] = -1;
                } else {
                    image.getPixelWriter().setColor(x, y, Color.WHITE);
                    array[id++] = 0;
                }
            }
        }
        imageView.setImage(image);
        unifySets();
        return image;
    }

    public void unifySets() { //if pixels next to each other are the same color they are unified
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth - 1; x++) {
                if (array[y * imageWidth + x] != 0 && (array[y * imageWidth + x + 1] != 0)) {
                    disjointSet.unify(y * imageWidth + x, y * imageWidth + x + 1);
                }
            }
        }
        //if the pixel directly underneath are the same color they are unified
        for (int y = 0; y < imageHeight - 1; y++) {
            for (int x = 0; x < imageWidth; x++) {
                if (array[y * imageWidth + x] != 0 && array[y * imageWidth + x + imageWidth] != 0) {
                    disjointSet.unify(y * imageWidth + x, y * imageWidth + x + imageWidth);
                }
            }
        }

        //prints sets to the console
        for (int i = 0; i < disjointSet.size(); i++) {
            if (i % imageWidth == 0) System.out.println();
            if (disjointSet.find(i) == i) System.out.print(0 + " ");
            else System.out.print(disjointSet.find(i) + " ");
        }
    }

    public void rectangles(ActionEvent e) {
        int numberOfRedCells = 0;
        int numberOfWhiteCells = 0;
        int outlier = getAverageSetSize() / 2;

        if (img != null) {
            int arr[] = IntStream.of(getRootArray()).distinct().toArray(); //takes unique values only from array of roots
            for (int j = 0; j < arr.length - 1; j++) {
                for (int i = 0; i < disjointSet.size(); i++) {
                    if (disjointSet.find(i) == arr[j] && disjointSet.componentSize(i) > outlier) { // Only puts rectangles over sets with more than half of the average pixels omits the rest
                        int x = i % imageWidth, y = i / imageWidth;
                        if (r[rectIndex] == null) r[rectIndex] = new Rectangle(x, y, 1, 1);
                        else {
                            if (x > r[rectIndex].getX() + r[rectIndex].getWidth())
                                r[rectIndex].setWidth(x - r[rectIndex].getX());
                            if (x < r[rectIndex].getX()) {
                                r[rectIndex].setWidth(r[rectIndex].getX() +
                                        r[rectIndex].getWidth() - x);
                                r[rectIndex].setX(x);
                            }
                            if (y > r[rectIndex].getY() + r[rectIndex].getHeight())
                                r[rectIndex].setHeight(y - r[rectIndex].getY());
                        }
                    }
                }
                if (r[rectIndex] != null) {
                    int areaOfRectangle = (int) (r[rectIndex].getWidth() * r[rectIndex].getHeight());
                    r[rectIndex].setFill(Color.TRANSPARENT);
                    if (areaOfRectangle > getAverageSetSize() * 5) {
                        r[rectIndex].setStroke(Color.BLUE);
                        numberOfRedCells += 3;
                        estimatedCellsInCluster(rectIndex);
                    } else if (areaOfRectangle > getAverageSetSize() * 2) {
                        r[rectIndex].setStroke(Color.YELLOW);
                        numberOfRedCells += 2;
                        estimatedCellsInCluster(rectIndex);
                    } else {
                        r[rectIndex].setStroke(Color.GREEN);
                        numberOfRedCells++;
                    }

                    //checking if centre of rectangle is purple(white blood cells) , if so set rectangle color to purple
                    int width = (int) r[rectIndex].getWidth() / 2;
                    int height = (int) r[rectIndex].getHeight() / 2;
                    int centrePointOfRectangle = (int) ((int) (r[rectIndex].getY() + height) * imageWidth + r[rectIndex].getX() + width);
                    int secondPointOfRectangle = centrePointOfRectangle - (imageWidth * height / 2);
                    int thirdPointOfRectangle = centrePointOfRectangle + width / 2;
                    if (array[centrePointOfRectangle] == -1 || array[secondPointOfRectangle] == -1 || array[thirdPointOfRectangle] == -1) {
                        r[rectIndex].setStroke(Color.PURPLE);
                        numberOfWhiteCells++;
                        if (areaOfRectangle > getAverageSetSize() * 5)
                            numberOfRedCells -= 3;
                        else if (areaOfRectangle > getAverageSetSize() * 2)
                            numberOfRedCells -= 2;
                        else numberOfRedCells--;
                    }

                    r[rectIndex].setTranslateX(imageView.getLayoutX());
                    r[rectIndex].setTranslateY(imageView.getLayoutY());
                    ((Pane) imageView.getParent()).getChildren().add(r[rectIndex++]);
                }
            } //Counts the number of cells in the image
            redCellCounter.setText(String.valueOf(numberOfRedCells));
            whiteCellCounter.setText(String.valueOf(numberOfWhiteCells));
        } else System.out.println("No Image Detected - Please Select 'Open Image'");
    }

    public void numberRectangles(ActionEvent e) {
        int cellNumber = 1;
        int labelIndex = 0;
        if (img != null) {
            for (int i = 0; i < rectIndex; i++) {
                numberLabel[labelIndex] = new Label();
                numberLabel[labelIndex].setText(String.valueOf(cellNumber++));
                numberLabel[labelIndex].setTextFill(Color.BLACK);
                numberLabel[labelIndex].setTranslateX(r[labelIndex].getX());
                numberLabel[labelIndex].setTranslateY(r[labelIndex].getY());

                ((Pane) imageView.getParent()).getChildren().add(numberLabel[labelIndex++]);
            }
        } else System.out.println("No Image Detected - Please Select 'Open Image'");
    }

    public int estimateClusters(int n) {
        int temp2 = n / getAverageSetSize();
        return temp2;
    }

    public void estimatedCellsInCluster(int n) {
        Label tempLabel = new Label();
        int areaOfRectangle = (int) (r[n].getWidth() * r[n].getHeight());

        r[n].hoverProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                tempLabel.setText("Estimated Red Blood Cells: " + (estimateClusters(areaOfRectangle)));
                tempLabel.setTextFill(Color.WHITE);
                tempLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
                tempLabel.setTranslateX(r[n].getX());
                tempLabel.setTranslateY(r[n].getY());

                ((Pane) imageView.getParent()).getChildren().add(tempLabel);
            } else
                ((Pane) imageView.getParent()).getChildren().remove(tempLabel);
        });
    }

    public int[] getRootArray() {
        int arr[] = new int[imageWidth * imageHeight];
        int temp = 0;
        for (int i = 0; i < disjointSet.size(); i++) {
            if (disjointSet.componentSize(i) > 50) {
                arr[temp] = disjointSet.find(i);
                temp++;
            }
        }
        return arr;
    }

    public int getAverageSetSize() {
        int average = 0;
        int arr[] = IntStream.of(getRootArray()).distinct().toArray(); //takes unique values only from array of roots
        for (int j = 0; j < arr.length - 1; j++) {
            average += disjointSet.componentSize(arr[j]);
        }
        return average / arr.length - 1;
    }

    public void fileChooser(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Files", "*.jpg", "*.jfif", "*.gif"));
        file = fc.showOpenDialog(null);

        if (file != null) {
            String path = file.toURI().toString();
            img = new Image(path, imageWidth, imageHeight, false, true);
            imageView.setImage(img);
        }
    }

    public void changeScene(ActionEvent e, String fxml) throws IOException {
        Parent pane = FXMLLoader.load(getClass().getResource(fxml));
        Scene scene = new Scene(pane);

        //Stage newStage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.initOwner(pane.getScene().getWindow());
        newStage.setScene(scene);
        newStage.show();
    }

    public void triColorScene(ActionEvent e) throws IOException {
        changeScene(e, "triColor.fxml");
    }

    public void handleCloseBtnAction(ActionEvent e) {
        Stage stage = (Stage) exitBtn.getScene().getWindow();
        stage.close();
    }
}
