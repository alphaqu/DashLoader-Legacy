package net.quantumfusion.dash.common;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class BufferedWritableFileByteChannel implements WritableByteChannel {
    private static final int BUFFER_CAPACITY = 1000000;

    private boolean isOpen = true;
    private final OutputStream outputStream;
    private final ByteBuffer byteBuffer;
    private final byte[] rawBuffer = new byte[BUFFER_CAPACITY];

    public BufferedWritableFileByteChannel(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.byteBuffer = ByteBuffer.wrap(rawBuffer);
    }

    @Override
    public int write(ByteBuffer inputBuffer) throws IOException {
        int inputBytes = inputBuffer.remaining();

        if (inputBytes > byteBuffer.remaining()) {
            dumpToFile();
            byteBuffer.clear();

            if (inputBytes > byteBuffer.remaining()) {
                throw new BufferOverflowException();
            }
        }

        byteBuffer.put(inputBuffer);

        return inputBytes;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void close() throws IOException {
        dumpToFile();
        isOpen = false;
    }

    private void dumpToFile() {
        try {
            outputStream.write(rawBuffer, 0, byteBuffer.position());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
