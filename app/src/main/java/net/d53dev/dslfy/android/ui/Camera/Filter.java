package net.d53dev.dslfy.android.ui.camera;

import android.graphics.PointF;

import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFalseColorFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePixelationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBDilationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

/**
 * Created by davidsere on 18/11/15.
 */
public enum Filter {
    None        (null),
    Contrast    (new GPUImageContrastFilter(1.7f)),

    Sepia       (new GPUImageSepiaFilter()),
    Greyscale   (new GPUImageGrayscaleFilter()),
    Toon        (new GPUImageToonFilter()),
    FalseColor  (new GPUImageFalseColorFilter(0f, 0f, 0.5f, 0.9f, 0.2f, 0f)),
    Monochrome  (new GPUImageMonochromeFilter()),
    Poserize    (new GPUImagePosterizeFilter()),
    Saturation  (new GPUImageSaturationFilter(1.4f)),
    RGB         (new GPUImageRGBFilter(1.2f, 1.3f, 0.8f)),
    Shadows     (new GPUImageHighlightShadowFilter(0.3f, 1.4f)),
    Pixelate    (new GPUImagePixelationFilter()),
    Invert      (new GPUImageColorInvertFilter()),
    Vignette    (new GPUImageVignetteFilter(new PointF(.5f, .5f), new float[] {0.0f, 0.0f, 0.0f}, 0.4f, 0.75f)),
    Sketch      (new GPUImageSketchFilter());


    protected GPUImageFilter imageFilter;

    private Filter(GPUImageFilter filter){
        this.imageFilter = filter;
    }

    public static Filter filterAt(int index){
        return Filter.values()[index];
    }
}
