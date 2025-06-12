# Skin Cancer Detection App using Deep Learning

This project was developed as part of the course **CS F425 Deep Learning** course instructed by [Dr. Bharat Riccharya](https://www.bits-pilani.ac.in/pilani/bharat-richhariya), Assistant Professor at BITS Pilani and [Dr. Pratik Narang](https://www.bits-pilani.ac.in/pilani/pratik-narang), Associate Proffesor at BITS Pilani, Rajasthan. The main objective of the project is to detect the correct type of skin cancer from models specifically used for edge devices such as [MobileNets](https://docs.pytorch.org/vision/main/models/mobilenetv3.html). It serves as an illustration of how MobileNet based models can be trained and deployed in Android.

---
## Well.. What's the purpose?

As we have chose to train a MobileNet based model, it naturally lends itself to deployment on modern smartphones. To leverage this, we went a step further and developed a user-friendly and light-weight Android application that runs the model seamlessly on smartphones, making skin cancer detection accessible anytime, anywhere. 

To run the app on your Android device:
1. Download the app-release.apk from [here](SkinCancerApp/app/release/) in your mobile.
2. Locate the downloaded file in the Downloads folder of your file system.
3. Tap to install. If prompted, grant the necessary permissions (e.g., install from unknown sources).
4. Once installed, launch the app from your app drawer—you're ready to go!

---
## What's Inside

- `DL_Course_Project` - Files which we submitted for the requirement for the course. Contains the detailed report about the strategy used while tarining and fine-tuning.
- `SkinCancerApp/` - Source code for the Android app using **PyTorch Android Lite** to run inference.
- `pytorch_models` - MobileNetV3 models in Pytorch format. For Serialized TorchScript models, please head up to 
[assets](SkinCancerApp/app/src/main/assets/).
- `Export_Mobile.ipynb` - Code for conversion of a pytorch model into serialized torchScript model and testing the same on the validation and train  datasets
- `Skin_Cancer_Large_DL` - Code for training the HAM10000 Dataset on [MobileNetV3-Large](https://docs.pytorch.org/vision/main/models/generated/torchvision.models.mobilenet_v3_large.html)
- `Skin_Cancer_Small_DL` - Code for training the HAM10000 Dataset on [MobileNetV3-Small](https://docs.pytorch.org/vision/main/models/generated/torchvision.models.mobilenet_v3_small.html)
- `App_Images` - Contains demo images for the android application.

---
## How The Model Was Trained?

- **Dataset**: [HAM10000](https://www.kaggle.com/datasets/surajghuwalewala/ham1000-segmentation-and-classification) — over 10k dermatoscopic images across 7 skin disease classes.
- **Architecture**: MobileNetV3-Large and Small models with modified classifier head and last feature layer.
- **Framework**: PyTorch
- **Preprocessing**:
  - Images converted into PIL, tensors and resized to 224×224.
  - Normalization and data augmentation by generating the fake data through multiple copies of under-represented classes and applying rotation, flipping, color jitter to handle imbalance. The number of augmented copies per class was determined experimentally based on the severity of imbalance.
- **Training Details**:
  - Loss: CrossEntropy with class weights to handle imbalance.
  - Optimizer: Adam
  - Regularization: Weight decay and dropout
  - Validation split: 60/40
  - Strategy: Initially trained the modified layers of the pre-trained model and then fine tuned all the layers of the model. 
- **Export**:
  - Converted to TorchScript using `torch.jit.trace()` for Android compatibility.

- **NOTE**:
In our University project, we have trained the model on the dataset on GrayScale Image dataset which was provided by our instructors. We could have implemented this model also in our app by implementing an additional conversion of the captured image by the camera to grayscale. To improve the accuracy of the models in the extended project, we trained the images on color images as provided in HAM10000 dataset and deployed this model in our application.

---
## App Overview

<table>
  <tr>
     <td align="center"><b>Welcome Page</b></td>
     <td align="center"><b>Take Picture</b></td>
     <td align="center"><b>Upload from Gallery</b></td>
     <td align="center"><b>Result</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/Saarthak-Vijayvargiya-github/Skin_Cancer_MobileNet_App/blob/main/App_Images/1_Welcome.jpg" width=500></td>
    <td><img src="https://github.com/Saarthak-Vijayvargiya-github/Skin_Cancer_MobileNet_App/blob/main/App_Images/2_TakePic.jpg" width=500></td>
    <td><img src="https://github.com/Saarthak-Vijayvargiya-github/Skin_Cancer_MobileNet_App/blob/main/App_Images/3_Gallery.jpg" width=500></td>
    <td><img src="https://github.com/Saarthak-Vijayvargiya-github/Skin_Cancer_MobileNet_App/blob/main/App_Images/4_Result.jpg" width=500></td>
  </tr>
</table>

---
## Libraries and Software Versions
1. Used Conda Environment for model training:
  - Python - 3.9.19, PyTorch - 2.5.1, Pillow - 10.4.0
2. Java Application:
  - Compile Options (build.gradle) :  sourceCompatibility = JavaVersion.VERSION_1_8, targetCompatibility = JavaVersion.VERSION_1_8
  - Android (build.gradle) : compileSdkVersion 33, minSdkVersion 23, targetSdkVersion 33
  - Used Android API 33

---
## Disclaimer

This application is intended **for educational and research purposes only**. The predictions made by the model are based on image classification techniques and are **not a substitute for professional medical diagnosis or advice**.

If you suspect any skin irregularities or medical conditions, **please consult a qualified healthcare professional**. The author and contributors of this project are not responsible for any decisions or actions taken based on the use of this application.

The model is trained only on a set of labeled skin cancer types and **cannot determine whether a skin is healthy or unhealthy**. It classifies images among the cancer types it has seen during training, and **may not generalize to unseen or benign cases**. It's important to note that we do not claim the code to be entirely error-free, and there may be potential errors during its execution. Use this software responsibly and at your own risk.

---
## Have fun! Thank-You!