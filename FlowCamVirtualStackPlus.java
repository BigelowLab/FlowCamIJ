/** 
  * This class creates a ImagePlus with a special FlowCamVirtualStack that 
  * will optionally transform all the images to 8-bit *before* being added 
  * to the stack.  
*/

import ij.*;
import ij.io.*;
import ij.process.*;
import java.io.*;

import ij.IJ;
import ij.ImagePlus;
import ij.process.*;

import java.io.File;

/** 
  * This convenience class will load and display a stack given a list of filenames.
  * The stack will be a VirtualStack (only the visible slice is loaded).
  *
*/
public class FlowCamVirtualStackPlus extends ImagePlus {


/** Construct an empty virtual stack.
  *
  */
  public FlowCamVirtualStackPlus(){
   super();
  }
/** 
  * Constructs a stack from a diretcory plus a String array of file names. 
  * @param directory the fully qualified path
  * @param files A String array with filenames 
  * @param toByte If true all images in stack are converted to 8 bit, otherwse they
  *   are left in native format.
*/    
  public FlowCamVirtualStackPlus(String directory, String[] names, boolean toByte) {
    super();
    String dir = markDirectory(directory);
    int[] dims = getDimensions(dir + names[0]);
    //get the stack
    FlowCamVirtualStack stack = new FlowCamVirtualStack(dims[0], dims[1], dir, toByte);
    if (stack == null){return;}
    //add each item
    for (int i = 0; i< names.length; i++){
      IJ.showProgress(i, names.length);
      stack.addSlice(names[i]);
    }
    setStack(names[0],stack);       
  }
 
/** 
  * Constructs a stack from a diretcory plus a String array of file names. Files
  * will be converted to 8-bit if required.
  * @param directory the fully qualified path
  * @param files A String array with filenames 
  * 
*/    
  public FlowCamVirtualStackPlus(String directory, String[] names) {
    super();
    String dir = markDirectory(directory);
    int[] dims = getDimensions(dir + names[0]);
    //IJ.log("dims= " + dims[0] + "  "  + dims[1]);
    //get the stack
    FlowCamVirtualStack stack = new FlowCamVirtualStack(dims[0], dims[1], dir, true);
    if (stack == null){return;}
    //add each item
    for (int i = 0; i< names.length; i++){
      IJ.showProgress(i, names.length);
      stack.addSlice(names[i]);
    }
    setStack(names[0],stack);       
  }
   
/** 
  * Constructs a stack from a String of absolute file paths concentanated 
  * with the "\n" newline character 
  *
  * @param concat_files A single String with fully qualified filenames 
  *   delimited with the newline character. 
  * @param toByte If true all images in stack are converted to 8 bit, otherwse they
  *   are left in native format.
  */
  public FlowCamVirtualStackPlus(String concat_files, boolean toByte) {
    super();
    String[] files = concat_files.split("\n");

    File file = new File(files[0]);
    String dir = markDirectory(file.getParent());
    int[] dims = getDimensions(files[0]);
    String name = file.getName();
    
    FlowCamVirtualStack stack = new FlowCamVirtualStack(dims[0], dims[1], dir, toByte);
    if (stack == null){return;}
    //add each item
    for (int i = 0; i< files.length; i++){
        IJ.showProgress(i, files.length);
            //check each file
        file = new File(files[i]);
        stack.addSlice(file.getName());
    }
    setStack(name,stack);  
  }//end of constructor with single String
	
    
/** 
  * Constructs a virtual stack from a String array of absolute file paths. 
  *
  * @param files A String array with fully qualified filenames 
  * @param toByte If true all images in stack are converted to 8 bit, otherwse they
  *   are left in native format.
  */    
  public FlowCamVirtualStackPlus(String[] files, boolean toByte) {
    super();
    File file = new File(files[0]);
    String dir = markDirectory(file.getParent());
    int[] dims = getDimensions(files[0]);
    String name = file.getName();
    
    FlowCamVirtualStack stack = new FlowCamVirtualStack(dims[0], dims[1], dir, toByte);
    if (stack == null){return;}
    //add each item
    for (int i = 0; i< files.length; i++){
        IJ.showProgress(i, files.length);
            //check each file
        file = new File(files[i]);
        stack.addSlice(file.getName());
    }
    setStack(name,stack);    
  }//end of constructor with String array  
  
/**
  * Ensures that the provided directory path includes the system 
  * path separator at the end.
  *
  * @param path the path to test
  * @return a String path descriptor with or without separator at end
*/
  private String markDirectory(String path){
    return (new File(path)).getAbsolutePath() + File.separator;
  }  
  
/**
  * returns a two element array of [width, height]
  * @param file The fulley qualified filename to query
  * @return a two element integer array specifying width and height
  */
  private int[] getDimensions(String file){
    //this is taken from an example Wayne Rasband described on the mailing list 
    //Fri, 5 Oct 2007 
    int[] arr = new int[2];
    ImagePlus imp = IJ.openImage(file);
    if (imp == null) {
      IJ.log("error opening " + file);
      return null;
    }
    arr[0] = imp.getWidth();
    arr[1] = imp.getHeight();
    imp.close();
    return arr;
  }
  
/**
  * Given x0, y-, width, height and pad generate a 4 element array
  * of [left, top, width, height] such that the padding doesn't run off the edge
  * @param x0 the starting coordinate
  * @param y0 the starting coordinate
  * @param width the width of the subset image (from x0 to right)
  * @param h the height of the subset image (from y0 downward)
  * @param pad the number of pixels to pad by in x and y
  * @return a 4 element array of adjuested [x0, y0, width, height]
  */
  
  public int[] adjustToPadding(int x0, int y0, int w, int h, int pad){
     int[] x = {x0-pad, y0-pad, w + 2*pad, h + 2*pad};
     if( x[0] < 0){ x[0] = 0;}
     if( x[1] < 0){ x[1] = 0;}
     if( (x[0] + x[2]) >= width) { x[2] = width - x[0] - 1;}
     if ( (x[1] + x[3]) >= height) { x[3] = height - x[1] - 1;}
     return x;
  }   
/**
  * A method for crafting a subimage ImageProcessor
  * @param x0 the starting coordinate
  * @param y0 the starting coordinate
  * @param width the width of the subset image (from x0 to right)
  * @param h the height of the subset image (from y0 downward)
  * @param pad the number of pixels to pad by in x and y
  * @return a subset processor of the specified coordinates with the grayscale
  * values 
  */
  public ImageProcessor createSubsetProcessor(int x0, int y0, int w, int h, int pad){
    int[] r = adjustToPadding(x0, y0, w, h, pad);
    if ((r[2] <= 0) || (r[3] <= 0)){return null;}
    
    ImageProcessor newIp;
    if (ip instanceof ByteProcessor){
      newIp = new ByteProcessor(r[2], r[3]);
    } else if (ip instanceof ShortProcessor){
      newIp = new ShortProcessor(r[2], r[3]);  
    } else if (ip instanceof FloatProcessor){
      newIp = new FloatProcessor(r[2], r[3]);
    } else if (ip instanceof ColorProcessor){
      newIp = new ColorProcessor(r[2], r[3]);
    } else {
      return null;
    }
    
    for (int y = 0; y < r[3]; y++){
      for (int x = 0; x < r[2]; x++){
        newIp.setf(x, y, ip.getPixelValue(x + r[0],  y + r[1]));
      } //x
    }//y
    
    return newIp;
  }//createSubsetProcessor
  
/**
  * A method for crafting a subimage ImagePlus
  * @param x0 the starting coordinate
  * @param y0 the starting coordinate
  * @param w the width of the subset image (from x0 to right)
  * @param height the height of the subset image (from y0 downward)
  * @param pad the number of pixels to pad by in x and y
  * @return a subset image of the specified coordinates with the grayscale
  * values and calibration of the the original
  */
  public ImagePlus createSubsetImagePlus(int x0, int y0, int w, int h, int pad){
   
    ImageProcessor newIp = createSubsetProcessor(x0, y0, w, h, pad);
    if (newIp == null) { return null;}
    
    ImagePlus newImp = new ImagePlus("Subset-" + newIp.getWidth() + "x" + newIp.getHeight(), newIp);
    newImp.setCalibration(getCalibration());
    
    return newImp; 
    
  }
  
} //FlowCamVirtualStack