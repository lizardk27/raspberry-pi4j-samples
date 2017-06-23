package orientation;

import ansi.EscapeSeq;
import calculation.AstroComputer;
import calculation.SightReductionUtil;
import i2c.sensor.LSM303;
import i2c.sensor.listener.LSM303Listener;
import i2c.servo.pwm.PCA9685;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.fusesource.jansi.AnsiConsole;
import user.util.GeomUtil;

/**
 * Show how to use the panel orienter (SunFlower)
 * in addition with a LSM303 for the device heading.
 * Along the same lines, position can be provided, form a GPS for example.
 * Instead of using an LSM303, the heading could come from an NMEA Station...
 */
public class SimplePanelOrienter {

	public static void main(String... args) {

		int servoHeading = 14;
		int servoTilt = 15;

		boolean ansiConsole = "true".equals(System.getProperty("ansi.console", "false"));

		// Supported parameters --heading:14 --tilt:15
		if (args.length > 0) {
			for (String prm : args) {
				if (prm.startsWith("--heading:")) {
					try {
						servoHeading = Integer.parseInt(prm.substring("--heading:".length()));
					} catch (Exception e) {
						throw e;
					}
				} else if (prm.startsWith("--tilt:")) {
					try {
						servoTilt = Integer.parseInt(prm.substring("--tilt:".length()));
					} catch (Exception e) {
						throw e;
					}
				}
			}
		}

		SunFlower instance = new SunFlower(servoHeading, servoTilt);

		String strLat = System.getProperty("latitude");
		if (strLat != null) {
			try {
				instance.setLatitude(Double.parseDouble(strLat));
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				System.exit(1);
			}
		}
		String strLong = System.getProperty("longitude");
		if (strLong != null) {
			try {
				instance.setLongitude(Double.parseDouble(strLong));
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				System.exit(1);
			}
		}
		String strDec = System.getProperty("declination");
		if (strDec != null) {
			try {
				instance.setDeclination(Double.parseDouble(strDec));
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				System.exit(1);
			}
		}

		final LSM303 sensor;
		final LSM303Listener lsm303Listener;

		// Set to 0
		instance.servosZero();
		instance.setCalibrating(false);


		try {
			sensor = new LSM303();
			lsm303Listener = new LSM303Listener() {
				@Override
				public void dataDetected(float accX, float accY, float accZ, float magX, float magY, float magZ, float heading, float pitch, float roll) {
					instance.setDeviceMagHeading(heading);
				}

				@Override
				public void close() {
					super.close();
				}
			};
			sensor.setDataListener(lsm303Listener);

			// TODO Point the device to the lower pole: S if you are in the North hemisphere, N if you are in the South hemisphere.
			// TODO Tropical zone case
			String mess = "Point the Device to the true South, hit [Return] when ready.";

			if (ansiConsole) {
				AnsiConsole.out.println(EscapeSeq.ansiLocate(1, 15) + EscapeSeq.ANSI_REVERSE + mess +  EscapeSeq.ANSI_ERASE_TO_EOL);
			} else {
				System.out.println(mess);
			}

			instance.setCalibrating(true);
			SunFlower.userInput("");
			if (ansiConsole) { // Cleanup
				AnsiConsole.out.println(EscapeSeq.ansiLocate(1, 15) + EscapeSeq.ANSI_ERASE_TO_EOL);
			}
			instance.setCalibrating(false);
			// Done calibrating
			instance.setDeviceHeading(180D);

			instance.startWorking();

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				System.out.println("\nBye.");
				instance.stopWorking();
				instance.stopHeadingServo();
				instance.stopTiltServo();
				synchronized (sensor) {
					sensor.setKeepReading(false);
					lsm303Listener.close();
					try {
						Thread.sleep(1_500L);
					} catch (InterruptedException ie) {
						System.err.println(ie.getMessage());
					}
				}
				instance.setHeadingServoAngle(0f);
				instance.setTiltServoAngle(0f);
				try {
					Thread.sleep(1_000L);
				} catch (InterruptedException ie) {
					System.err.println(ie.getMessage());
				}
				if (ansiConsole) {
					AnsiConsole.systemUninstall();
				}
			}));

			mess = "Start listening to the LSM303";
			if (ansiConsole){
				AnsiConsole.out.println(EscapeSeq.ansiLocate(1, 15) + mess + EscapeSeq.ANSI_ERASE_TO_EOL);
			} else {
				System.out.println(mess);
			}
			sensor.startReading();

		} catch (Throwable ex) {
			System.err.println(">>> Panel Orienter... <<< BAM!");
			ex.printStackTrace();
//		System.exit(1);
		}
	}
}
