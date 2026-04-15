# Отчет по практическому занятию № 6

**Дисциплина:** Разработка мобильных приложений  
**Тема:** Хранение данных в Android  
**Язык реализации:** Kotlin

## Содержание

1. Цель работы
2. Постановка задач
3. Краткие теоретические сведения
4. Структура проекта
5. Ход выполнения работы
6. Сборка и запуск
7. Вывод

## Цель работы

Цель практической работы заключается в изучении основных способов хранения данных в Android-приложениях и их применении на практике. В рамках задания были реализованы примеры работы с `SharedPreferences`, `EncryptedSharedPreferences`, внутренним и внешним хранилищем, а также локальной базой данных `Room`.

## Постановка задач

В ходе выполнения работы требовалось:

1. Реализовать экран с сохранением данных через `SharedPreferences`.
2. Создать модуль безопасного хранения данных через `EncryptedSharedPreferences`.
3. Реализовать запись и чтение файла во внутреннем хранилище приложения.
4. Создать приложение-блокнот с сохранением и загрузкой текстовых файлов во внешнем каталоге приложения.
5. Реализовать локальную базу данных с использованием `Room`.

> В текущем репозитории присутствуют только модули лабораторной работы `Lesson6`. Контрольное задание с `MireaProject`, упоминавшееся в исходном тексте, в проект не входит и поэтому в этом README не описывается.

## Краткие теоретические сведения

### SharedPreferences

`SharedPreferences` используется для хранения небольших наборов данных в формате "ключ-значение". Такой подход подходит для пользовательских настроек, строк, чисел и логических значений.

### EncryptedSharedPreferences

`EncryptedSharedPreferences` предоставляет тот же интерфейс, что и обычные настройки, но сохраняет данные в зашифрованном виде. Для работы используется `MasterKey` и библиотека `androidx.security:security-crypto`.

### Работа с файлами

Во внутреннем хранилище файлы доступны только приложению. Для этого применяются методы `openFileOutput()` и `openFileInput()`.  
Во внешнем каталоге приложения удобно работать через `getExternalFilesDir(...)`, например для сохранения текстовых файлов в `Documents`.

### SQLite и Room

`Room` является надстройкой над `SQLite`, которая упрощает работу с локальной базой данных за счет использования:

- `Entity` для описания таблиц;
- `DAO` для запросов;
- `RoomDatabase` для конфигурации базы данных.

## Структура проекта

| Модуль | Назначение | Ключевые файлы |
| --- | --- | --- |
| `app` | Пример хранения данных через `SharedPreferences` | [MainActivity.kt](app/src/main/java/ru/mirea/kornilovku/lesson6/MainActivity.kt), [activity_main.xml](app/src/main/res/layout/activity_main.xml) |
| `securesharedpreferences` | Безопасное хранение данных через `EncryptedSharedPreferences` | [MainActivity.kt](securesharedpreferences/src/main/java/ru/mirea/kornilovku/securesharedpreferences/MainActivity.kt), [build.gradle.kts](securesharedpreferences/build.gradle.kts) |
| `internalfilestorage` | Работа с файлом во внутреннем хранилище | [MainActivity.kt](internalfilestorage/src/main/java/ru/mirea/kornilovku/internalfilestorage/MainActivity.kt), [activity_main.xml](internalfilestorage/src/main/res/layout/activity_main.xml) |
| `notebook` | Сохранение и загрузка текстовых файлов во внешнем каталоге приложения | [MainActivity.kt](notebook/src/main/java/ru/mirea/kornilovku/notebook/MainActivity.kt), [activity_main.xml](notebook/src/main/res/layout/activity_main.xml) |
| `employeedb` | Локальная база данных `Room` | [MainActivity.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/MainActivity.kt), [Employee.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/Employee.kt), [EmployeeDao.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/EmployeeDao.kt), [AppDatabase.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/AppDatabase.kt) |

Дополнительно многомодульная структура проекта описана в [settings.gradle.kts](settings.gradle.kts).

## Ход выполнения работы

### 1. Модуль `app`: SharedPreferences

В модуле реализован экран с тремя полями ввода:

- номер группы;
- номер по списку;
- любимый фильм или сериал.

При нажатии на кнопку данные сохраняются в `SharedPreferences` с именем `mirea_settings`, а при повторном запуске приложения автоматически загружаются обратно в поля ввода.

Ключевой фрагмент реализации:

```kotlin
sharedPreferences = getSharedPreferences("mirea_settings", Context.MODE_PRIVATE)

editor.putString("GROUP", group)
editor.putInt("NUMBER", number)
editor.putString("FAVORITE", favorite)
editor.apply()
```

Основная логика находится в [app/src/main/java/ru/mirea/kornilovku/lesson6/MainActivity.kt](app/src/main/java/ru/mirea/kornilovku/lesson6/MainActivity.kt).

### 2. Модуль `securesharedpreferences`: EncryptedSharedPreferences

В этом модуле реализовано безопасное хранение двух параметров:

- любимого поэта;
- названия изображения.

Для шифрования используется `MasterKey` и `EncryptedSharedPreferences`. После сохранения данные сразу отображаются на экране в расшифрованном виде.

Подключенная зависимость:

```kotlin
implementation("androidx.security:security-crypto:1.1.0")
```

Ключевой фрагмент создания защищенного хранилища:

```kotlin
val masterKey = MasterKey.Builder(this)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

secureSharedPreferences = EncryptedSharedPreferences.create(
    this,
    "secret_shared_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

Исходники: [securesharedpreferences/src/main/java/ru/mirea/kornilovku/securesharedpreferences/MainActivity.kt](securesharedpreferences/src/main/java/ru/mirea/kornilovku/securesharedpreferences/MainActivity.kt), [securesharedpreferences/build.gradle.kts](securesharedpreferences/build.gradle.kts).

### 3. Модуль `internalfilestorage`: внутреннее хранилище

Модуль демонстрирует сохранение и чтение файла `history_date.txt` во внутреннем хранилище приложения. В качестве текста используется памятная дата в истории России, связанная с полетом Юрия Гагарина.

Для записи и чтения используются стандартные методы Android:

```kotlin
openFileOutput(fileName, Context.MODE_PRIVATE)
openFileInput(fileName)
```

После записи содержимое можно сразу прочитать и вывести в `TextView`.

Исходники: [internalfilestorage/src/main/java/ru/mirea/kornilovku/internalfilestorage/MainActivity.kt](internalfilestorage/src/main/java/ru/mirea/kornilovku/internalfilestorage/MainActivity.kt), [internalfilestorage/src/main/res/layout/activity_main.xml](internalfilestorage/src/main/res/layout/activity_main.xml).

### 4. Модуль `notebook`: внешнее хранилище приложения

Модуль `notebook` реализует простое приложение для сохранения цитат в текстовые файлы. Пользователь вводит имя файла и текст цитаты, после чего может:

- сохранить содержимое в файл;
- загрузить содержимое существующего файла обратно на экран.

Файлы сохраняются во внешний каталог приложения:

```kotlin
val path = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
val file = File(path, fileName)
```

Для чтения используется `BufferedReader` и кодировка `UTF-8`.

Исходники: [notebook/src/main/java/ru/mirea/kornilovku/notebook/MainActivity.kt](notebook/src/main/java/ru/mirea/kornilovku/notebook/MainActivity.kt), [notebook/src/main/res/layout/activity_main.xml](notebook/src/main/res/layout/activity_main.xml).

### 5. Модуль `employeedb`: Room

В модуле реализована локальная база данных с информацией о вымышленных супергероях. Архитектура включает:

- сущность `Employee`;
- интерфейс `EmployeeDao`;
- базу данных `AppDatabase`;
- класс `App` для инициализации `Room`;
- `MainActivity` для добавления и отображения записей.

Подключение `Room`:

```kotlin
implementation("androidx.room:room-runtime:2.8.4")
ksp("androidx.room:room-compiler:2.8.4")
```

Описание сущности:

```kotlin
@Entity(tableName = "employee")
data class Employee(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String,
    var power: String,
    var city: String
)
```

Для учебной задачи база создается через `allowMainThreadQueries()`, что допустимо в демонстрационном примере, но не рекомендуется для production-кода.

Исходники:

- [employeedb/src/main/java/ru/mirea/kornilovku/employeedb/Employee.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/Employee.kt)
- [employeedb/src/main/java/ru/mirea/kornilovku/employeedb/EmployeeDao.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/EmployeeDao.kt)
- [employeedb/src/main/java/ru/mirea/kornilovku/employeedb/AppDatabase.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/AppDatabase.kt)
- [employeedb/src/main/java/ru/mirea/kornilovku/employeedb/App.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/App.kt)
- [employeedb/src/main/java/ru/mirea/kornilovku/employeedb/MainActivity.kt](employeedb/src/main/java/ru/mirea/kornilovku/employeedb/MainActivity.kt)

## Сборка и запуск

Для сборки всех модулей из PowerShell можно использовать:

```powershell
.\gradlew.bat assembleDebug
```

Для запуска конкретного модуля удобнее открыть проект в Android Studio и выбрать нужную конфигурацию приложения.

## Вывод

В ходе выполнения практической работы были изучены и реализованы основные способы хранения данных в Android:

- `SharedPreferences` для хранения параметров пользователя;
- `EncryptedSharedPreferences` для защищенного хранения данных;
- внутреннее файловое хранилище приложения;
- внешний каталог приложения для работы с текстовыми файлами;
- локальная база данных `Room`.

Практическим результатом стала многомодульная Android-разработка, в которой каждый модуль демонстрирует отдельный способ хранения данных и может использоваться как самостоятельный учебный пример.
