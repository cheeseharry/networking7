/************************************************
 *
 * Author: Kevin Aud
 * Assignment: Program 7
 * Class: CSI 4321, Data Communications
 *
 ************************************************/

package instayak.app;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * General data dispatcher for asynchronous I/O
 * @version 0.1
 */
public class TCPAIODispatcher {

	/**
	 * Default buffer size
	 */
	private static final int BUFSIZE = 1024;
	/**
	 * Protocol-specific handler
	 */
	private final AIOHandler hdlr;
	/**
	 * Server logger
	 */
	private final Logger logger;
	/**
	 * Buffer to read into
	 */
	private ByteBuffer readBuffer = ByteBuffer.allocateDirect(BUFSIZE);
	/**
	 * Buffers to write from
	 */
	private List<ByteBuffer> writeBufferList = new ArrayList<>();
	/**
	 * Local byte buffer for processing
	 */
	private final byte[] localBuffer = new byte[BUFSIZE];

	/**
	 * Instantiate dispatcher for new client
	 * 
	 * @param hdlr protocol-specific handler
	 * @param logger server logger
	 */
	public TCPAIODispatcher(final AIOHandler hdlr, final Logger logger) {
		this.hdlr = hdlr;
		this.logger = logger;
	}

	/**
	 * Handle client connection accept
	 * 
	 * @param clntChan accepted channel
	 */
	public void handleAccept(final AsynchronousSocketChannel clntChan) {
		// Get bytes (if any) to send and write them
		processWriteBuffer(clntChan, hdlr.handleAccept());
	}

	/**
	 * Prepare the write buffer containing given bytes to be sent on given channel
	 * 
	 * @param clntChan channel on which to send bytes
	 * @param buf bytes to send
	 */
	private void processWriteBuffer(final AsynchronousSocketChannel clntChan,
			final byte[] buf) {
		// If buffer contains data to write, prepare for sending
		if (buf != null && buf.length > 0) {
			logger.info("Preparing to write for " + clntChan);
			writeBufferList.add(ByteBuffer.wrap(buf));
			clntChan.write(writeBufferList.toArray(new ByteBuffer[] {}), 0,
					writeBufferList.size(), -1, null, this,
					makeWriteCompletionHandler(
							clntChan, logger));
		// If buffer does not contain data, prepare for reading
		} else {
			logger.info("Preparing to read for " + clntChan);
			clntChan.read(readBuffer, this, makeReadCompletionHandler(clntChan, logger));
		}
	}

	/**
	 * Handle client read
	 * 
	 * @param clntChan channel for reading
	 */
	public void handleRead(final AsynchronousSocketChannel clntChan) {
		// Read next set of bytes
		readBuffer.flip();
		System.out.println("remaining " + readBuffer.remaining());
		readBuffer.get(localBuffer, 0, readBuffer.limit());
		logger.info("Read " +  readBuffer.limit() + " bytes for " + clntChan);
		// Allow protocol to handle the read
		byte buf[] = hdlr.handleRead(Arrays.copyOf(localBuffer,
				readBuffer.limit()));
		readBuffer.clear();
		// Write result
		processWriteBuffer(clntChan, buf);
	}

	/**
	 * Handle client write
	 * 
	 * @param clntChan channel for writing
	 */
	public void handleWrite(final AsynchronousSocketChannel clntChan) {
		// Remove first buffer in list until list is empty or the first buffer
		// has bytes left to write
		while (!writeBufferList.isEmpty()
				&& !writeBufferList.get(0).hasRemaining()) {

			logger.info("Wrote buffer for " + clntChan);
			writeBufferList.remove(0);
		}
		// Nothing to write, so read
		if (writeBufferList.isEmpty()) {
			logger.info("Preparing to read for " + clntChan);
			clntChan.read(readBuffer, this, makeReadCompletionHandler(clntChan, logger));
		} else {
		// More to write
			logger.info("Preparing to write for " + clntChan);
			clntChan.write(writeBufferList.toArray(new ByteBuffer[] {}), 0,
					writeBufferList.size(), -1, null, this,
					makeWriteCompletionHandler(
							clntChan, logger));
		}
	}
	
	/**
	 * Create completion handler for read
	 * 
	 * @param clntChan channel for reading
	 * @param logger server logger
	 * 
	 * @return read completion handler
	 */
	public static CompletionHandler<Integer, TCPAIODispatcher> makeReadCompletionHandler(
			final AsynchronousSocketChannel clntChan, final Logger logger) {
		return new CompletionHandler<Integer, TCPAIODispatcher>() {
			/* 
			 * Called when read completes
			 * 
			 * @param clntChan channel for read
			 * @param aioDispatcher AIO dispatcher for handling read
			 */
			public void completed(final Integer bytesRead,
					final TCPAIODispatcher aioDispatcher) {
				try {
					logger.info("Handling read for " + clntChan);
					// If other end closed, we will
					if (bytesRead == -1) {
						clntChan.close();
						return;
					}
					// Call protocol-specific handler
					aioDispatcher.handleRead(clntChan);
				} catch (IOException ex) {
					failed(ex, aioDispatcher);
				}
			}

			/*
			 * Called if read fails
			 * 
			 * @param ex exception triggered by read failure
			 * @param aioDispatcher AIO dispatcher for handling read
			 */
			public void failed(final Throwable ex,
					final TCPAIODispatcher aioDispatcher) {
				logger.log(Level.WARNING, "read failed", ex);
				try {
					clntChan.close();
				} catch (IOException e) {
					logger.warning("Attempted to close " + clntChan + " and failed");
				}
			}
		};
	}

	/**
	 * Create completion handler for write
	 * 
	 * @param clntChan channel for writing
	 * @param logger server logger
	 * 
	 * @return write completion handler
	 */
	public static CompletionHandler<Long, TCPAIODispatcher> makeWriteCompletionHandler(
			final AsynchronousSocketChannel clntChan, final Logger logger) {
		return new CompletionHandler<Long, TCPAIODispatcher>() {
			/* 
			 * Called when write completes
			 * 
			 * @param clntChan channel for write
			 * @param aioDispatcher AIO dispatcher for handling write
			 */
			public void completed(final Long bytesWritten,
					final TCPAIODispatcher aioDispatcher) {
				logger.info("Handling write for " + clntChan);
				aioDispatcher.handleWrite(clntChan);
			}

			/*
			 * Called if read fails
			 * 
			 * @param ex exception triggered by read failure
			 * @param aioDispatcher AIO dispatcher for handling read
			 */
			public void failed(final Throwable ex,
					final TCPAIODispatcher aioDispatcher) {
				logger.log(Level.WARNING, "write failed", ex);
				try {
					clntChan.close();
				} catch (IOException e) {
					logger.warning("Attempted to close " + clntChan + " and failed");
				}
			}
		};
	}
}

