# NotiPay

**[Español](#español) · [English](#english)**

Hands-free, spoken confirmation of incoming Yape payments — for any vendor who
wants instant payment confirmation without watching the screen.

---

## Español

Confirmación de pagos de Yape por voz y sin manos — para cualquier vendedor que
quiera confirmar pagos al instante sin mirar la pantalla.

### El problema

Existen apps falsas que imitan a Yape y muestran una pantalla de *"pago enviado"*
muy convincente en el celular de **quien paga**. El que recibe ve la pantalla falsa,
entrega el producto, y el dinero nunca llega.

### El enfoque

Confiar en lo que el celular **recibe**, no en lo que la otra persona **muestra**.
Una notificación real de Yape solo llega cuando el dinero realmente se transfirió.
NotiPay escucha esa notificación oficial, la lee, la dice en voz alta (opcionalmente
por un parlante Bluetooth) y guarda un historial local.

### Funcionalidades

- 🔊 Anuncia cada pago en voz alta: *"Confirmación de pago. {nombre} te envió {monto}."*
- ⏸️ Pausar / reanudar la lectura con un interruptor.
- 🧾 Historial local de cada pago, filtrable por **día / mes / año**, con total acumulado.
- 🧪 Botón "Prueba de audio" para verificar el sonido (y el parlante Bluetooth) sin un pago real.
- 🔋 Botón para evitar que el sistema cierre la app por ahorro de batería.

### Requisito de plataforma

Leer las notificaciones de otra app requiere `NotificationListenerService` de Android.
iOS **no tiene equivalente** — Apple no permite que apps de terceros lean notificaciones
de otras apps. Por eso NotiPay corre en el celular que **recibe** los pagos, y ese
celular **debe ser Android**.

### Instalación y configuración

1. **Generar el APK** (dentro de la carpeta del proyecto):
   ```bash
   ./gradlew assembleDebug
   ```
   El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.
2. **Pasar el APK** al celular (Google Drive o Telegram; WhatsApp suele bloquear los `.apk`).
3. **Instalar**: abrir el archivo y permitir "instalar apps desconocidas" para la app desde la que se abre.
4. **Permitir ajustes restringidos** (Android 13+, frecuente en Xiaomi/MIUI):
   si al intentar dar acceso a notificaciones el interruptor está **gris** y dice
   *"Función controlada por configuración restringida"*, hacer esto:
   - Ajustes → Aplicaciones → Gestionar aplicaciones → **NotiPay**
   - Tocar los **tres puntos (⋮)** arriba a la derecha
   - Tocar **"Permitir ajustes restringidos"**

   Esto es una protección de Android para apps instaladas por fuera de la Play Store; no es un error.
5. **Dar acceso a notificaciones**: en la app, tocar "Activar acceso a notificaciones",
   buscar NotiPay y activarlo.
6. **Evitar el cierre por batería**: tocar "Evitar que el sistema lo cierre" y permitir.
7. **Emparejar el parlante Bluetooth** desde los ajustes del sistema.
8. **Probar**: pedir un Yape de S/ 0.10 → debería escucharse la voz y verse el pago en la lista.

### Arquitectura

MVVM con un contenedor de dependencias manual y liviano (sin framework de DI).

```
notification/  NotificationListenerService → parsear → guardar + anunciar
domain/        YapeParser, YapePayment, Money (Kotlin puro, con tests)
data/          base de datos Room + repositorios (pagos, ajustes)
ui/            Jetpack Compose (Material 3) + ViewModel + StateFlow
```

- El dinero se guarda en **céntimos enteros (Long)**, nunca en punto flotante.
- El listener se filtra por el nombre del paquete oficial de Yape.
- El audio sale por el dispositivo activo, así que el parlante Bluetooth no necesita código.

### Stack

Kotlin · Jetpack Compose · Material 3 · Room · Coroutines/Flow · MVVM

### Decisiones de diseño

- **El código de seguridad no se dice en voz por defecto** — es sensible, y anunciarlo
  por un parlante en público pierde sentido. Configurable vía `speakSecurityCode`.
- **Compose nativo en vez de multiplataforma** — la API central es nativa e iOS es imposible igual.

### Próximos pasos

- 🟢 Compatibilidad con **Plin** (otra billetera peruana). La arquitectura ya lo facilita:
  solo hay que agregar el paquete oficial de Plin y un parser para su formato de notificación;
  todo lo demás (voz, historial, filtros, base de datos) se reutiliza.

---

## English

### The problem

Fraudulent apps imitate Yape and show a convincing fake *"payment sent"* screen on
the **payer's** phone. The receiver sees the fake, hands over the goods, and the
money never arrives.

### The approach

Trust what the receiver's phone **receives**, not what the payer **shows**.
A genuine Yape push notification only lands when real money is transferred.
NotiPay listens for that official notification, parses it, speaks it aloud
(optionally through a paired Bluetooth speaker), and keeps a local history.

### Features

- 🔊 Speaks each payment out loud in Spanish: *"Confirmación de pago. {name} te envió {amount}."*
- ⏸️ Pause / resume listening with one switch.
- 🧾 Local history of every payment, filterable by **day / month / year**, with a running total.
- 🧪 "Test audio" button to verify sound (and a Bluetooth speaker) without a real transfer.
- 🔋 Button to keep the OS from killing the app for battery saving.

### Platform requirement

Reading another app's notifications requires Android's `NotificationListenerService`.
iOS provides **no equivalent** — Apple forbids third-party apps from reading other
apps' notifications. NotiPay therefore runs on the **receiving** phone, which must be Android.

### Installation & setup

1. **Build the APK** (from the project folder):
   ```bash
   ./gradlew assembleDebug
   ```
   The APK is at `app/build/outputs/apk/debug/app-debug.apk`.
2. **Send the APK** to the phone (Google Drive or Telegram; WhatsApp often blocks `.apk` files).
3. **Install**: open the file and allow "install unknown apps" for the app you open it from.
4. **Allow restricted settings** (Android 13+, common on Xiaomi/MIUI):
   if the notification-access toggle is **greyed out** and says
   *"Feature controlled by restricted setting"*, do this:
   - Settings → Apps → Manage apps → **NotiPay**
   - Tap the **three-dot menu (⋮)** at the top right
   - Tap **"Allow restricted settings"**

   This is an Android protection for apps installed outside the Play Store; it is not a bug.
5. **Grant notification access**: in the app, tap "Activar acceso a notificaciones",
   find NotiPay, and enable it.
6. **Disable battery optimization**: tap "Evitar que el sistema lo cierre" and allow.
7. **Pair the Bluetooth speaker** from system settings.
8. **Test**: have someone send an S/ 0.10 Yape → you should hear the voice and see it in the list.

### Architecture

MVVM with a thin manual dependency container (no DI framework).

```
notification/  NotificationListenerService → parse → record + announce
domain/        YapeParser, YapePayment, Money (pure Kotlin, unit-tested)
data/          Room database + repositories (payments, settings)
ui/            Jetpack Compose (Material 3) + ViewModel + StateFlow
```

- Money is stored as integer **cents (Long)** — never floating point.
- The notification listener is filtered by the official Yape package name.
- Audio routes to the active output device, so a paired Bluetooth speaker needs no code.

### Tech

Kotlin · Jetpack Compose · Material 3 · Room · Coroutines/Flow · MVVM

### Design decisions

- **Security code muted in voice by default** — it is sensitive, and broadcasting it
  through a speaker in public defeats its purpose. Toggle via `speakSecurityCode`.
- **Native Compose over cross-platform** — the core API is native and iOS is impossible anyway.

### Roadmap

- 🟢 **Plin** support (another Peruvian wallet). The architecture already makes this easy:
  add Plin's official package name and a parser for its notification format; everything
  else (voice, history, filters, database) is reused.

### Status

Working app — builds and runs. Real Yape parsing must be validated on a physical
Android device with Yape installed.

---

## Licencia · License

MIT — ver / see [LICENSE](LICENSE).

## Autor · Author

**[AroxArth](https://github.com/AroxArth)** — más proyectos open source ahí ·
more open-source projects there.
