package net.imglib2.examples;

import java.io.File;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.RealType;

import ij.ImageJ;

/**
 * Here we use special cursors to find the local maxima and display them with spheres in another image
 *
 * @author Stephan Preibisch &amp; Stephan Saalfeld
 *
 */
public class Example4
{
	public Example4()
	{
		// define the file to open
		File file = new File( "DrosophilaWing.tif" );

		// open with ImgOpener using an ArrayContainer
		Img<FloatType> image = new ImgOpener().openLOCIFloatType( file.getAbsolutePath(), new ArrayImgFactory<T>() );

		// find local maxima and paint them into another image as spheres
		Img<BitType> display = findAndDisplayLocalMaxima( image, new BitType() );

		// display output and input
		image.getDisplay().setMinMax();
		display.getDisplay().setMinMax();
		ImageJFunctions.copyToImagePlus( image ).show();
		ImageJFunctions.copyToImagePlus( display ).show();
	}

	public static <T extends Comparable<T> & Type<T>, U extends RealType<U>> Img<U> findAndDisplayLocalMaxima( final Img<T> image, final U outputType )
	{
		// Create a new image of the provided RealType U
		ImageFactory<U> imageFactory = new ImageFactory<U>( outputType, image.getContainerFactory() );
		Img<U> output = imageFactory.createImage( image.getDimensions() );

		// create a Cursor that runs over the image and checks in a 3^n neighborhood if it is a maxima
		LocalizableCursor<T> cursor1 = image.createLocalizableCursor();

		// create a LocalizableByDimCursor that is used to check the local neighborhood of each pixel
		LocalizableByDimCursor<T> cursor2 = image.createLocalizableByDimCursor();

		// and a local neighborhood cursor on top of the localizablebydim
		LocalNeighborhoodCursor<T> nbCursor = LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( cursor2 );

		// we need the number of dimensions a lot
		final int numDimensions = image.getNumDimensions();

		// we should have a temporary array to get the current position
		int[] tmp = new int[ image.getNumDimensions() ];

		// iterate over the image
A:		while ( cursor1.hasNext() )
		{
			cursor1.fwd();

			// get the current position
			cursor1.getPosition( tmp );

			// check if there is at least a distance of 1 to the border
			for ( int d = 0; d < numDimensions; ++d )
				if ( tmp[ d ] < 1 || tmp[ d ] > image.getDimension( d ) - 2 )
					continue A;

			// move the cursor to the current position
			cursor2.setPosition( cursor1 );

			// update the local neighborhood cursor
			nbCursor.update();

			// what is the value that we investigate
			final T centerValue = cursor2.getType().copy();

			boolean isMaximum = true;

			// check if all pixels are smaller
			while ( nbCursor.hasNext() && isMaximum )
			{
				nbCursor.fwd();

				// test if the center is smaller than the current pixel value
				if ( centerValue.compareTo( nbCursor.getType() ) <= 0 )
					isMaximum = false;
			}

			if ( isMaximum )
			{
				// draw a sphere of radius one in the new image
				HyperSphereIterator<U> sphere = new HyperSphereIterator<U>( output, cursor1, 1 );

				for ( U value : sphere )
					value.setOne();

				sphere.close();
			}
		}

		return output;
	}

	public static void main( String[] args )
	{
		// open an ImageJ window
		new ImageJ();

		// run the example
		new Example4();
	}
}
