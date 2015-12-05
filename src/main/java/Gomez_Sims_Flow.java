/*
 * To the extent possible under law, the Fiji developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.OpenDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;


/**
 * ProcessPixels
 *
 * A template for processing each pixel of either
 * GRAY8, GRAY16, GRAY32 or COLOR_RGB images.
 *
 * @author The Fiji Team
 */
public class Gomez_Sims_Flow implements PlugInFilter {
	protected ImagePlus image;
        protected ImagePlus mask;
        

	// image property members
	private int width;
	private int height;
        
        private int resultwidth;

	// plugin parameters
	public double value;
	public String name;

	/**
	 * @see ij.plugin.filter.PlugInFilter#setup(java.lang.String, ij.ImagePlus)
	 */
	@Override
	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")) {
			showAbout();
			return DONE;
		}

                
                this.mask = IJ.getImage();
                
                
                
                this.image = IJ.openImage(new OpenDialog("Select Image:").getPath());
		image.show();

		return DOES_8G | DOES_16 ;
	}

	/**
	 * @see ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)
	 */
	@Override
	public void run(ImageProcessor ip) {
		// get width and height
		width = ip.getWidth();
		height = ip.getHeight();
                
                

		if (showDialog()) {
			process(image);
			image.updateAndDraw();
		}
	}

	private boolean showDialog() {
//		GenericDialog gd = new GenericDialog("Process pixels");
//
//		// default value is 0.00, 2 digits right of the decimal point
//		gd.addNumericField("value", 0.00, 2);
//		gd.addStringField("name", "John");
//
//		gd.showDialog();
//		if (gd.wasCanceled())
//			return false;
//
//		// get entered values
//		value = gd.getNextNumber();
//		name = gd.getNextString();

		return true;
	}

	/**
	 * Process an image.
	 *
	 * Please provide this method even if {@link ij.plugin.filter.PlugInFilter} does require it;
	 * the method {@link ij.plugin.filter.PlugInFilter#run(ij.process.ImageProcessor)} can only
	 * handle 2-dimensional data.
	 *
	 * If your plugin does not change the pixels in-place, make this method return the results and
	 * change the {@link #setup(java.lang.String, ij.ImagePlus)} method to return also the
	 * <i>DOES_NOTHING</i> flag.
	 *
	 * @param image the image (possible multi-dimensional)
	 */
	public void process(ImagePlus image) {
		// slice numbers start with 1 for historical reasons
            int size = 0;
            int c = 0;
            ImageProcessor ip = mask.getProcessor();
            short[] resultarray;
            
            if(mask.getType() == ImagePlus.GRAY8){
                  this.resultwidth = getTraceSize(mask.getProcessor().getFloatArray());
                  resultarray = new short[this.resultwidth*image.getStackSize()];
                  IJ.log("Result array: " + resultwidth);
                  ImageStack is = image.getStack();
                    for (int i = 1; i <= image.getStackSize(); i++){
                        for (int y=0; y < height; y++) {
                            for (int x=0; x < width; x++) {
                                if(mask.getRoi().contains(x,y) && mask.getProcessor().getPixel(x, y) > 0){
                                    resultarray[c] = (short)is.getVoxel(x,y,i);
                                    c++;
                                }
                            }            
                        }                
                    }
            
            
            ShortProcessor resultip = new ShortProcessor(this.resultwidth, image.getStackSize(), resultarray, image.getProcessor().getColorModel());
            ImagePlus result = new ImagePlus("Result", resultip);
            result.show();
            this.image.close();
            
            }  else {
			throw new RuntimeException("not supported");         
            }        
	}
        
        private int getTraceSize(float pixels[][]){
            int size = 0;
                for (int y=0; y < height; y++) {
			for (int x=0; x < width; x++) {
			if(mask.getRoi().contains(x,y) && pixels[x][y] > 0){
                                size++;
                            }
			}
		}
           return size;

        }

	public void showAbout() {
		IJ.showMessage("Gomez-Sims flow analysis",
			"*************************************"
		);
	}

	/**
	 * Main method for debugging.
	 *
	 * For debugging, it is convenient to have a method that starts ImageJ, loads an
	 * image and calls the plugin, e.g. after setting breakpoints.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		Class<?> clazz = Gomez_Sims_Flow.class;
		String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
		String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
		System.setProperty("plugins.dir", pluginsDir);

		// start ImageJ
		new ImageJ();

		// open the Clown sample


		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
