package ch.zhaw.prog2.mandelbrot.processors;

import ch.zhaw.prog2.mandelbrot.ImageRow;
import ch.zhaw.prog2.mandelbrot.MandelbrotProcessorListener;
import javafx.scene.paint.Color;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MandelbrotExecutorProcessor extends MandelbrotProcessor {

    private volatile boolean terminate; // signal the threads to abort processing and terminate
    // TODO: Declare ExecutorService


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

        // TODO: Start the the executor service with the given number of threads and submit a tasks per row




    }

    /**
     * Stopp processing tasks and terminate all threads.
     * Also notifies the GUI that the processing has been stopped.
     */
    @Override
    public void stopProcessing() {
        terminate = true;  // signal the threads to abort
        // TODO: shutdown and unset executor service



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
        // TODO: Use Task implementation from MandelbrotTaskProcessor and modify it to calculat only one row.

        @Override
        public void run() {

        }
    } // end MandelbrotTask
}
