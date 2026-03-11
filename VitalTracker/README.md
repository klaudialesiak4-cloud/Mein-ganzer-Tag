# VitalTracker – Ihre persönliche Vitalwerte-App

Eine Android-App für Samsung-Geräte zur wöchentlichen Erfassung und Visualisierung von Vitalwerten.

## Funktionen

### Seite 1 – Eingabe
- Messdatum (Datumsauswahl)
- **Gewicht** (kg) mit BMI-Anzeige
- **Blutdruck** (systolisch / diastolisch in mmHg)
- **Puls** (bpm)
- **Sauerstoffsättigung** (%)
- Optionales Notizfeld
- Eingabevalidierung mit sinnvollen Fehlermeldungen

### Seite 2 – Verlauf (Grafiken)
5 separate Grafiken (wählbar über Tabs):
- Blutdruck systolisch
- Blutdruck diastolisch
- Puls
- Sauerstoffsättigung
- Gewicht

Jede Grafik zeigt:
- **Messwert-Verlauf** als Liniengraph
- **Oberer und unterer Normalwert** als gestrichelte rote Linien
- **Aktuellen Wert** + farbiger Status (Normal / Zu hoch / Zu niedrig)
- Zoom & Scrollen möglich

### Seite 3 – Einstellungen
Persönliche Daten zur Berechnung der individuellen Normalwerte:
- Name, Alter, Körpergröße, Geschlecht
- Sportler-Modus (senkt Puls-Normalbereich auf 40–100 bpm)
- Vorschau der berechneten Normalwerte

## Normalwerte-Berechnung

| Vitalwert | Grundlage |
|---|---|
| Blutdruck | Altersabhängig (Deutsche Hochdruckliga) |
| Puls | 60–100 bpm (Sportler: 40–100) |
| Sauerstoffsättigung | ≥ 95 % |
| Gewicht | BMI 18,5–24,9 (nach Körpergröße) |

## Technologie

- **Kotlin** + Android Jetpack
- **Room** (SQLite) – lokale Datenspeicherung
- **MPAndroidChart** – Verlaufsgrafiken
- **Navigation Component** – 3-Tab Bottom Navigation
- **ViewModel + LiveData** – reaktive Architektur
- **Material Design 3** – modernes UI

## Öffnen in Android Studio

1. Android Studio öffnen → **Open Project** → Ordner `VitalTracker` auswählen
2. `local.properties` anlegen:
   ```
   sdk.dir=/Users/<Name>/Library/Android/sdk   # macOS
   sdk.dir=C:\Users\<Name>\AppData\Local\Android\sdk   # Windows
   ```
3. Gradle sync abwarten
4. Gerät per USB verbinden (Samsung Galaxy) oder Emulator starten
5. **Run** ▶️

## Mindestanforderung
- Android 8.0 (API 26) oder neuer
- Samsung Galaxy: alle aktuellen Modelle unterstützt
