package net.imglib2.examples;

import java.io.File;

import net.imglib2.type.numeric.RealType;

import ij.ImageJ;


/**
 * Opens a file with LOCI Bioformats as an ImgLib {@link Image}.
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example1b
{
	// within this method we define <T> to be a RealType
	public < T extends RealType<T> > Example1b()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with LOCI using an ArrayContainer
		Image<T> image = LOCI.openLOCI( file.getAbsolutePath(), new ArrayContainerFactory() );

		// display it via ImgLib using ImageJ
		ImageJFunctions.displayAsVirtualStack( image ).show();

		// open with LOCI as Float using an ArrayContainer
		Image<FloatType> imageFloat = LOCI.openLOCIFloatType( file.getAbsolutePath(), new CellContainerFactory( 10 ) );

		// display it via ImgLib using ImageJ
		imageFloat.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( imageFloat ).show();
	}


	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example1b();
	}
}
