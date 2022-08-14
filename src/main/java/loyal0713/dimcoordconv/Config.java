package loyal0713.dimcoordconv;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class Config {
    private static boolean showInActionBar = false;
    private static boolean showFacing = true;
    private static String filePath = "config/dimcoordconv.config";
    private static final Logger LOGGER = LogManager.getLogger("dimcoordconv");

    public static void readConfigFile() {

        Properties prop = new Properties();

        try (FileInputStream stream = new FileInputStream(filePath)){
            prop.load(stream);
            showInActionBar = Boolean.parseBoolean(prop.getProperty("showInActionBar"));
            showFacing = Boolean.parseBoolean(prop.getProperty("showFacing"));
            LOGGER.error("dim-coord-conv: Loaded config!");
        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find dimcoordconv.config file! Regenerating file!");
            generateConfigFile();
        } catch (IOException e) {
            LOGGER.error("Error reading default config file!");
        }
    }

    public static boolean getShowInActionBar() {
        return showInActionBar;
    }

    public static boolean getShowFacing() {
        return showFacing;
    }

    public static void generateConfigFile() {
        String showInActionBarValue = "showInActionBar=false";
        String showFacingValue = "showFacing=true";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("config/dimcoordconv.config"));
            writer.write(showInActionBarValue);
            writer.newLine();
            writer.write(showFacingValue);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            LOGGER.error("Error writing default config file!");
        }

        readConfigFile();
        LOGGER.error(getShowFacing() + " " + getShowInActionBar());
    }
}
