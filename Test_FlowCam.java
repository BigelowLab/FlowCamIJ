import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.ResultsTable;

import java.awt.*;
import ij.plugin.*;
import ij.plugin.frame.*;
import java.lang.Exception;

public class Test_FlowCam implements PlugIn {

	public void run(String arg) {
	   String inputPath;
	  
	   inputPath = Macro.getOptions();
	   if (inputPath == null){
	     inputPath = "/Users/Shared/data/fac/NASAcarbon/fcam/242-142907";
	   }
      FlowCam fcam = new FlowCam(inputPath);
      fcam.loadStack("collageMaskImages");
      fcam.loadStack("collageImages");
      BlobHandler bh = new BlobHandler();
      BHCompoundBlob blob;
      int measureFlags = BHBlob.BASIC + BHBlob.LOCATION + BHBlob.SHAPE + BHBlob.VOLUME + BHBlob.ELLIPSE + BHBlob.GRAY;
      double minESD = fcam.ctx.getDouble("CaptureParameters", "MinESD");
      double maxESD = fcam.ctx.getDouble("CaptureParameters", "MaxESD");
      double mergeIfCloserThan = fcam.ctx.getDouble("CaptureParameters", "DistanceToNeighbor");
      int pad = 0;
      int N = 0;
      ImageProcessor subMaskIp;
      ImageProcessor subImageIp;
      boolean ok, hasBlob;
      //ResultsTable rt = new ResultsTable();
      //rt.reset();
      for (int isub = 0; isub < fcam.data.size(); isub ++){
         subMaskIp = fcam.createSubsetBinaryProcessor(isub, "collageMaskImages", pad);
         subImageIp = fcam.createSubsetProcessor(isub, "collageImages", pad);
         ok = bh.setup(subMaskIp, subImageIp, fcam.cal, measureFlags);
         ok = bh.process();
         bh.blobs.filterByMinESD(minESD, maxESD);
         
         int[][] mergeTable = bh.blobs.computeMergeTable(mergeIfCloserThan);
         if (mergeTable != null){
           for (int k = 0; k < mergeTable.length; k++){
             bh.blobs.combine(mergeTable[k]);
           }
         }
         bh.blobs.removeAllButLargest();
         bh.blobs.setVolumeMethod(BHBlob.VOL_AUTO);
         bh.blobs.computeVolume();
         int[] labels = bh.blobs.getLabels();    
         hasBlob = labels.length != 0;
         blob = bh.blobs.get(labels[0]);
 
         //update the results table
         fcam.rt.incrementCounter();
         if (hasBlob) {blob.showInfo(fcam.rt, N);} //show the results
         fcam.rt.setLabel(fcam.name + "-" + (N+1), N); //set the label
         //rt.incrementCounter();
         //if (hasBlob) {blob.showInfo(rt, N);} //show the results
         //rt.setLabel(fcam.name + "-" + (N+1), N); //set the label
         N++; //increment the results counter        
         
      } //isub loop
      fcam.rt.show("Results");
      fcam.saveTable();
      //IJ.log("done");
   } //run

}
