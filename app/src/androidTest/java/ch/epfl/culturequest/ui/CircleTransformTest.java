package ch.epfl.culturequest.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Bitmap;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CircleTransformTest {

    @Test
    public void transformReturnsNonNullBitmap() {
        CircleTransform transform = new CircleTransform();
        Bitmap source = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Bitmap result = transform.transform(source);
        assertNotNull(result);
    }
    @Test
    public void transformReturnsBitmapWithSameDimensions() {
        Bitmap source = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888);

        // Apply the transformation
        CircleTransform transform = new CircleTransform();
        Bitmap result = transform.transform(source);

        // Check that the result is a circle with the same size as the smaller dimension of the source
        int size = Math.min(source.getWidth(), source.getHeight());
        assertEquals(size, result.getWidth());
        assertEquals(size, result.getHeight());
    }

    @Test
    public void transformKeyIsCircle(){
        assertEquals("circle", new CircleTransform().key());
    }
}
