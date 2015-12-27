package sopra.grenoble.jiraLoaderfx;

import java.io.IOException;
import java.io.OutputStream;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * @author cmouilleron
 * Custom class to allow the redirection on a specific JAVAFX textArea without freeze the main thread.
 * I don't know why but the main thread (the JAVA FX thread) freezes when too many datas are redirected on the textArea.
 * This class can be see as a buffer.
 * All datas are temporary stored in a {@link StringBuilder}. A thread is launched. And every XX milliseconds, this thread will write the
 * {@link StringBuilder} content in the textArea.
 *
 */
public class TextAreaOutputStream extends OutputStream {

	/**
	 * {@link StringBuffer} where string are stored
	 */
	private StringBuilder strBuilder = new StringBuilder();
	
	/**
	 * JAVAFX {@link TextArea} where {@link #strBuilder} content will be write.
	 */
	private TextArea textArea;	
	
	/**
	 * Reference on the {@link RunnableFlush} thread.
	 */
	private RunnableFlush runnableFlush;
	
	/***
	 * Default constructor
	 * @param textAreaInput
	 * @param timeBeforeFlushInMs
	 * 
	 */
	public TextAreaOutputStream(TextArea textAreaInput, int timeBeforeFlushInMs) {
		super();
		this.textArea = textAreaInput;
		//create the thread
		runnableFlush = new RunnableFlush(timeBeforeFlushInMs);
	}


	/**
	 * Append in the internal StringBuilder
	 */
	@Override
	public void write(int b) throws IOException {
		synchronized (strBuilder) {
			strBuilder.append(String.valueOf((char) b));
		}
	}

	
	/**
	 * Start the thread
	 */
	public void startFlushThread() {
		Thread t = new Thread(runnableFlush);
		t.start();
	}
	
	/**
	 * Stop the thread
	 */
	public void stopFlushThread() {
		runnableFlush.activateStop();
	}
	
	
	/**
	 * Function to write the {@link #strBuilder} content in the {@link #textArea}
	 */
	private void flushInTextArea() {
		synchronized (strBuilder) {
			if (strBuilder.length() != 0) {
				String msg = strBuilder.toString();
				Platform.runLater(() -> textArea.appendText(msg));
				strBuilder = new StringBuilder();
			}
		}
	}

	/**
	 * @author cmouilleron
	 *
	 */
	class RunnableFlush implements Runnable {
		private int sleepTime;
		private boolean isRunning = true;
		
		public void activateStop() {
			this.isRunning = false;
		}
		
		public RunnableFlush(int sleepTimeBetweenFlush) {
			super();
			this.sleepTime = sleepTimeBetweenFlush;
		}

		@Override
		public void run() {
			while (isRunning) {
				// flush in textarea
				flushInTextArea();
				//wait before wake up
				try {
					Thread.sleep(this.sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
