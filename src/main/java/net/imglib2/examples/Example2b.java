package net.imglib2.examples;

import java.io.File;

import net.imglib2.img.display.imagej.ImageJFunctions;

import ij.ImageJ;

/**
 * Here we want to copy an Image into another with a different Container one using a generic method,
 * but we cannot do it with simple Cursors
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example2b
{

	public Example2b()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with LOCI using an ArrayContainer
		Image<FloatType> image = LOCI.openLOCIFloatType( file.getAbsolutePath(), new ArrayContainerFactory() );

		// copy the image
		Image<FloatType> duplicate = copyImage( image, new CellContainerFactory( 20 ) );

		// display the copy
		duplicate.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack( duplicate ).show();
	}

	public <T extends Type<T>> Image<T> copyImage( final Image<T> input, final ContainerFactory containerFactory )
	{
		// create a new Image with the same dimensions
		ImageFactory<T> imageFactory = new ImageFactory<T>( input.createType(), containerFactory );
		Image<T> output = imageFactory.createImage( input.getDimensions(), "Copy of " + input.getName() );

		// create a cursor for both images
		Cursor<T> cursorInput = input.createCursor();
		Cursor<T> cursorOutput = output.createCursor();

		// iterate over the input cursor
		while ( cursorInput.hasNext() )
		{
			// move both forward
			cursorInput.fwd();
			cursorOutput.fwd();

			// set the value of this pixel of the output image, every Type supports T.set( T type )
			cursorOutput.getType().set( cursorInput.getType() );
		}

		// close the cursors
		cursorInput.close();
		cursorOutput.close();

		//. return the copy
		return output;
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example2b();
	}
}
