# Отчет по практической работе №2
## Дисциплина: Разработка мобильных приложений

**Выполнил:** Студент группы БСБО-09-23 
**ФИО:** Корнилов Кирилл Юрьевич  
**Номер по списку:** 13 

---

## 1. Цель работы
Изучить и освоить на практике жизненный цикл компонентов Activity в ОС Android. Научиться создавать многоэкранные приложения, передавать данные между экранами (явные намерения) и взаимодействовать со сторонними приложениями (неявные намерения). Изучить механизмы информирования пользователя: всплывающие подсказки (Toast), системные уведомления (Notification) и диалоговые окна (Dialogs), включая самостоятельную реализацию различных типов диалогов с использованием языка Kotlin.

## 2. Структура проекта
В рамках выполнения практической работы был создан проект `Lesson2`, содержащий следующие независимые модули:
1. `ActivityLifecycle` — изучение методов жизненного цикла приложения.
2. `MultiActivity` — явные вызовы Activity и передача данных через Intent.
3. `IntentFilter` — неявные вызовы (браузер, системное меню "Поделиться").
4. `ToastApp` — работа со всплывающими подсказками.
5. `NotificationApp` — создание каналов и отправка Push-уведомлений.
6. `Dialog` — работа с фрагментами диалоговых окон (AlertDialog, TimePicker, DatePicker, ProgressDialog) и Snackbar.

---

## 3. Выполнение работы

### Задание 1. Модуль `ActivityLifecycle`
**Цель:** Отследить вызовы системных методов при смене состояний экрана.
В классе `MainActivity` были переопределены все методы жизненного цикла (`onStart`, `onResume`, `onPause`, `onStop`, `onDestroy` и др.) с добавлением логирования (`Log.i`). На экран добавлено поле `EditText`.

**Ответы на контрольные вопросы:**
1. **Будет ли вызван метод `onCreate` после нажатия на кнопку «Home» и возврата в приложение?**
   *Ответ:* Нет. Метод `onCreate` вызывается только при первичном создании Activity. При сворачивании (кнопка Home) вызываются `onPause()` и `onStop()`. При возврате — `onRestart()`, `onStart()` и `onResume()`.
2. **Изменится ли значение поля `EditText` после нажатия на кнопку «Home» и возврата в приложение?**
   *Ответ:* Нет, не изменится. Android автоматически сохраняет состояние базовых элементов интерфейса (с установленным ID) при переходе в фоновый режим.
3. **Изменится ли значение поля `EditText` после нажатия на кнопку «Back» и возврата в приложение?**
   *Ответ:* Да, текст исчезнет. Кнопка «Back» полностью уничтожает текущую Activity (вызывается `onDestroy()`). При повторном запуске приложение создается с нуля (вызывается `onCreate()`).

---

### Задание 2. Модуль `MultiActivity` (Явные намерения)
**Цель:** Реализовать переход между двумя Activity с передачей текста.
В `MainActivity` реализован захват текста из поля ввода и его упаковка в объект `Intent` с помощью `putExtra`. В `SecondActivity` текст извлекается через `getStringExtra` и выводится на экран.

**Листинг `MainActivity.kt` (Отправка):**
```kotlin
btnSend.setOnClickListener {
    val textToSend = editText.text.toString()
    val intent = Intent(this, SecondActivity::class.java)
    intent.putExtra("key", textToSend)
    startActivity(intent)
}
```

---

### Задание 3. Модуль `IntentFilter` (Неявные намерения)
**Цель:** Использовать системные приложения для обработки пользовательских запросов.
Реализовано две кнопки: одна для открытия веб-страницы (через `ACTION_VIEW`), другая для передачи данных студента в другие приложения (через `ACTION_SEND` и `createChooser`).

**Листинг фрагмента `MainActivity.kt`:**
```kotlin
btnOpenBrowser.setOnClickListener {
    val address = Uri.parse("https://www.mirea.ru/")
    val openLinkIntent = Intent(Intent.ACTION_VIEW, address)
    startActivity(openLinkIntent)
}

btnShareData.setOnClickListener {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "MIREA")
    shareIntent.putExtra(Intent.EXTRA_TEXT, "ФИО Студента, Группа")
    startActivity(Intent.createChooser(shareIntent, "МОИ ФИО"))
}
```

---

### Задание 4. Модуль `ToastApp`
**Цель:** Подсчет символов и вывод всплывающего уведомления (Toast).

**Листинг фрагмента `MainActivity.kt`:**
```kotlin
btnCount.setOnClickListener {
    val text = editText.text.toString()
    val count = text.length
    val message = "СТУДЕНТ № 1 ГРУППА XXXX-00-00 Количество символов - $count"
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
```

---

### Задание 5. Модуль `NotificationApp`
**Цель:** Реализация системного уведомления в Status Bar.
Учтены требования Android 13+ (запрос разрешения `POST_NOTIFICATIONS`). Реализовано создание `NotificationChannel` (обязательно для Android 8+) и сборка уведомления через паттерн Builder.

**Листинг создания уведомления (`MainActivity.kt`):**
```kotlin
val builder = NotificationCompat.Builder(this, CHANNEL_ID)
    .setContentTitle("Mirea")
    .setContentText("Congratulation!")
    .setSmallIcon(android.R.drawable.ic_dialog_info)
    .setPriority(NotificationCompat.PRIORITY_HIGH)

val channel = NotificationChannel(CHANNEL_ID, "Student Notification", NotificationManager.IMPORTANCE_DEFAULT)
val notificationManager = NotificationManagerCompat.from(this)
notificationManager.createNotificationChannel(channel)
notificationManager.notify(1, builder.build())
```

---

### Задание 6. Модуль `Dialog` (Включая самостоятельную работу)
**Цель:** Разработать различные типы диалоговых окон с использованием `DialogFragment`.

Были созданы 4 отдельных класса-наследника `DialogFragment`:
1. `MyDialogFragment` — стандартный Alert Dialog с 3 кнопками (Иду дальше, На паузе, Нет).
2. `MyTimeDialogFragment` — диалог выбора времени (`TimePickerDialog`).
3. `MyDateDialogFragment` — диалог выбора даты (`DatePickerDialog`).
4. `MyProgressDialogFragment` — диалог с индикатором загрузки (`ProgressBar`).

Все классы передают результат выбора обратно в `MainActivity`, где происходит обработка и вывод результатов с использованием `Toast` и современного `Snackbar`.

**Листинг базового диалога `MyDialogFragment.kt`:**
```kotlin
class MyDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Здравствуй МИРЭА!")
            .setMessage("Успех близок?")
            .setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton("Иду дальше") { dialog, _ ->
                (activity as MainActivity).onOkClicked()
                dialog.cancel()
            }
            .setNeutralButton("На паузе") { dialog, _ ->
                (activity as MainActivity).onNeutralClicked()
                dialog.cancel()
            }
            .setNegativeButton("Нет") { dialog, _ ->
                (activity as MainActivity).onCancelClicked()
                dialog.cancel()
            }
        return builder.create()
    }
}
```

**Листинг вызова Snackbar (`MainActivity.kt`):**
```kotlin
btnProgress.setOnClickListener {
    val progressDialog = MyProgressDialogFragment()
    progressDialog.show(supportFragmentManager, "progress_dialog")

    // Симуляция загрузки и показ Snackbar
    Handler(Looper.getMainLooper()).postDelayed({
        progressDialog.dismiss()
        Snackbar.make(it, "Загрузка успешно завершена!", Snackbar.LENGTH_LONG).show()
    }, 3000)
}
```

---

## 4. Вывод
В результате выполнения практической работы были успешно изучены механизмы жизненного цикла Activity и экспериментально подтверждено поведение приложения при сворачивании и закрытии. Получены практические навыки настройки явных и неявных намерений (Intent) для создания многооконных приложений и взаимодействия с системой Android. 

Был успешно освоен инструментарий информирования пользователя: реализованы короткие всплывающие подсказки (Toast), настроены Push-уведомления (Notification) с учетом политик безопасности новых версий Android, а также успешно выполнена самостоятельная работа по проектированию и внедрению четырех различных типов диалоговых окон (Alert, DatePicker, TimePicker, Progress) и компонента Snackbar с использованием возможностей языка Kotlin.