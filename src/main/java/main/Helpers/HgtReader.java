package main.Helpers;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

/**
 * Class HgtReader reads data from SRTM HGT files. Currently this class is
 * restricted to a resolution of 3 arc seconds.
 *
 * SRTM data files are available at the <a
 * href="http://dds.cr.usgs.gov/srtm/version2_1/SRTM3">NASA SRTM site</a>
 *
 * @author Oliver Wieland <oliver.wieland@online.de>
 */
public class HgtReader {
    private static final int SECONDS_PER_MINUTE = 60;

    private static final String HGT_EXT = ".hgt";

    // alter these values for different SRTM resolutions
    private static final int HGT_RES = 3; // resolution in arc seconds
    private static final int HGT_ROW_LENGTH = 1201; // number of elevation values
    // per line
    private static final int HGT_VOID = -32768; // magic number which indicates
    // 'void data' in HGT file

    private static final HashMap<String, ShortBuffer> cache = new HashMap<>();

    public double getElevation(double lat, double lng) {
        File file = new File("/Users/Solln/Desktop/Route-Finder-Backend/src/main/resources/O30/" + getHgtFileName(lat, lng));
//        System.out.println(file.getAbsolutePath());
        return getElevationFromHgt(lat, lng, file);
    }


    private static double getElevationFromHgt(double lat, double lon, File f) {
        try {
            String file = f.getName();
            // given area in cache?
            if (!cache.containsKey(file)) {

                // fill initial cache value. If no file is found, then
                // we use it as a marker to indicate 'file has been searched
                // but is not there'
                cache.put(file, null);
                if (f.exists()) {
                    ShortBuffer data = readHgtFile(f.getAbsolutePath());
                    cache.put(file, data);
                }
            }

            return readElevation(lat, lon, file);
        } catch (Exception ioe) {
            return 0;
        }
    }

    @SuppressWarnings("resource")
    private static ShortBuffer readHgtFile(String file) throws Exception {

        ShortBuffer sb;
        try (FileChannel fc = new FileInputStream(file).getChannel()) {
            // Eclipse complains here about resource leak on 'fc' - even with
            // 'finally' clause???
            // choose the right endianness

            ByteBuffer bb = ByteBuffer.allocateDirect((int) fc.size());
            while (bb.remaining() > 0)
                fc.read(bb);

            bb.flip();
            // sb = bb.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
            sb = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
        }

        return sb;
    }


    private static double readElevation(double lat, double lon, String tag) {

        ShortBuffer sb = cache.get(tag);

        if (sb == null) {
            return 0;
        }

        // see
        // http://gis.stackexchange.com/questions/43743/how-to-extract-elevation-from-hgt-file
        double fLat = frac(lat) * SECONDS_PER_MINUTE;
        double fLon = frac(lon) * SECONDS_PER_MINUTE;

        // compute offset within HGT file
        int row = (int) Math.round(fLat * SECONDS_PER_MINUTE / HGT_RES);
        int col = (int) Math.round(fLon * SECONDS_PER_MINUTE / HGT_RES);

        row = HGT_ROW_LENGTH - row;
        int cell = (HGT_ROW_LENGTH * (row - 1)) + col;

        // System.out.println("Read SRTM elevation data from row/col/cell " +
        // row + "," + col + ", " + cell + ", " + sb.limit());

        // valid position in buffer?
        if (cell < sb.limit()) {
            short ele = sb.get(cell);
            // System.out.println("==> Read SRTM elevation data from row/col/cell "
            // + row + "," + col + ", " + cell + " = " + ele);
            // check for data voids
            if (ele == HGT_VOID) {
                return 0;
            } else {
                return ele;
            }
        } else {
            return 0;
        }
    }

    private static String getHgtFileName(double lati, double lngi) {
     int lat = (int) lati;
     int lon = (int) lngi;

     String latPref = "N";
     if (lat < 0) latPref = "S";

     String lonPref = "E";
     if (lon < 0) {
     lonPref = "W";
     lon *= -1;
     lon += 1;
     }

     return String.format("%s%02d%s%03d%s", latPref, lat, lonPref, lon,
     HGT_EXT);
     }

    private static double frac(double d) {
        long iPart;
        double fPart;

        // Get user input
        iPart = (long) d;
        fPart = d - iPart;
        return fPart;
    }
}