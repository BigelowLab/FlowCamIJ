import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import java.lang.Exception;

public class Test_FlowCam implements PlugIn {

	public void run(String arg) {
      FlowCam fcam = new FlowCam("/Users/Shared/data/fac/NASAcarbon/fcam/242-142907");
      fcam.loadStack("collageMaskImages");
      fcam.collageMaskImages.show();
      fcam.loadStack("collageImages");
      fcam.collageImages.show();
      //fcam.loadStack("rawImages");
      //fcam.rawImages.show();


	}

}
