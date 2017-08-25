


import java.io.IOException;
import java.security.Security;
import java.util.Date;
import java.util.logging.Logger;

// START SNIPPET: serial-snippet

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: Java Examples
 * FILENAME      :  SerialExample.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  http://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2017 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialDataEvent;
import com.pi4j.io.serial.SerialDataEventListener;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.SerialPort;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;

/**
 * This example code demonstrates how to perform serial communications using the Raspberry Pi.
 *
 * @author Robert Savage
 */
public class SerialExample {
	private static final Logger log = Logger.getLogger(SerialExample.class.getName());
	
    /**
     * This example program supports the following optional command arguments/options:
     *   "--device (device-path)"                   [DEFAULT: /dev/ttyAMA0]
     *   "--baud (baud-rate)"                       [DEFAULT: 38400]
     *   "--data-bits (5|6|7|8)"                    [DEFAULT: 8]
     *   "--parity (none|odd|even)"                 [DEFAULT: none]
     *   "--stop-bits (1|2)"                        [DEFAULT: 1]
     *   "--flow-control (none|hardware|software)"  [DEFAULT: none]
     *
     * @param args
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String args[]) throws InterruptedException, IOException {

        // !! ATTENTION !!
        // By default, the serial port is configured as a console port
        // for interacting with the Linux OS shell.  If you want to use
        // the serial port in a software program, you must disable the
        // OS from using this port.
        //
        // Please see this blog article for instructions on how to disable
        // the OS console for this port:
        // https://www.cube-controls.com/2015/11/02/disable-serial-port-terminal-output-on-raspbian/

        // create Pi4J console wrapper/helper
        // (This is a utility class to abstract some of the boilerplate code)
       // final Console console = new Console();

        // print program title/header
        log.info("<-- The Pi4J Project -->"+"Serial Communication Example");
        System.out.println("<-- The Pi4J Project -->"+"Serial Communication Example");
        // allow for user to exit program using CTRL-C
        //console.promptForExit();

        // create an instance of the serial communications class
        final Serial serial = SerialFactory.createInstance();

        // create and register the serial data listener
        serial.addListener(new SerialDataEventListener() {

			public void dataReceived(SerialDataEvent event) {
				  try {
					  log.info("[HEX DATA]   " + event.getHexByteString());
					  System.out.println("[HEX DATA]   " + event.getHexByteString());
					  log.info("[ASCII DATA] " + event.getAsciiString()+ "\n\n");
					  System.out.println("[ASCII DATA] " + event.getAsciiString()+ "\n\n");
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
			}
        	
        });

        try {
            // create serial config object
            SerialConfig config = new SerialConfig();

            // set default serial settings (device, baud rate, flow control, etc)
            //
            // by default, use the DEFAULT com port on the Raspberry Pi (exposed on GPIO header)
            // NOTE: this utility method will determine the default serial port for the
            //       detected platform and board/model.  For all Raspberry Pi models
            //       except the 3B, it will return "/dev/ttyAMA0".  For Raspberry Pi
            //       model 3B may return "/dev/ttyS0" or "/dev/ttyAMA0" depending on
            //       environment configuration.
            config.device("/dev/ttyUSB0")
                  .baud(Baud._9600)
                  .dataBits(DataBits._8)
                  .parity(Parity.NONE)
                  .stopBits(StopBits._1)
                  .flowControl(FlowControl.NONE);

            // parse optional command argument options to override the default serial settings.
            if(args.length > 0){
                config = CommandArgumentParser.getSerialConfig(config, args);
            }

            // display connection details
            log.info(" Connecting to: " + config.toString()+
                    " We are sending ASCII data on the serial port every 1 second."+
                    " Data received on serial port will be displayed below.");
            System.out.println(" Connecting to: " + config.toString()+
                    " We are sending ASCII data on the serial port every 1 second."+
                    " Data received on serial port will be displayed below.");

            // open the default serial device/port with the configuration settings
            serial.open(config);

            // continuous loop to keep the program running until the user terminates the program
            //while(console.isRunning()) {
                try {
                    // write a formatted string to the serial transmit buffer
                    //serial.write("CURRENT TIME: " + new Date().toString());

                    // write a individual bytes to the serial transmit buffer
                    //serial.write((byte) 13);
                    //serial.write((byte) 10);

                    // write a simple string to the serial transmit buffer
                    serial.write("$PMTK251,57600*2C\r\n");
                    //serial.write("$PMTK220,200*2C\r\n");

                    // write a individual characters to the serial transmit buffer
                    serial.write('\r');
                    serial.write('\n');

                    // write a string terminating with CR+LF to the serial transmit buffer
                    //serial.writeln("Third Line");
                    System.out.println("-------------------- SUCCESS --------------");
                     log.info("-------------------- SUCCESS --------------");
                     //serial.close();//??
                     System.exit(0);
                }
                catch(IllegalStateException ex){
                    ex.printStackTrace();
                    System.out.println(ex.getMessage());
                    log.info(ex.getMessage());
                }

                // wait 1 second before continuing
                Thread.sleep(1000);
          //  }

           
        }
        catch(IOException ex) {
            log.info(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            System.out.println(" ==>> SERIAL SETUP FAILED : " + ex.getMessage());
            return;
        }
    }
}
