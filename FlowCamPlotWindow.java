import ij.IJ;

import ij.gui.PlotWindow;
import ij.gui.Plot;
import ij.gui.Roi;

public class FlowCamPlotWindow extends PlotWindow {
   public double[] xdata;
   public double[] ydata;
   public int[] index;
   public Roi roi;
   public Plot plot;
   
   public FlowCamPlotWindow(String title, String xlabel, String ylabel){
      
   }
   
   public void addPoints(double[] x, double[] y, int shape){
      xdata = x;
      ydata = y;
      super.addPoints(x,y,shape);
   }
} //class