package nmea.forwarders;

import java.util.Properties;
import nmea.parser.GeoPos;
import nmea.parser.RMC;
import nmea.parser.StringParsers;
import orientation.SunFlower;

/**
 * This is a {@link Forwarder}, get the position and heading from NMEA
 * and orient the solar panel accordingly.
 * <br>
 * It can be loaded dynamically. As such, it can be set only from the properties file
 * used at startup. It - for now - cannot be managed from the Web UI.
 * The REST api is not aware of it.
 * <br>
 * To load it, use the properties file at startup:
 * <pre>
 *   forward.XX.cls=nmea.forwarders.SolarPanelOrienter
 *   forward.XX.properties=sunflower.properties
 * </pre>
 * A jar (or classpath) containing this class and its dependencies must be available in the classpath.
 *
 * TODO: Smoothing? Tolerance?
 */
public class SolarPanelOrienter implements Forwarder {
	private double declination = 0d;

	private SunFlower sunFlower = null;
	/*
	 * @throws Exception
	 */
	public SolarPanelOrienter() throws Exception {
	}

	@Override
	public void write(byte[] message) {
		String str = new String(message);
//	System.out.println(">>>> Mess:" + str);
		if (StringParsers.validCheckSum(str)) {
//		String deviceId = StringParsers.getDeviceID(str);
			String sentenceId = StringParsers.getSentenceID(str);

			switch (sentenceId) {
				case "RMC":
					RMC rmc = StringParsers.parseRMC(str);
					GeoPos gp = rmc.getGp();
					if (gp != null) {
						sunFlower.setLatitude(gp.lat);
						sunFlower.setLongitude(gp.lng);
					}
					if (rmc.getRmcDate() != null) { // TODO Same for getRmcTime ?
						sunFlower.setCurrentDateTime(rmc.getRmcDate());
					}
					if (rmc.getDeclination() != -Double.MAX_VALUE) {
						this.declination = rmc.getDeclination();
					}
					break;
				case "GLL":
					Object[] objects = StringParsers.parseGLL(str);
					GeoPos pos = (GeoPos)objects[StringParsers.GP_in_GLL];
					if (pos != null) {
						sunFlower.setLatitude(pos.lat);
						sunFlower.setLongitude(pos.lng);
					}
					break;
				case "HDG":
					double[] hdg = StringParsers.parseHDG(str);
					double heading = hdg[StringParsers.HDG_in_HDG];
					if (hdg[StringParsers.VAR_in_HDG] != -Double.MAX_VALUE) {
						this.declination = hdg[StringParsers.VAR_in_HDG];
					}
					sunFlower.setDeviceHeading(heading + this.declination);
					break;
				case "HDM":
					int hdm = StringParsers.parseHDM(str);
					sunFlower.setDeviceHeading(hdm + this.declination);
					break;
				default:
					break;
			}

		}
	}

	@Override
	public void close() {
		System.out.println("- Stop writing to " + this.getClass().getName());
		sunFlower.stopWorking();
	}

	public static class SunFlowerBean {
		private String cls;
		private String type = "sun-flower";

		public SunFlowerBean(SolarPanelOrienter instance) {
			cls = instance.getClass().getName();
		}
	}

	@Override
	public Object getBean() {
		return new SunFlowerBean(this);
	}

	private static int parsePropInt(Properties props, String name, int defaultValue) {
		String val = props.getProperty(name);
		if (val != null) {
			try {
				int i = Integer.parseInt(val);
				if (i<0 || i>15) {
					return defaultValue;
				}
				return i;
			} catch (NumberFormatException nfe) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	private static double parsePropDouble(Properties props, String name, double defaultValue) {
		String val = props.getProperty(name);
		if (val != null) {
			try {
				double d = Double.parseDouble(val);
				return d;
			} catch (NumberFormatException nfe) {
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}

	@Override
	public void setProperties(Properties props) {
		/*
		Servo pins read here, system variables equivalents
		--------------------------------------------------
		heading.servo.id=14
		tilt.servo.id=15
		#
		declination=14 # default if missing
		deltaT=68.8033
		#
		smooth.moves=true
		#
		ansi.console=false
		orient.verbose=true
		astro.verbose=true
		tilt.verbose=true
		servo.super.verbose=false
		#
		tilt.servo.sign=1
		heading.servo.sign=1
		#
		tilt.limit=20
		tilt.offset=0
		#
		one.by.one=false
		#
		time.provided=false
		*/
		int headingPin = parsePropInt(props, "heading.servo.id", 14);
		int tiltPin = parsePropInt(props, "tilt.servo.id", 15);
		
		System.setProperty("deltaT", props.getProperty("deltaT", "68.8033"));
		System.setProperty("smooth.moves", props.getProperty("smooth.moves", "true"));
		System.setProperty("ansi.console", props.getProperty("ansi.console", "false"));
		System.setProperty("orient.verbose", props.getProperty("orient.verbose", "true"));
		System.setProperty("astro.verbose", props.getProperty("astro.verbose", "true"));
		System.setProperty("tilt.verbose", props.getProperty("tilt.verbose", "true"));
		System.setProperty("servo.super.verbose", props.getProperty("servo.super.verbose", "false"));
		System.setProperty("tilt.servo.sign", props.getProperty("servo.tilt.sign", "1"));
		System.setProperty("heading.servo.sign", props.getProperty("heading.servo.sign", "1"));
		System.setProperty("tilt.limit", props.getProperty("tilt.limit", "20"));
		System.setProperty("tilt.offset", props.getProperty("tilt.offset", "0"));
		System.setProperty("one.by.one", props.getProperty("one.by.one", "false"));
		System.setProperty("time.provided", props.getProperty("time.provided", "false"));

		sunFlower = new SunFlower(new int [] { headingPin }, new int[] { tiltPin });

		declination = parsePropDouble(props, "declination", 14.0);

		sunFlower.startWorking();
	}
}
