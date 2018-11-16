# Barcode Scanner

Barcode Scanner is a library that provides easy integration of [Zxing library](https://github.com/zxing/zxing) with your android applications.

Screenshots
-------------------
<img src="https://raw.github.com/softotalss/barcodescanner/master/screenshots/scanner.png" width="266">

Download
-------------------
You can download an **arr** from [maven releases][3] page.

Or use Gradle:

```groovy
repositories {
    maven { url 'https://github.com/softotalss/barcodescanner/raw/master/maven-repository' }
}
```

```groovy
dependencies {
    implementation 'com.google.zxing:core:x.x.x'
    implementation 'com.github.softotalss:barcodescanner:1.0.1'
}
```

How do I use Barcode Scanner?
-------------------
#### Simple use
```java
public class ScannerActivity extends AppCompatActivity implements BarcodeScannerView.ActivityCallback {
    
    private ViewGroup mContentFrame;
    private BarcodeScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ...
        initCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    public void onPause() {
        stopCamera();
        super.onPause();
    }    

    private void initCamera() {
        mScannerView = new BarcodeScannerView(this);
        mContentFrame.addView(mScannerView);
        // Here setFormats
    }

    private void startCamera() {
        if (mScannerView != null) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }

    private void stopCamera() {
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    public void onResult(Result result) {
        // Get barcode with result.getText()
    } 

    @Override
    public void onErrorExit() {
        // An error occurred with the camera, notify to user
    }
```

#### Advanced use
```java
// Use flash
void setFlash(boolean flag);
boolean isFlashOn();
public void toggleFlash();
```

```java
// Read only specific formats
List<BarcodeFormat> formats = new ArrayList<>();
formats.add(BarcodeFormat.QR_CODE);
void setFormats(formats);
```
[BarcodeFormat][2]
> Use setFormats only right after initCamera

Deployment
------
1. update version information
2. gradlew :barcodescanner:assembleRelease :barcodescanner:publishMavenPublicationToMavenRepository
3. git add, commit, push (on master branch)

Author
------
Alejandro Santana - @softotalss on GitHub

> This project is based on:
 - [zxing](https://github.com/zxing/zxing)
 - [barcodescanner](https://github.com/dm77/barcodescanner)

License
-------
Apache 2.0. See the [LICENSE][4] file for details.

[2]: https://github.com/zxing/zxing/blob/master/core/src/main/java/com/google/zxing/BarcodeFormat.java
[3]: https://github.com/softotalss/BarcodeScanner/tree/master/maven-repository/com/github/softotalss/barcodescanner
[4]: https://github.com/softotalss/BarcodeScanner/blob/master/LICENSE