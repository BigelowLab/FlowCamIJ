import ij.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;

public class Test_Spreadsheet implements PlugIn {

    public void run(String arg) {
        
    try {
        Spreadsheet x = new Spreadsheet("/Users/Shared/data/fac/201-080131/201-080131.csv", ",");
         IJ.log("size = " + x.size());
         IJ.log("ncol = " + x.countColumns() );
         IJ.log("sigma_intensity [2] =" + x.getDataElementDouble("sigma_intensity", 2));
         x.showInTextWindow("Boo!");
        } catch (Exception e) {
                IJ.log("Caught Exception: " + e.getMessage());
        }
    }

}
