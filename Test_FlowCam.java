import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import java.lang.Exception;

public class Test_FlowCam implements PlugIn {

	public void run(String arg) {

		try {
			FlowCam fcam = new FlowCam("/Users/Shared/data/fac/NASAcarbon/fcam/242-142907");
			IJ.log("loaded FlowCAM");
         IJ.log("nrow = " + fcam.data.size() );
         IJ.log("n calFilenames = " + fcam.calFilenames.length);
         IJ.log("n rawFilenames = " + fcam.rawFilenames.length);
         IJ.log("n collageMaskFilenames = " + fcam.collageMaskFilenames.length);
         IJ.log("n collageFilenames = " + fcam.collageFilenames.length);
		} catch (Exception e) {
         IJ.log("Caught Exception: " + e.getMessage());
      }
	}

}
