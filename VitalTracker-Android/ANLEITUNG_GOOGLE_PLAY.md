# VitalTracker – Schritt-für-Schritt: Google Play veröffentlichen

---

## SCHRITT 1 – Android Studio installieren

1. Gehen Sie auf: https://developer.android.com/studio
2. Klicken Sie auf **"Download Android Studio"**
3. Setup-Datei ausführen → alles auf Standard lassen → **Next** durchklicken
4. Beim ersten Start: **"Standard"** Installation wählen → fertigstellen
   ⏱ Dauer: ca. 20–30 Minuten

---

## SCHRITT 2 – Projekt öffnen

1. Android Studio starten
2. **"Open"** klicken (NICHT "New Project")
3. Den Ordner **`VitalTracker-Android`** auswählen → **OK**
4. Unten erscheint ein Ladebalken "Gradle sync" → warten bis fertig
   ⏱ Dauer: ca. 5–10 Minuten (lädt Abhängigkeiten)

---

## SCHRITT 3 – App auf Ihrem Samsung testen

1. Am Samsung: **Einstellungen → Über das Telefon → Software-Informationen**
   → 7x auf **"Build-Nummer"** tippen → Entwickleroptionen sind aktiv
2. **Einstellungen → Entwickleroptionen → USB-Debugging** einschalten
3. Samsung per USB-Kabel mit dem PC verbinden
4. Am Handy: Popup "USB-Debugging zulassen?" → **Zulassen**
5. In Android Studio oben das Samsung-Gerät auswählen
6. Grüner **▶ Play-Button** drücken
7. App startet auf Ihrem Handy!

---

## SCHRITT 4 – Signierte AAB-Datei erstellen (für Google Play)

1. In Android Studio: Menü **Build → Generate Signed Bundle / APK**
2. **"Android App Bundle"** auswählen → **Next**
3. **"Create new..."** klicken:
   - Key store path: Speicherort wählen (z.B. `C:\vitaltracker-key.jks`)
   - Password: ein sicheres Passwort (MERKEN oder notieren!)
   - Key alias: `vitaltracker`
   - Key password: gleiches Passwort
   - First and Last Name: Ihr Name
   - Country Code: `AT` (Österreich) oder `DE` (Deutschland)
   → **OK**
4. **Next** → **Release** auswählen → **Finish**
5. Die fertige Datei liegt in:
   `VitalTracker-Android/app/release/app-release.aab`

---

## SCHRITT 5 – Google Play Developer-Konto einrichten

1. Gehen Sie auf: https://play.google.com/console
2. Mit Google-Konto anmelden
3. Einmalige Gebühr: **25 USD** (ca. 23 €) bezahlen
4. Entwicklerprofil ausfüllen → bestätigen

---

## SCHRITT 6 – App hochladen und veröffentlichen

1. In der Play Console: **"App erstellen"**
2. App-Name: `VitalTracker`
3. Sprache: Deutsch
4. App-Typ: App (kein Spiel)
5. Kostenlos / Kostenpflichtig wählen
6. Im linken Menü: **"Produktion" → "Neue Version erstellen"**
7. Die `app-release.aab` Datei hochladen
8. **Release-Notizen** eingeben (z.B. "Erste Version")
9. Store-Eintrag ausfüllen (siehe unten)
10. **"Zur Überprüfung einreichen"**
    ⏱ Google prüft ca. 1–3 Tage

---

## Store-Eintrag Texte (zum Kopieren)

**App-Name:** VitalTracker – Vitalwerte

**Kurzbeschreibung (max. 80 Zeichen):**
Wöchentliche Vitalwerte erfassen, verfolgen und visualisieren.

**Vollständige Beschreibung:**
VitalTracker hilft Ihnen, Ihre wichtigsten Gesundheitswerte im Blick zu behalten.

📊 Erfassen Sie wöchentlich:
• Gewicht (mit automatischer BMI-Berechnung)
• Blutdruck (systolisch und diastolisch)
• Puls
• Sauerstoffsättigung (SpO₂)

📈 Verlauf & Grafiken:
• Übersichtliche Liniendiagramme für jeden Messwert
• Persönliche Normalwerte als rote Grenzlinien
• Status-Anzeige: Normal / Zu hoch / Zu niedrig

⚙️ Persönliche Einstellungen:
• Normalwerte werden berechnet nach Alter, Größe, Geschlecht
• Sportler-Modus für niedrigeren Puls-Normalbereich
• Basiert auf Empfehlungen der Deutschen Hochdruckliga und WHO

🔒 100 % offline – alle Daten bleiben auf Ihrem Gerät.

**Kategorie:** Gesundheit & Fitness
**Inhaltsbewertung:** Für alle Altersgruppen

---

## Screenshot-Anforderungen (Google Play)

Sie benötigen mindestens 2 Screenshots vom Handy:
- Starten Sie die App auf Ihrem Samsung
- Machen Sie Screenshots der 3 Seiten:
  1. Eingabe-Seite (mit ausgefüllten Werten)
  2. Verlauf-Seite (mit Grafik)
  3. Einstellungen-Seite

Screenshots per USB oder Google Drive auf den PC übertragen
und in der Play Console hochladen.

---

## Bei Problemen

- **Gradle sync schlägt fehl:** Menü → File → Invalidate Caches → Restart
- **Gerät wird nicht erkannt:** USB-Treiber für Samsung installieren:
  https://www.samsung.com/de/support/model/smart-phones/
- **Fragen:** Gerne in diesem Chat nachfragen!
