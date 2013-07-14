package de.synyx.rxtx.utils;

import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

/**
 *
 * @author Johannes Graf - graf@synyx.de
 */
public class SerialPortFacade {

    private static final Logger LOG = LoggerFactory.getLogger(SerialPortFacade.class);
    private String portName;
    private SerialPortParams serialPortParams;
    private SerialPort serialPort;

    public SerialPortFacade(String portName, SerialPortParams serialPortParams) {
        this.portName = portName;
        this.serialPortParams = serialPortParams;
    }

    public SerialPortFacade(String portName) {
        this(portName, new SerialPortParams());
    }

    public void open() throws UnsupportedCommOperationException, PortInUseException, UnsupportedPortException, NoSuchPortException {

        CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);

        PortType currentPortType = PortType.fromValue(commPortIdentifier.getPortType());
        if (!PortType.SERIAL.equals(currentPortType)) {
            throw new UnsupportedPortException(currentPortType);
        }

        if (commPortIdentifier.isCurrentlyOwned()) {
            throw new PortInUseException();
        }

        this.serialPort = (SerialPort) commPortIdentifier.open(portName, 2000);
        applySerialPortParams();
    }

    private void applySerialPortParams() throws UnsupportedCommOperationException {
        this.serialPort.setSerialPortParams(
                this.serialPortParams.getBaudRate(),
                this.serialPortParams.getDataBits(),
                this.serialPortParams.getStopBits(),
                this.serialPortParams.getParity()
        );
    }

    public void close() {
        serialPort.close();
    }

    public void registerEventlistener(SerialPortEventListener serialPortEventListener) throws TooManyListenersException {
        this.serialPort.addEventListener(serialPortEventListener);
        this.serialPort.notifyOnDataAvailable(true);
    }

    public SerialPort getSerialPort() {
        return this.serialPort;
    }

    public InputStream getInputStream() throws IOException {
        return this.serialPort.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.serialPort.getOutputStream();
    }
}
