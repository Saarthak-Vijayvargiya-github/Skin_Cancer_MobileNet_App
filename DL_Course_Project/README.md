# Deep Learning Project

This university project was developed as part of the course **CS F425 Deep Learning** course instructed by [Dr. Bharat Riccharya](https://www.bits-pilani.ac.in/pilani/bharat-richhariya), Assistant Professor at BITS Pilani and [Dr. Pratik Narang](https://www.bits-pilani.ac.in/pilani/pratik-narang), Associate Proffesor at BITS Pilani, Rajasthan. The main objective of the project is to detect the correct type of skin cancer from models specifically used for edge devices such as [MobileNets](https://docs.pytorch.org/vision/main/models/mobilenetv3.html). It serves as an illustration of how MobileNet based models can be trained and deployed in Android.

---
## What's Inside

- `DL_Proj_Report.pdf` - Report explaining about the Project.
- `DL_Proj_Submission.ipynb` - Code for training their Dataset on customized [MobileNetV3-Large](https://docs.pytorch.org/vision/main/models/generated/torchvision.models.mobilenet_v3_large.html). Dataset contained 3000 images for training and 1000 images for validation. We won't be able to include the dataset in this repository for some reasons. 
- `model_large_755Adam.pt` - Custom MobileNetV3 Large model with 75.5% validation accuracy.
- `model_small_704Adam.pt` - Custom MobileNetV3 Small model with 70.4% validation accuracy.

---
## Libraries and Software Versions
1. Used Conda Environment for model training:
  - Python - 3.9.19, PyTorch - 2.5.1, Pillow - 10.4.0

---
## Contributors
1. Saarthak Vijayvargiya (BITS Pilani University)
2. Afzal Aftab (BITS Pilani University)
3. Sanju S (BITS Pilani University)

## [Disclaimer](https://github.com/Saarthak-Vijayvargiya-github/Skin_Cancer_MobileNet_App/tree/main?tab=readme-ov-file#disclaimer)