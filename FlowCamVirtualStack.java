/** 
  * This class creates a VirtualStack that will optionally transform all the images
  * to 8-bit *before* added to the stack.  The key to this class is the 
  * getProcessor() method override.
*/

//import ij.*;
//import ij.process.*;
//import ij.io.*;

import ij.VirtualStack;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.io.Opener;

import java.io.*;
/** 
  * This convenience class will load and display a stack given a list of filenames.
  * The stack will be a VirtualStack (only the visible slice is loaded).
  *
  */
public class FlowCamVirtualStack extends VirtualStack{

  public boolean bToByte = true;
  
  
/** Construct an empty virtual stack.
  *
  */
  public FlowCamVirtualStack(){
   super();
  }
/** 
  * Constructs a virtual stack where all images are converted to 8bit. 
  * @param width the width of the input image in pixels
  * @param height the height of the image in pixels
  * @param dir the fully qualified path
  * @param toByte set to true to force all images to 8 bit, 
  *   native depth otherwise they are left as 
  */    
  public FlowCamVirtualStack(int width, int height, String dir, boolean toByte) {
    super(width, height, null, dir);  
    bToByte = toByte;
  }//constructor
  
/** Returns an ImageProcessor for the specified slice, 
  * if not 8-bit then it is converted first *IF* required to do so.
  * where 1<=n<=nslices. Returns null if the stack is empty.
  * @param n the nth slice numbered processor to return
  * @return and ImageProcessor
  */
    public ImageProcessor getProcessor(int n) {
      //IJ.log("getProcessor: "+n+"  "+getFileName(n));
      ImagePlus imp = new Opener().openImage(getDirectory(), getFileName(n));
      if (imp!=null) {
        ImageProcessor ip = imp.getProcessor();
        if (bToByte) {
          if(imp.getType() != 0){
            ip=ip.convertToByte(true);
          }
        }
        return ip;
      } else {
          return null;
      }
   }   

} //FlowCamVirtualStack