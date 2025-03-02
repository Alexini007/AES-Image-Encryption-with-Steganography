# AES Image Encryption and Steganography
A Java-based application for encrypting images using the Advanced Encryption Standard (AES) and embedding hidden messages inside images using steganography. 
This project was developed as my Computer Science senior project at the American University in Bulgaria.

# 🛠 Overview
This project provides secure image encryption and hidden message embedding using AES encryption and Only Blue LSB steganography techniques. 
Users can encrypt an image, transforming it into an unreadable format, and later decrypt it using the correct key, while also having the option to hide and extract secret text messages inside images using encoding on the blue color channel to ensure visual integrity.

#### AES Encryption
AES (Advanced Encryption Standard) is a symmetric encryption algorithm that transforms image pixel data by applying multiple rounds of substitution and permutation using a secret key. This ensures that only users with the correct key can decrypt and restore the original image.

#### Only Blue LSB Steganography
The Only Blue LSB (Least Significant Bit) Steganography technique embeds hidden text within the least significant bits of only the blue channel in an image's pixels. Since the human eye is less sensitive to changes in blue tones, this method conceals messages while keeping the image’s visual appearance nearly unchanged.

# ✅ Features 

-  Encrypt images using the AES Encryption algorithm, transforming them into an unreadable format
-  Decrypt images back to their original state using the correct encryption key.
-  Text Embedding (Steganography) – Hides encrypted messages inside images while preserving visual integrity.
-  Text Extraction – Retrieves hidden messages from images with steganographic encoding.
-  User-Friendly Interface – No technical expertise is required to encrypt images or hide messages.
-  File Selection Dialog – users can Upload an image in PNG, JPEG and BMP images from their local system.
-  Output File Management – Saves encrypted, decrypted, and steganographic images in the desired format.

# 📑 Functional Requirements

#### File Selection
- Users can select an image file from their local file system - supported image formats: PNG, JPEG anb BMP.

#### Encryption and Decryption
- Implements AES encryption to convert an image into an unreadable format and AES decryption to recover the original image.
- Ensures that decryption perfectly restores the original quality and appearance.

#### Text Embedding & Extraction
- Users can embed a secret message inside an image and retrieve exactly the same text when extracting the hidden message.
- The hidden message remains undetectable to the human eye.
 
#### User Interface
- Provides a simple and intuitive UI for easy navigation.
- Allows users to enter passwords, select files, encrypt, decrypt, embed, and extract messages efficiently.
  
#### Output File Management
- After encryption or decryption, the user is prompted to choose a save location for the encrypted image.


# 📌 Non-Functional Requirements

#### Performance
- Standard images (2MB - 6MB) should be encrypted within 3 seconds.
- Larger images processed within a reasonable time without freezing the application.

#### Reliability
- The encryption, decryption, and text embedding does not damage the image.

#### Compatibility
- Supports widely used image formats: PNG, JPG, JPEG.

#### Scalability
- Should handle high-resolution images without crashing.
- Can be extended to support additional image formats in future updates.

# ⚙️ Installation

## Requirements:
- Java Development Kit (JDK) 17 or later. For best compatibility, download the official [Oracle JDK](https://www.oracle.com/java/technologies/downloads/).
- JavaFX Runtime compatible with your JDK version. [Link to install JavaFX](https://gluonhq.com/products/javafx/).
- Install JUnit5 to run the unit tests. [Link to the JUnit5 website](https://junit.org/junit5/). If you are using IntelliJ IDEA, this can be done entirely through the IDE.
- (Optional) Scene Builder for modifying the UI. Link to the Scene Builder download page.

## Setup:
1. Clone this repository.
2. Ensure JavaFX is configured correctly in your IDE. You should add the SDK to your IDE of choice and also configure the VM options:
```
--module-path "path_to_javafx_lib" --add-modules javafx.controls,javafx.fxml
```
Where the "path_to_javafx_lib" is the path to the lib folder of your JavaFX Runtime installation.


## Interface 

#### Overall design and layout of the software, including user interaction elements.
![Screenshot_1](https://github.com/user-attachments/assets/ff34a559-a0f6-4da2-81c7-89bab1af4266)

#### The UI after the user has entered a password and selected an image, ready to either Encrypt or Decrypt.
![Screenshot_2](https://github.com/user-attachments/assets/b9f34410-59d1-4b0d-b8a5-546eab749956)

#### The interface after the user has successfully decoded the text from an image.
![Screenshot_5](https://github.com/user-attachments/assets/39d0920c-8b5a-4c31-b892-97925e0f0ad9)



## Acknowledgments 
Senior Project Fall 2024 Department of Computer Science, American University in Bulgaria, supervisor: Vladimir Georgiev
