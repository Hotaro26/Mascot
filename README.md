<p align="center">
<img width="300" height="300" alt="mascot_icon" src="https://github.com/user-attachments/assets/acbc7ecb-5e85-4049-90d9-26f59540287e" />


</p>

# Mascot

sup! welcome to Mascot (formerly mastco/matco) — it's basically this evil open-source alarm app I made that literally *forces* you out of bed. if you're the type to snooze 50 times and end up late to everything, you kinda need this. 

Mascot makes you do annoying stuff like solving math problems, scanning random QR codes, or even walking to your bathroom to take a picture of your sink just to turn off the alarm. the AI object detection is all on-device so it's super fast and doesn't steal your data.

### Key Features (Why this app is truly evil)
- **Invincible Mode:** it uses Device Administrator privileges so you literally can't uninstall the app while the alarm is blasting. nice try though.
- **Anti Power-Off:** leverages Accessibility Services to block you from turning off or restarting your phone when the alarm goes off. you are trapped.
- **4 Wake-up Challenges:**
  - **Math:** solve basic to hardcore math equations.
  - **Puzzles:** a lot of fun puzzles which actually forces your brain to get out of that unconcious state
  - **QR Code:** print a QR code, stick it in your bathroom, and go scan it to shut the alarm up.
  - **Flashbang:** literally blinds you with a bright flashing screen (you can double-tap to dismiss if you're weak).
  - **AI Object Detection:** forces you to get out of bed and take a picture of a random object (like a chair, toothbrush, grass, or even your cat).
- **Predictive Back Gestures & Minimalist UI:** built with Jetpack Compose Material 3, squishy cards, custom mascot icons, and a super clean dark mode.
- **Strict Mode:** locks down your alarm settings so you can't cheat and change the time 5 minutes before it rings.

oh, and if you hate oversleeping and actually find this app useful, dropping a star on the repo would be sick.
## Screenshots


| | | | | |
|:---:|:---:|:---:|:---:|:---:|
| <img width="200" alt="Screenshot_20260711-215802_mascot" src="https://github.com/user-attachments/assets/cad64fa7-68c2-4c18-a707-91a6bd1e769b" /> | <img width="200" alt="Screenshot_20260711-215807_mascot" src="https://github.com/user-attachments/assets/6b74c0fb-a328-492f-bee0-4e6b016bee27" /> | <img width="200" alt="Screenshot_20260711-182217_mastco" src="https://github.com/user-attachments/assets/8cbe5b5e-1f39-4e05-9bba-2b2bf5346353" /> | <img width="200" alt="Screenshot_20260711-182400_mastco" src="https://github.com/user-attachments/assets/2b074c5d-5c27-41c6-ba2e-989f2195ca3e" /> | <img width="200" alt="Screenshot_20260711-182600_mastco" src="https://github.com/user-attachments/assets/28e67025-5613-4b23-870d-a1c92d5fe6d4" /> |
| <img width="200" alt="Screenshot_20260711-203449_System UI" src="https://github.com/user-attachments/assets/28a1df36-242c-49b8-88ba-9c51a757ef90" /> | <img width="200" alt="Screenshot_20260711-215726_mascot" src="https://github.com/user-attachments/assets/ae9b815e-7032-4385-aecd-f15c005e8a2a" /> | <img width="200" alt="Screenshot_20260711-215730_mascot" src="https://github.com/user-attachments/assets/11f45e11-27f8-4639-af57-8bf10cb1ffeb" /> | <img width="200" alt="Screenshot_20260711-215735_mascot" src="https://github.com/user-attachments/assets/e58de9a4-ffcd-402c-97c7-7a6614a6679d" /> | <img width="200" alt="Screenshot_20260711-215739_mascot" src="https://github.com/user-attachments/assets/2da14be3-66c1-4b54-946f-0d728ba749e1" /> |

## Tablet ui

| | | |
|:---:|:---:|:---:|
| <img width="300" alt="Screenshot_20260711-220644_mascot" src="https://github.com/user-attachments/assets/977a7b2d-b7d1-4492-b2cb-ad6849227eed" /> | <img width="300" alt="Screenshot_20260711-220540_mascot" src="https://github.com/user-attachments/assets/f04bb148-f266-4a79-bedc-095fc4fc0f93" /> | <img width="300" alt="Screenshot_20260711-220556_mascot" src="https://github.com/user-attachments/assets/4be1607d-bf21-49d4-9993-6eedec8916bd" /> |
| <img width="300" alt="Screenshot_20260711-220609_mascot" src="https://github.com/user-attachments/assets/eca9f55c-c7ca-4cf7-91a7-1229c392ef68" /> | <img width="300" alt="Screenshot_20260711-220629_mascot" src="https://github.com/user-attachments/assets/331936a5-c852-4a90-b157-1114ccaed223" /> | <img width="300" alt="Screenshot_20260711-220637_mascot" src="https://github.com/user-attachments/assets/7a81fbb2-2b89-4745-b681-51f3dc9c56d3" /> |



### What's under the hood?

I built this thing using some pretty cool stuff:
- **Kotlin & Jetpack Compose:** all the UI is modern and smooth (the cards even get squishy when you press them).
- **Google ML Kit:** handles all the on-device AI for object detection (like recognizing your toothbrush) and QR scanning offline!
- **Room Database:** saves your alarms locally.
- **Coroutines & Services:** keeps the alarm and timers running perfectly in the background so you can't escape it.

### How to build it locally

if you wanna mess with the code or build it yourself, it's super easy.

1. clone the repo:
   ```bash
   git clone https://github.com/Hotaro26/Mascot.git
   cd Mascot
   ```
2. open the project in Android Studio (or whatever you use).
3. let Gradle sync all the dependencies.
4. hit the run button or build an APK from the command line:
   ```bash
   ./gradlew assembleDebug
   ```
5. install the APK on your phone and try not to throw it at a wall when the alarm goes off.

### Contributing

wanna add a new feature or fix a bug? bet. i'm totally down for pull requests. 
1. fork the repo.
2. create a new branch (`git checkout -b feature/your-cool-idea`).
3. make your changes and commit them.
4. push to your fork and open a PR.

just make sure your code isn't super messy and you're good to go.
## Localization:
[Help translate the Mascot App on Crowdin](https://crowdin.com/project/mascot-app)

### License

Mascot is licensed under the [MIT License](LICENSE). do whatever you want with the code, just don't blame me if you still sleep through your math problems.

## Star History

<a href="https://www.star-history.com/?repos=Hotaro26%2FMascot&type=date&legend=top-left">
 <picture>
   <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=Hotaro26/Mascot&type=date&theme=dark&legend=top-left&sealed_token=PbH68iOU-Q_qMTaqKhcUEkhGfRr6-3bso9AQ0M9V6woOeBoTM04E6LvdCzwys0C7lJKqhNGQzBsgySklq-o2JVmUSVbDWZz5x04gdiuDDZ0cRU6ItAtt6my08j-ybSqPtkOZYuJox4KC2Tznc7tSl4hAKU-30RflwYasq7XtDTiJ6jvFeU0s5ccEEAtv" />
   <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=Hotaro26/Mascot&type=date&legend=top-left&sealed_token=PbH68iOU-Q_qMTaqKhcUEkhGfRr6-3bso9AQ0M9V6woOeBoTM04E6LvdCzwys0C7lJKqhNGQzBsgySklq-o2JVmUSVbDWZz5x04gdiuDDZ0cRU6ItAtt6my08j-ybSqPtkOZYuJox4KC2Tznc7tSl4hAKU-30RflwYasq7XtDTiJ6jvFeU0s5ccEEAtv" />
   <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=Hotaro26/Mascot&type=date&legend=top-left&sealed_token=PbH68iOU-Q_qMTaqKhcUEkhGfRr6-3bso9AQ0M9V6woOeBoTM04E6LvdCzwys0C7lJKqhNGQzBsgySklq-o2JVmUSVbDWZz5x04gdiuDDZ0cRU6ItAtt6my08j-ybSqPtkOZYuJox4KC2Tznc7tSl4hAKU-30RflwYasq7XtDTiJ6jvFeU0s5ccEEAtv" />
 </picture>
</a>
