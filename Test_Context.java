import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class Test_Context implements PlugIn {

	public void run(String arg) {

		try {
			Context cfg = new Context("/Users/Shared/data/fac/NASAcarbon/fcam/242-142907/242-142907.ctx");
			IJ.log("[General] RunStartTime="+ cfg.getString("General", "RunStartTime"));
			IJ.log("[General] TechnicianName="+ cfg.getString("General", "TechnicianName"));
			IJ.log("[Fluid] CalibrationConstant="+ cfg.getDouble("Fluid", "CalibrationConstant"));
		} catch (Exception e) {
         		IJ.log("Caught Exception: " + e.getMessage());
      	}
	}

}
