# README #

ImageJ plugin for working with FlowCam data sets

### What is this repository for? ###

FlowCam runs produce a number of files: images (raw, binarized masks, background and collages), context/configuration and metadata files.  This plugin provides facilities for working with each component of FlowCAM output.

### How do I get set up? ###
Download the repository to an ImageJ plugins directory.  

### What is in it? ###

FlowCam is really just a container for common elements of a FlowCam run directory.

* *Context* class for reading entries in the context file.
* *Spreadsheet* class for reading the metadata file
* *FlowCamVirtualStack* and *FlowCamVirtualStackPlus* for navigating the images
* *ResultsTable* (ij.measure.ResultsTable) for storing and saving results in an ImageJ friendly format.
* *Calibration* (ij.measure.ResultsTable) for managing pixels-to-microns



### Who do I talk to? ####
* Ben Tupper btupper@beigelow.org
* Nicole Poulton npoulton@bigelow.org
* Michael Sieracki msieracki@bigelow.org