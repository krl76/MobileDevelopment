# Отчет по практическому занятию № 5

## Содержание

1. Цель работы
2. Задачи практики
3. Краткие теоретические сведения
4. Ход выполнения работы
   1. Задание 1. Список датчиков
   2. Задание 2. Акселерометр
   3. Задание 3. Механизм разрешений
   4. Задание 4. Камера
   5. Задание 5. Микрофон
   6. Контрольное задание в MireaProject
5. Вывод

---

## 1. Цель работы

Изучить способы работы с аппаратными возможностями мобильного устройства в ОС Android, а именно:
- получить список датчиков устройства;
- вывести показания акселерометра;
- реализовать механизм запроса разрешений;
- организовать работу с камерой;
- организовать запись и воспроизведение аудио;
- встроить аппаратные возможности в итоговый проект `MireaProject`.

---

## 2. Задачи практики

В рамках практической работы требовалось:
- создать проект `Lesson5` и вывести список датчиков устройства;
- создать модуль `Accelerometer` и отобразить показания акселерометра;
- изучить и реализовать механизм runtime-разрешений;
- создать модуль `Camera` для вызова системной камеры, сохранения снимка и его отображения;
- создать модуль `AudioRecord` для записи и воспроизведения голоса;
- выполнить контрольное задание: встроить в `MireaProject` экран с датчиком, экран с камерой и экран с микрофоном.  

---

## 3. Краткие теоретические сведения

Для работы с датчиками в Android используется класс `SensorManager`, который позволяет получать список сенсоров и регистрировать слушатели изменений. Для обработки событий датчиков применяется интерфейс `SensorEventListener`. В случае акселерометра значения по трем осям передаются в массиве `event.values`. Значения по оси X отражают поперечное ускорение, по оси Y — продольное, по оси Z — вертикальное. В состоянии покоя по оси Z наблюдается значение около `9.8 м/с²`. :contentReference[oaicite:0]{index=0} :contentReference[oaicite:1]{index=1}

Для работы с опасными возможностями устройства в Android необходимо использовать механизм разрешений. Алгоритм включает проверку текущего статуса разрешения, запрос разрешения у пользователя и обработку ответа. Такой механизм обязателен для камеры и микрофона. :contentReference[oaicite:2]{index=2} :contentReference[oaicite:3]{index=3}

Для съемки фотографии в работе используется системное приложение камеры через `MediaStore.ACTION_IMAGE_CAPTURE`. Для безопасной передачи пути к файлу применяется `FileProvider`, а место сохранения результата передается через `MediaStore.EXTRA_OUTPUT`. :contentReference[oaicite:4]{index=4} :contentReference[oaicite:5]{index=5}

Для записи звука используется класс `MediaRecorder`, а для воспроизведения — `MediaPlayer`. В методических указаниях требуется реализовать приложение-диктофон с функциями записи и последующего воспроизведения. :contentReference[oaicite:6]{index=6} :contentReference[oaicite:7]{index=7}

---

## 4. Ход выполнения работы

### 4.1. Задание 1. Список датчиков

По методическим указаниям требовалось создать проект `ru.mirea.<фамилия>.Lesson5`, добавить в разметку `ListView`, получить через `SensorManager` список всех доступных датчиков и вывести их на экран. :contentReference[oaicite:8]{index=8} :contentReference[oaicite:9]{index=9}

#### Листинг `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ListView
        android:id="@+id/sensorListView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="8dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Листинг `MainActivity.kt`

```kotlin
package ru.mirea.kornilovku.lesson5

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import ru.mirea.kornilovku.lesson5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)

        val data = ArrayList<HashMap<String, String>>()

        for (sensor in sensors) {
            val item = HashMap<String, String>()
            item["Name"] = sensor.name
            item["Value"] = "Макс. диапазон: ${sensor.maximumRange}"
            data.add(item)
        }

        val adapter = SimpleAdapter(
            this,
            data,
            android.R.layout.simple_list_item_2,
            arrayOf("Name", "Value"),
            intArrayOf(android.R.id.text1, android.R.id.text2)
        )

        binding.sensorListView.adapter = adapter
    }
}
```

В результате был реализован экран, отображающий список всех датчиков устройства. В списке выводились названия датчиков и дополнительная информация о них.

---

### 4.2. Задание 2. Акселерометр

В задании требовалось создать модуль `Accelerometer`, разместить на экране три текстовых поля и реализовать приложение, которое в реальном времени показывает значения акселерометра. Инициализация датчика должна выполняться в `onResume`, снятие регистрации — в `onPause`, а обработка изменений — в `onSensorChanged`.  

#### Листинг `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <TextView
        android:id="@+id/textViewAzimuth"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="32dp"
        android:layout_marginEnd="16dp"
        android:text="X: "
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/textViewPitch"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="16dp"
        android:text="Y: "
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewAzimuth" />

    <TextView
        android:id="@+id/textViewRoll"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="20dp"
        android:layout_marginEnd="16dp"
        android:text="Z: "
        android:textSize="22sp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/textViewPitch" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Листинг `MainActivity.kt`

```kotlin
package ru.mirea.kornilovku.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var textViewAzimuth: TextView
    private lateinit var textViewPitch: TextView
    private lateinit var textViewRoll: TextView

    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewAzimuth = findViewById(R.id.textViewAzimuth)
        textViewPitch = findViewById(R.id.textViewPitch)
        textViewRoll = findViewById(R.id.textViewRoll)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometerSensor == null) {
            textViewAzimuth.text = "Акселерометр отсутствует"
            textViewPitch.text = ""
            textViewRoll.text = ""
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val valueX = event.values[0]
            val valueY = event.values[1]
            val valueZ = event.values[2]

            textViewAzimuth.text = "X: %.2f".format(valueX)
            textViewPitch.text = "Y: %.2f".format(valueY)
            textViewRoll.text = "Z: %.2f".format(valueZ)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
```

В результате был создан экран, где отображались текущие значения акселерометра по осям X, Y и Z. При наклоне и движении устройства значения изменялись в реальном времени.

---

### 4.3. Задание 3. Механизм разрешений

В методических указаниях приведен алгоритм получения пользовательского разрешения: проверка текущего состояния, запрос разрешения и обработка ответа. Этот механизм должен использоваться перед обращением к камере и микрофону. 

#### Листинг шаблона запроса разрешения

```kotlin
private fun checkPermissions() {
    val permissionStatus = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.CAMERA
    )

    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
        isWork = true
    } else {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_PERMISSION
        )
    }
}

override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    if (requestCode == REQUEST_CODE_PERMISSION) {
        isWork = grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}
```

Таким образом был реализован универсальный механизм получения runtime-разрешений, использованный в дальнейших заданиях.

---

### 4.4. Задание 4. Камера

В задании требовалось создать модуль `Camera`, который вызывает системную камеру, сохраняет изображение в папку приложения и отображает полученный снимок на экране. Для этого нужно использовать `registerForActivityResult`, `MediaStore.ACTION_IMAGE_CAPTURE`, `MediaStore.EXTRA_OUTPUT` и `FileProvider`.  

#### Листинг `AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.CAMERA" />

    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

    <application
        android:allowBackup="true"
        android:supportsRtl="true"
        android:theme="@style/Theme.Lesson5">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/paths" />
        </provider>

    </application>

</manifest>
```

#### Листинг `res/xml/paths.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path
        name="images"
        path="Pictures" />
</paths>
```

#### Листинг `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <ImageView
        android:id="@+id/imageView"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_margin="16dp"
        android:background="#DDDDDD"
        android:contentDescription="Фото"
        android:scaleType="centerCrop"
        android:src="@android:drawable/ic_menu_camera"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Листинг `MainActivity.kt`

```kotlin
package ru.mirea.kornilovku.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ru.mirea.kornilovku.camera.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let { binding.imageView.setImageURI(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.setOnClickListener {
            checkPermissionAndOpenCamera()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val photoFile = createImageFile()
            val authorities = "${applicationContext.packageName}.fileprovider"
            imageUri = FileProvider.getUriForFile(this, authorities, photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraLauncher.launch(intent)
        } catch (e: IOException) {
            Toast.makeText(this, "Ошибка создания файла", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
}
```

В результате модуль позволял сделать фотографию, сохранить ее во внешнюю папку приложения и отобразить снимок на экране.

---

### 4.5. Задание 5. Микрофон

В этой части работы требовалось создать модуль `AudioRecord`, который записывает звук с микрофона и воспроизводит его. В манифест необходимо добавить разрешение `RECORD_AUDIO`, а запись должна выполняться через `MediaRecorder`, воспроизведение — через `MediaPlayer`.  

#### Листинг `AndroidManifest.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />

    <application
        android:allowBackup="true"
        android:supportsRtl="true"
        android:theme="@style/Theme.Lesson5">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```

#### Листинг `activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <Button
        android:id="@+id/btnRecord"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginTop="120dp"
        android:layout_marginEnd="24dp"
        android:text="Начать запись"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <Button
        android:id="@+id/btnPlay"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="24dp"
        android:layout_marginTop="24dp"
        android:layout_marginEnd="24dp"
        android:text="Воспроизвести"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/btnRecord" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

#### Листинг `MainActivity.kt`

```kotlin
package ru.mirea.kornilovku.audiorecord

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_PERMISSION = 200
    }

    private lateinit var btnRecord: Button
    private lateinit var btnPlay: Button

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isRecording = false
    private var isPlaying = false
    private var isWork = false

    private lateinit var fileName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.btnRecord)
        btnPlay = findViewById(R.id.btnPlay)

        fileName = "${externalCacheDir?.absolutePath}/voice_note.m4a"

        checkPermissions()
        updateButtons()

        btnRecord.setOnClickListener {
            if (!isWork) {
                checkPermissions()
                Toast.makeText(this, "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRecording) stopRecording() else startRecording()
            updateButtons()
        }

        btnPlay.setOnClickListener {
            if (isPlaying) stopPlaying() else startPlaying()
            updateButtons()
        }
    }

    private fun checkPermissions() {
        val audioPermissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )

        if (audioPermissionStatus == PackageManager.PERMISSION_GRANTED) {
            isWork = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(this)
        } else {
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(fileName)
            prepare()
            start()
        }

        isRecording = true
        Toast.makeText(this, "Запись началась", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        isRecording = false
        Toast.makeText(this, "Запись остановлена", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(fileName)
            prepare()
            start()
            setOnCompletionListener {
                stopPlaying()
                updateButtons()
            }
        }

        isPlaying = true
        Toast.makeText(this, "Воспроизведение началось", Toast.LENGTH_SHORT).show()
    }

    private fun stopPlaying() {
        player?.release()
        player = null
        isPlaying = false
        Toast.makeText(this, "Воспроизведение остановлено", Toast.LENGTH_SHORT).show()
    }

    private fun updateButtons() {
        btnRecord.text = if (isRecording) "Остановить запись" else "Начать запись"
        btnPlay.text = if (isPlaying) "Остановить воспроизведение" else "Воспроизвести"

        btnPlay.isEnabled = !isRecording
        btnRecord.isEnabled = !isPlaying
    }

    override fun onStop() {
        super.onStop()

        if (isRecording) stopRecording()
        if (isPlaying) stopPlaying()

        updateButtons()
    }
}
```

В результате было создано приложение-диктофон, позволяющее записывать голос с микрофона и воспроизводить созданную запись.

---

### 4.6. Контрольное задание в `MireaProject`

В контрольном задании требуется добавить в `MireaProject` экран аппаратной части с тремя видами функциональности: использование датчика для логической задачи, использование камеры для творческой задачи и использование микрофона для практической задачи. Также необходимо добавить механизм запроса разрешений. 

#### Реализованные экраны

В проект `MireaProject` были добавлены следующие фрагменты:

1. **`SensorFragment`**
   Экран использует акселерометр для анализа положения устройства. На экране выводятся текущие значения `X`, `Y`, `Z`, а также логическое заключение:

   * телефон наклонен влево;
   * телефон наклонен вправо;
   * телефон лежит почти ровно экраном вверх.

2. **`CameraFragment`**
   Экран реализован в формате «Фото-заметка». Пользователь может сделать снимок, после чего фотография показывается на экране, а под ней можно ввести подпись.

3. **`AudioFragment`**
   Экран реализован в формате «Голосовая заметка». Пользователь может записать голосовое сообщение, остановить запись и затем прослушать ее.

#### Пример `SensorFragment.kt`

```kotlin
package ru.mirea.kornilovku.mireaproject.ui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.mirea.kornilovku.mireaproject.R

class SensorFragment : Fragment(R.layout.fragment_sensor), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private lateinit var textX: TextView
    private lateinit var textY: TextView
    private lateinit var textZ: TextView
    private lateinit var textState: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textX = view.findViewById(R.id.textViewX)
        textY = view.findViewById(R.id.textViewY)
        textZ = view.findViewById(R.id.textViewZ)
        textState = view.findViewById(R.id.textViewState)

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer == null) {
            textState.text = "Акселерометр отсутствует на устройстве"
        }
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        textX.text = "X: %.2f".format(x)
        textY.text = "Y: %.2f".format(y)
        textZ.text = "Z: %.2f".format(z)

        textState.text = when {
            x > 2 -> "Телефон наклонён влево"
            x < -2 -> "Телефон наклонён вправо"
            z > 9 -> "Телефон лежит почти ровно экраном вверх"
            else -> "Положение устройства изменяется"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
```

#### Пример `CameraFragment.kt`

```kotlin
package ru.mirea.kornilovku.mireaproject.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import ru.mirea.kornilovku.mireaproject.R
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraFragment : Fragment(R.layout.fragment_camera) {

    private lateinit var imageViewPhoto: ImageView
    private lateinit var editTextNote: EditText
    private lateinit var buttonTakePhoto: Button

    private var imageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Разрешение на камеру не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri?.let {
                    imageViewPhoto.setImageURI(it)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageViewPhoto = view.findViewById(R.id.imageViewPhoto)
        editTextNote = view.findViewById(R.id.editTextNote)
        buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto)

        buttonTakePhoto.setOnClickListener {
            checkCameraPermissionAndOpen()
        }
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        try {
            val photoFile = createImageFile()
            val authorities = "${requireContext().packageName}.fileprovider"
            imageUri = FileProvider.getUriForFile(requireContext(), authorities, photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            cameraActivityResultLauncher.launch(intent)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Ошибка создания файла для фото", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir)
    }
}
```

#### Пример `AudioFragment.kt`

```kotlin
package ru.mirea.kornilovku.mireaproject.ui

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ru.mirea.kornilovku.mireaproject.R
import java.io.File

class AudioFragment : Fragment(R.layout.fragment_audio) {

    private lateinit var textViewStatus: TextView
    private lateinit var buttonRecord: Button
    private lateinit var buttonPlay: Button

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isRecording = false
    private var isPlaying = false
    private var isPermissionGranted = false

    private lateinit var audioFilePath: String

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            isPermissionGranted = granted
            if (!granted) {
                Toast.makeText(requireContext(), "Разрешение на микрофон не выдано", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewStatus = view.findViewById(R.id.textViewAudioStatus)
        buttonRecord = view.findViewById(R.id.buttonRecordAudio)
        buttonPlay = view.findViewById(R.id.buttonPlayAudio)

        audioFilePath = "${requireContext().externalCacheDir?.absolutePath}/voice_note.m4a"

        checkAudioPermission()
        updateUi()

        buttonRecord.setOnClickListener {
            if (!isPermissionGranted) {
                checkAudioPermission()
                Toast.makeText(requireContext(), "Сначала выдайте разрешение на микрофон", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isRecording) {
                stopRecording()
            } else {
                startRecording()
            }
            updateUi()
        }

        buttonPlay.setOnClickListener {
            if (isPlaying) {
                stopPlaying()
            } else {
                startPlaying()
            }
            updateUi()
        }
    }

    private fun checkAudioPermission() {
        isPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!isPermissionGranted) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecording() {
        try {
            val file = File(audioFilePath)
            if (file.exists()) {
                file.delete()
            }

            recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(requireContext())
            } else {
                MediaRecorder()
            }

            recorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }

            isRecording = true
            textViewStatus.text = "Идёт запись голосовой заметки"
            Toast.makeText(requireContext(), "Запись началась", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            textViewStatus.text = "Ошибка записи"
            Toast.makeText(requireContext(), "Ошибка записи: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Ошибка остановки записи: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            recorder = null
            isRecording = false
        }

        val file = File(audioFilePath)
        textViewStatus.text = "Запись сохранена, размер: ${file.length()} байт"
        Toast.makeText(requireContext(), "Запись остановлена", Toast.LENGTH_SHORT).show()
    }

    private fun startPlaying() {
        val file = File(audioFilePath)
        if (!file.exists() || file.length() == 0L) {
            Toast.makeText(requireContext(), "Сначала запишите голосовую заметку", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(audioFilePath)
                prepare()
                start()
                setOnCompletionListener {
                    stopPlaying()
                    updateUi()
                }
            }

            isPlaying = true
            textViewStatus.text = "Идёт воспроизведение голосовой заметки"
            Toast.makeText(requireContext(), "Воспроизведение началось", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            textViewStatus.text = "Ошибка воспроизведения"
            Toast.makeText(requireContext(), "Ошибка воспроизведения: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopPlaying() {
        try {
            player?.stop()
        } catch (_: Exception) {
        }

        player?.release()
        player = null
        isPlaying = false
        textViewStatus.text = "Воспроизведение остановлено"
        Toast.makeText(requireContext(), "Воспроизведение остановлено", Toast.LENGTH_SHORT).show()
    }

    private fun updateUi() {
        buttonRecord.text = if (isRecording) "Остановить запись" else "Начать запись"
        buttonPlay.text = if (isPlaying) "Остановить воспроизведение" else "Воспроизвести"

        buttonPlay.isEnabled = !isRecording
        buttonRecord.isEnabled = !isPlaying
    }

    override fun onStop() {
        super.onStop()

        if (isRecording) {
            stopRecording()
        }
        if (isPlaying) {
            stopPlaying()
        }
        updateUi()
    }
}
```

В итоге контрольное задание было выполнено: в `MireaProject` добавлены три экрана, использующие аппаратные возможности устройства для решения прикладных задач.

---

## 5. Вывод

В ходе выполнения практической работы были изучены способы использования аппаратных возможностей Android-устройств. Были реализованы:

* проект со списком датчиков устройства;
* модуль отображения показаний акселерометра;
* механизм получения runtime-разрешений;
* модуль работы с камерой с сохранением и отображением изображения;
* модуль записи и воспроизведения аудио;
* итоговая интеграция аппаратных функций в `MireaProject`.

В результате работы были получены практические навыки использования `SensorManager`, `SensorEventListener`, `FileProvider`, `MediaRecorder`, `MediaPlayer`, а также механизмов взаимодействия приложения с системными компонентами Android.
