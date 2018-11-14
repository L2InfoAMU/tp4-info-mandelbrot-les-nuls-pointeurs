package viewer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import mandelbrot.Complex;
import mandelbrot.Mandelbrot;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controls the color of the pixels of the canvas.
 */
public class Controller implements Initializable {

    /**
     * Dimension of the grid used to supersample each pixel.
     * The number of subpixels for each pixel is the square of <code>SUPERSAMPLING</code>
     */
    private static final int SUPERSAMPLING = 3;

    @FXML
    private Canvas canvas; /* The canvas to draw on */

    private Camera camera = Camera.camera0; /* The view to display */

    private Mandelbrot mandelbrot = new Mandelbrot(); /* the algorithm */


    /* positions of colors in the histogram */
    private double[] breakpoints = {0., 0.75, 0.85, 0.95, 0.99, 1.0};
    /* colors of the histogram */
    private Color[] colors =
            {Color.gray(0.2),
                    Color.gray(0.7),
                    Color.rgb(55, 118, 145),
                    Color.rgb(63, 74, 132),
                    Color.rgb(145, 121, 82),
                    Color.rgb(250, 250, 200)
            };

    /* algorithm to generate the distribution of colors */
    private Histogram histogram = new Histogram(breakpoints, colors);

    /**
     * Method called when the graphical interface is loaded
     *
     * @param location  location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Dialog<Color> test = new Dialog<>();
        ColorPicker colorPicker1 = new ColorPicker(colors[0]);
        ColorPicker colorPicker2 = new ColorPicker(colors[1]);
        ColorPicker colorPicker3 = new ColorPicker(colors[2]);
        ColorPicker colorPicker4 = new ColorPicker(colors[3]);
        ColorPicker colorPicker5 = new ColorPicker(colors[4]);
        ColorPicker colorPicker6 = new ColorPicker(colors[5]);
        test.getDialogPane().setContent(new HBox(colorPicker1, colorPicker2, colorPicker3, colorPicker4, colorPicker5, colorPicker6));
        test.getDialogPane().getButtonTypes().add(ButtonType.OK);
        test.showAndWait();
        //histogram = new Histogram(breakpoints, colors);
        colors[0] = colorPicker1.getValue();
        colors[1] = colorPicker2.getValue();
        colors[2] = colorPicker3.getValue();
        colors[3] = colorPicker4.getValue();
        colors[4] = colorPicker5.getValue();
        colors[5] = colorPicker6.getValue();
        render();
    }

    /**
     * compute and display the image.
     */
    private void render() {
        List<Pixel> pixels = getPixels();
        renderPixels(pixels);
    }

    /**
     * display each pixel
     *
     * @param pixels the list of all the pixels to display
     */
    private void renderPixels(List<Pixel> pixels) {
        GraphicsContext context = canvas.getGraphicsContext2D();
        for (Pixel pix : pixels) {
            pix.render(context);
        }
    }

    /**
     * Attributes to each subpixel a color
     *
     * @param subPixels the list of all subpixels to display
     */
    private void setSubPixelsColors(List<SubPixel> subPixels) {
        int nonBlackPixelsCount = countNonBlackSubPixels(subPixels);
        if (nonBlackPixelsCount == 0) return;
        Color[] colors = histogram.generate(nonBlackPixelsCount);
        subPixels.sort(SubPixel::compare);
        int pixCount = 0;
        for (SubPixel pix : subPixels) {
            pix.setColor(colors[pixCount]);
            pixCount++;
            if (pixCount >= colors.length) // remaining subpixels stay black (converge).
                break;
        }
    }


    /**
     * Count how many subpixel diverge.
     *
     * @param subPixels the subpixels to display
     * @return the number of diverging subpixels
     */
    private int countNonBlackSubPixels(List<SubPixel> subPixels) {
        return (int)
                subPixels.stream()
                        .filter(pix -> pix.value != Double.POSITIVE_INFINITY)
                        .count();
    }

    /**
     * Generates the list of all the pixels in the canvas
     *
     * @return the list of pixels
     */
    private List<Pixel> getPixels() {
        int width = (int) canvas.getWidth();
        int height = (int) canvas.getHeight();
        List<SubPixel> subPixels =
                new ArrayList<>(width * height * SUPERSAMPLING * SUPERSAMPLING);
        List<Pixel> pixels =
                new ArrayList<>(width * height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Pixel pix = preparePixel(x, y);
                subPixels.addAll(pix.getSubPixels());
                pixels.add(pix);
            }
        }
        setSubPixelsColors(subPixels);
        return pixels;
    }

    /**
     * Create the pixel with given coordinates
     *
     * @param x horizontal coordinate of the pixel
     * @param y vertical coordinate of the pixel
     * @return the computed pixel with given coordinates
     */
    private Pixel preparePixel(int x, int y) {
        double width = SUPERSAMPLING * canvas.getWidth();
        double height = SUPERSAMPLING * canvas.getHeight();
        List<SubPixel> sampledSubPixels = new ArrayList<>();
        for (int i = 0; i < SUPERSAMPLING; i++) {
            for (int j = 0; j < SUPERSAMPLING; j++) {
                Complex z =
                        camera.toComplex(
                                ((double) (SUPERSAMPLING * x) + i) / width,
                                1 - ((double) (SUPERSAMPLING * y) + j) / height // invert y-axis
                        );
                double divergence = mandelbrot.divergence(z);
                sampledSubPixels.add(new SubPixel(divergence));
            }
        }
        return new Pixel(x, y, sampledSubPixels);
    }
}
