<h1 align="center">Simple Flashlight</h1>

![Compose BOM](https://img.shields.io/badge/Compose%20Bom-2026.03.00-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple)
![Min Sdk](https://img.shields.io/badge/Min%20Sdk-26-green)

Simple android flashlight app on jetpack compose📱🔦.

## 🖼️Gallery(in light/dark themes)

<table>
    <tr>
        <td>
            <img src="previews/screen1.jpg">
        </td>
        <td>
            <img src="previews/screen3.jpg">
        </td>
        <td>
            <img src="previews/screen4.jpg">
        </td>
    </tr>
</table>

## Android versions🔃
Android 8.0 and later.

## Tech stack📚
* Jetpack Compose
* Accompanist

## Features🌟
* runtime permission check✅
* battery level control🪫 *(flashlight turns off when the battery is low)*
* synchronization with the phone flashlight state from the outside📱
* camera flashlight feature support check📸🔍

## How it works?📃
When launched, the application checks for flashlight support on the phone's camera, then if it is present (otherwise, a screen is displayed informing that it is not supported), it grants camera permission using the **Accompanist** library. Then, if permission is granted, a **torch callback** (to synchronize the flashlight's state if the flashlight was turned on externally) and a **battery charge broadcast receiver** (which monitors the battery level) are registered. Afterward, the user can turn the flashlight on and off. When the battery level is low (approximately **5%** or less), flashlight will turn off and the app will notify the user.