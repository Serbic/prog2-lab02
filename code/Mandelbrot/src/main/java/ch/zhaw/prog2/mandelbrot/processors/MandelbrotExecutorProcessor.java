package ch.zhaw.prog2.mandelbrot.processors;

import ch.zhaw.prog2.mandelbrot.ImageRow;
import ch.zhaw.prog2.mandelbrot.MandelbrotProcessorListener;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MandelbrotExecutorProcessor extends MandelbrotProcessor {

    private volatile boolean terminate; // signal the threads to abort processing and terminate
    private ExecutorService executorService;


    /**
     * Initialize the Mandelbrot processor.
     * This method also initializes the color palette, containing colors in spectral order.
     * @param processorListener class to notify about processing results
     * @param width with of the canvas in pixel
     * @param height height of the canvas in pixel
     */
    public MandelbrotExecutorProcessor(MandelbrotProcessorListener processorListener, int width, int height) {
        super(processorListener, width, height);
    }

    /**
     * This method starts as many new threads as the user has specified,
     * and assigns a different part of the image to each thread.
     * The threads are run at lower priority than the event-handling thread,
     * in order to keep the GUI responsive.
     *
     * @param numThreads number of thread to start to run the tasks
     */
    @Override
    public void startProcessing(int numThreads) {
        terminate = false;  // Set the signal before starting the threads!
        // use a task for each row
        super.tasksRemaining = height;  // Records how many of the threads are still running
        super.startTime = System.currentTimeMillis();
        // calculate number of rows each task needs to calculate

        // starts the executor service with the given number of threads and submit a tasks per row
        executorService = Executors.newFixedThreadPool(numThreads);

        for (int i = 1; i <= height; i++) {
            executorService.execute(new MandelbrotTask(i));
        }
    }

    /**
     * Stopp processing tasks and terminate all threads.
     * Also notifies the GUI that the processing has been stopped.
     */
    @Override
    public void stopProcessing() {
        terminate = true;  // signal the threads to abort
        // executor shuts down
        executorService.shutdown();
        //executorService.awaitTermination(1, TimeUnit.MINUTES);

        // calculate processing time
        long duration = System.currentTimeMillis() - startTime;
        // notify the listener that the processing is completed
        processorListener.processingStopped(duration);
    }


    /**
     * This class defines the thread that does the computation.
     * The run method computes the image one pixel at a time.
     * After computing the colors for each row of pixels, the colors are
     * copied into the image, and the part of the display that shows that
     * row is repainted.
     */
    private class MandelbrotTask implements Runnable {

        // these values define the area and depth of the Mandelbrot graphic
        // we keep them local to allow to extend the function to
        // select the area and depth dynamically.
        private final double xmin, xmax, ymin, ymax, dx, dy;
        private final int maxIterations;
        // this tasks calculates the following range of rows
        private final int startRow, endRow;

        /** initialize the Task to calculate a single row */
        MandelbrotTask(int row) {
            this(row, row);
        }

        /** initialize the Task to calculate a range of rows */
        MandelbrotTask(int startRow, int endRow) {
            this.startRow = startRow;
            this.endRow = endRow;
            xmin = -1.6744096740931858;
            xmax = -1.674409674093473;
            ymin = 4.716540768697223E-5;
            ymax = 4.716540790246652E-5;
            dx = (xmax - xmin) / (width - 1);
            dy = (ymax - ymin) / (height - 1);
            maxIterations = 10000;
        }

        public void run() {
            try {
                for (int row = startRow; row <= endRow; row++) {
                    // Compute one row of pixels.
                    ImageRow imageRow = calculateRow(row);
                    // Check for the signal to immediately abort the computation.
                    if (terminate || imageRow == null) return;
                    // notify the listener about the processed image row
                    processorListener.rowProcessed(imageRow);
                }
            } finally {
                // Make sure this is called when the thread finishes for any reason.
                taskFinished();
            }
        }

        private ImageRow calculateRow(int row) {
            final ImageRow imageRow = new ImageRow(row, width);
            double x;
            double y = ymax - dy * row;

            for (int col = 0; col < width; col++) {
                x = xmin + dx * col;
                int count = 0;
                double xx = x;
                double yy = y;
                while (count < maxIterations && (xx * xx + yy * yy) < 4) {
                    count++;
                    double newxx = xx * xx - yy * yy + x;
                    yy = 2 * xx * yy + y;
                    xx = newxx;
                }
                // select color based on count of iterations
                imageRow.pixels[col] = (count != maxIterations) ?
                    palette[count % palette.length] : Color.BLACK;
                // Check for the signal to immediately abort the computation.
                if (terminate) return null;
            }
            return imageRow;
        }
    } // end MandelbrotTask
}
