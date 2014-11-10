import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class Test_IniFile implements PlugIn {

	public void run(String arg) {

		try {
			IniFile cfg = new IniFile("/Users/Shared/data/macf/NASAcarbon/fcam/242-142907/242-142907.ctx");
			IJ.log("[General] RunStartTime="+ cfg.getString("General", "RunStartTime"));
		} catch (Exception e) {
         		IJ.log("Caught Exception: " + e.getMessage());
      	}
	}

}
