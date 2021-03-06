#### FlowCam

[ImageJ](http://rsb.info.nih.gov/ij/) plugin for working with [FlowCam](http://www.fluidimaging.com/) data sets.  

Windows users see this [note](http://serc.carleton.edu/earth_analysis/image_analysis/download_install_imageJ.html) 

```Note to Windows Users: It is recommended that you install ImageJ in the Documents directory, rather than in the Program Files
directory. For security reasons, Windows 7 and Windows Vista do not allow programs to alter themselves by writing files to the 
Program Files directory. If ImageJ is installed in the Program Files directory, then the update function in Step 2 below will 
not work properly. In addition, if you are a Windows Vista user, be sure to choose the correct version of ImageJ (either 32-bit 
or 64-bit) for your computer.```


### What is this repository for?

FlowCam runs produce a number of files: images (raw, binarized masks, background and collages), context/configuration, metadata  and optional classification files. This plugin provides facilities for working with each component of FlowCam output.  It does not do any processing of 

### How do I get set up?

Clone the repository to an ImageJ plugins directory.  


### Usage

The repository includes compiled code (.jar and .class) to ease the user experience.  You should be able to simply restart ImageJ (or use "Help > Refresh Menus").  The navigate to "Plugins > FlowCam > FlowCam Simple".


### What is in it?

The FlowCam class is really just a container for common elements of a FlowCam run directory.

+ *Context* class for reading entries in the context file.
+ *Spreadsheet* class for reading the metadata file (.csv)
+ *FlowCamVirtualStack* and *FlowCamVirtualStackPlus* classes for navigating the images
+ *ResultsTable* class for storing and saving results in an [ImageJ](http://rsb.info.nih.gov/ij/developer/api/ij/measure/ResultsTable.html) friendly format.
+ *Calibration* class for managing pixels-to-microns [calibration](http://rsb.info.nih.gov/ij/developer/api/ij/measure/Calibration.html)
+ *Classification* class for user classification data stored in the .cla file


### Who do I talk to?
* Ben Tupper btupper@beigelow.org
* Nicole Poulton npoulton@bigelow.org
* Michael Sieracki msieracki@bigelow.org
