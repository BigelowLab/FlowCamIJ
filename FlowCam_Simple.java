/**
Use the FlowCam_Simple plugin to perform minimal post-processing of a classified FlowCam dataset.
<p>
The user either provides a directory from a macro or manually selects one using a dialog.
<p>
    Required files include
        <li> Context (.ctx)
        <li> Spreadsheet(.csv)
        <li> Collages (_00000N.tif)
        <li> Masks (_00000N_bin.tif)
        <li> Classfication (.cla)
<p>
Outputs a subdirectory (_directoryname) and a spreadsheet within (.csv)
*/

import ij.*;
import ij.process.*;
import ij.gui.*;
import ij.measure.ResultsTable;
import ij.io.DirectoryChooser;
import ij.plugin.*;
import ij.plugin.frame.*;
import java.io.File;
import java.awt.*;
import java.lang.Exception;

public class FlowCam_Simple implements PlugIn {

    public void run(String arg) {
        //called from a macro?
        String inputPath = Macro.getOptions();
        if (inputPath == null || inputPath.isEmpty()){
            DirectoryChooser dc = new DirectoryChooser("Please select a FlowCam directory");
            inputPath = dc.getDirectory();
            if (inputPath == null) return;
            if (inputPath.trim() == "") return;
        } else {
            inputPath = inputPath.trim();
        }
        // does the directory exists?
        File dir = new File(inputPath);
        if (!dir.exists() || !dir.isDirectory()){
            IJ.log("inputPath must be an existing directory");
            return;
        }
        IJ.log("FlowCam_Simple:" + inputPath);
        //instatiate a new FLowCam
        FlowCam fcam = new FlowCam(inputPath);
        if (!fcam.hasClassification()) IJ.log("FlowCam_Simple: missing .cla");
        //load but don't show
        fcam.loadStack("collageMaskImages");
        fcam.loadStack("collageImages");
        // make one resuable BlobHandler
        BlobHandler bh = new BlobHandler();
        BHCompoundBlob blob;
        // define the desired measurements
        int measureFlags = BHBlob.BASIC + BHBlob.LOCATION + BHBlob.SHAPE + BHBlob.VOLUME + BHBlob.ELLIPSE + BHBlob.GRAY;
        double mergeIfCloserThan = fcam.ctx.getDouble("CaptureParameters", "DistanceToNeighbor");
        int pad = 0;
        int N = 0;
        // these are for processing steps
        ImageProcessor subMaskIp;
        ImageProcessor subImageIp;
        boolean ok, hasBlob;
        // get the classification steps
        String[] userLabels = fcam.data.getDataColumn("UserLabel");
        
        // iterate through the subimages
        for (int isub = 0; isub < fcam.data.size(); isub ++){
            subMaskIp = fcam.createSubsetBinaryProcessor(isub, "collageMaskImages", pad);
            subImageIp = fcam.createSubsetProcessor(isub, "collageImages", pad);
            ok = bh.setup(subMaskIp, subImageIp, fcam.cal, measureFlags);
            ok = bh.process();
            //bh.blobs.filterByMinESD(minESD/10.0, maxESD*10.0);
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
            if (hasBlob){
                blob = bh.blobs.get(labels[0]);
                //update the results table
                fcam.rt.incrementCounter();
                if (hasBlob) {blob.showInfo(fcam.rt, N);} //show the results
                fcam.rt.setLabel(fcam.name + "-" + (N+1), N); //set the label
                fcam.rt.addValue("UserLabel", userLabels[isub]);
                N++; //increment the results counter        
            }
        } //isub loop
        //fcam.rt.show("Results");
        fcam.saveTable();
        IJ.log("FlowCam_Simple: Done!");
    } //run

}
