package org.pytorch.helloworld;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.pytorch.IValue;
//import org.pytorch.LiteModuleLoader;
//import org.pytorch.MemoryFormat;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

  Button camera, gallery;
  TextView description;
  TextView result;
  ImageView imageView;
  int imageSize = 224;
  Bitmap imageBuffer = null;
  Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    camera = findViewById(R.id.button);
    gallery = findViewById(R.id.button2);

    description = findViewById(R.id.classified);
    result = findViewById(R.id.result);
    imageView = findViewById(R.id.imageView);

    description.setText(getString(R.string.before_result));

    // Restore bitmap after rotation
    if (savedInstanceState != null) {
      byte[] byteArray = savedInstanceState.getByteArray("image_bitmap");
      if (byteArray != null) {
        imageBuffer = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imageView.setImageBitmap(imageBuffer);
        classifyImage(imageBuffer);
      }
    }

    camera.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view){
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
          Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          startActivityForResult(cameraIntent, 3);
        } else{
          requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
      }
    });

    gallery.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, 1);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
    if(resultCode == RESULT_OK){
      if(requestCode == 3){
        Bitmap image = (Bitmap) data.getExtras().get("data");
//        int dimension = Math.min(image.getWidth(), image.getHeight());
//        image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);

        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
        imageView.setImageBitmap(image);
        imageBuffer = image;
        classifyImage(image);
      }else{
        Uri dat = data.getData();
        Bitmap image = null;
        try {
          image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
        } catch (IOException e) {
          e.printStackTrace();
        }

        image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
        imageView.setImageBitmap(image);
        imageBuffer = image;
        classifyImage(image);
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    if (imageBuffer != null) {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      imageBuffer.compress(Bitmap.CompressFormat.PNG, 100, stream);
      byte[] byteArray = stream.toByteArray();
      outState.putByteArray("image_bitmap", byteArray);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.top_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.model_info) {
      // Handle the click (show info dialog, new activity, toast, etc.)
      final AlertDialog.Builder builder = new AlertDialog.Builder(this)
              .setCancelable(true)
              .setView(MenuItemFactory.newInfoDialog(this, MenuItemFactory.MODEL_INFO));
      builder.show();
    } else if (id == R.id.developer_info) {
      final AlertDialog.Builder builder = new AlertDialog.Builder(this)
              .setCancelable(true)
              .setView(MenuItemFactory.newInfoDialog(this, MenuItemFactory.DEVELOPER_INFO));
      builder.show();
    }
    return super.onOptionsItemSelected(item);
  }


  /**
   * Classifies the bitmap image and sets the text of the elements in the application
   */
  public void classifyImage(Bitmap bitmap){
    Module module = null;

    /**
     * loading serialized torchscript module from packaged into app android asset model.pt,
     * LiteModuleLoader.load() is used to load models saved using _save_for_lite_interpreter()
     * Module.load() is used to load models saved using torch.jit.save()
     */

    try{
      // module = LiteModuleLoader.load(assetFilePath(this, "model_mobile_small_812_6.pt"));

      module = Module.load(assetFilePath(this, "model_large.pt"));
    } catch (IOException e){
      Log.e("PytorchHelloWorld", "Error reading assets", e);
      System.err.println(e.getMessage());
      Toast.makeText(this, "Error loading model", Toast.LENGTH_LONG).show();
      finish();
    }

    // preparing input tensor
    final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);
//    final Tensor inputTensor = preprocessBitmap(bitmap);

    // running the model
    final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

    // getting tensor content as java array of floats
    final float[] scores = outputTensor.getDataAsFloatArray();

//    System.out.println(scores[0] + " " + scores[1] + " " + scores[2] + " " + scores[3] + " " + scores[4] + " " + scores[5] + " " + scores[6]);

    // searching for the index with maximum score
    float maxScore = -Float.MAX_VALUE;
    int maxScoreIdx = -1;
    for (int i = 0; i < scores.length; i++) {
      if (scores[i] > maxScore) {
        maxScore = scores[i];
        maxScoreIdx = i;
      }
    }

    String className = ImageNetClasses.IMAGENET_CLASSES[maxScoreIdx];

    // showing className on UI
    description.setText(getString(R.string.after_result));
    result.setText(className);
  }

  /**
   * Copies specified asset to the file in /files app directory and returns this file absolute path.
   * @return absolute file path
   */
  public static String assetFilePath(Context context, String assetName) throws IOException {
    File file = new File(context.getFilesDir(), assetName);
    if (file.exists() && file.length() > 0) {
      return file.getAbsolutePath();
    }

    try (InputStream is = context.getAssets().open(assetName)) {
      try (OutputStream os = new FileOutputStream(file)) {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = is.read(buffer)) != -1) {
          os.write(buffer, 0, read);
        }
        os.flush();
      }
      return file.getAbsolutePath();
    }
  }

  /** Function to process RGB image for resize and normalize the channels same as python
   * which is expected by the model. Need to use if using LiteModuleLoader.
   *
   * @return Tensor in [1, C, H, W] format
   *
   * @see "app/build.gradle"
   */
  private Tensor preprocessBitmap(Bitmap bitmap) {

    final int width = bitmap.getWidth();
    final int height = bitmap.getHeight();
    float[] input = new float[3 * width * height];
    int[] pixels = new int[width * height];
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

    float meanR = 0.485f, meanG = 0.456f, meanB = 0.406f;
    float stdR = 0.229f, stdG = 0.224f, stdB = 0.225f;

    for (int i = 0; i < pixels.length; i++) {
      int pixel = pixels[i];
      float r = ((pixel >> 16) & 0xFF) / 255.0f;
      float g = ((pixel >> 8) & 0xFF) / 255.0f;
      float b = (pixel & 0xFF) / 255.0f;

      input[i] = (r - meanR) / stdR;
      input[i + width * height] = (g - meanG) / stdG;
      input[i + 2 * width * height] = (b - meanB) / stdB;
    }

    return Tensor.fromBlob(input, new long[]{1, 3, height, width});
  }
}
