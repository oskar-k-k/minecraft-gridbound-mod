# Gridbound – 3×3 Quest Borders

Eine eigenständige Mod für **Minecraft 1.21.1 / NeoForge**, entwickelt für ATM10 mit FTB Quests. Versionsziel ist **ATM10 8.2**: dessen [offizieller Changelog](https://github.com/AllTheMods/ATM-10/blob/main/changelogs/CHANGELOG-ATM10-8.1-8.2.md) nennt NeoForge 21.1.251 und FTB Quests 2101.1.36. Das benachbarte OrbitCraft-Modpack wird nicht verändert.

## Installation

1. ATM10 im Launcher schließen.
2. `gridbound-1.0.0.jar` aus `dist` (oder nach eigenem Build aus `build/libs`) in den `mods`-Ordner deiner ATM10-Instanz kopieren.
3. Bei einem Server dieselbe JAR auf **Client und Server** installieren.
4. Eine neue Welt starten. Bestehende Welten funktionieren ebenfalls: Dein Standort beim ersten Eintritt wird zum Startfeld; alte Questabschlüsse werden nicht nachträglich belohnt.

Zielversion: Java 21, Minecraft 1.21.1, NeoForge ab 21.1.219 und FTB Quests ab 2101.1.36. FTB Library, FTB Teams und Architectury gehören zu den Abhängigkeiten von FTB Quests und sind in ATM10 enthalten. Keine Forge-/Fabric-/1.21.10-Version verwenden.

## Spielen

- Du startest mit **3×3 Blöcken Grundfläche**, ohne Höhenbegrenzung.
- Das blaue Gitter markiert die geschlossenen Außengrenzen. Es werden keine Weltblöcke ersetzt.
- Jeder neue FTB-Questabschluss bringt **1 Punkt und 1 zufälliges Item**. Einzelne Aufgaben innerhalb einer Quest zählen nicht separat. Die normale Questbelohnung bleibt erhalten.
- Schaue in die gewünschte Richtung und drücke **G**. Die Taste lässt sich unter Optionen → Steuerung → Gridbound ändern, falls ATM10 bereits G verwendet.
- Das Menü zeigt Punkte und eine Karte (Norden oben, Weiß = Spieler, Blau = offen, Gelb = nächstes Feld). Wähle bei Bedarf Norden/Osten/Süden/Westen und drücke „3×3 freischalten“.
- Pro Punkt wird genau **ein Feld mit neun Blöcken** geöffnet: das erste geschlossene Feld entlang der gewählten Richtung, ausgehend von deiner aktuellen Rasterzeile/-spalte. So entstehen auch L-förmige Gebiete.
- Jede Dimension startet einmal mit einem eigenen 3×3-Feld am ersten Ankunftsort. Das Punktekonto gilt dimensionsübergreifend. Rückkehr in eine bekannte Dimension öffnet kein neues Startfeld.
- Im Multiplayer sind Gebiete und Punkte persönlich. Bei einer gemeinsamen FTB-Teamquest werden die **zu diesem Zeitpunkt online befindlichen Teammitglieder** belohnt. Offline-Mitglieder erhalten keine nachträgliche Gutschrift.
- Quest-IDs werden dauerhaft erfasst: Wiederholbare oder zurückgesetzte Quests bringen nur beim ersten Abschluss die zusätzliche Gridbound-Belohnung.
- Zufallsitems kommen gleichverteilt aus der Item-Registry einschließlich Mod-Items. Luft und einige Vanilla-Administratoritems sind ausgeschlossen; es gibt keine Seltenheitsgewichtung. Bei vollem Inventar wird der Gegenstand fallen gelassen.

Punkte, Quest-IDs, Dimensionen und Felder liegen in der Weltdatei `data/gridbound.dat`. Tod und Neustart löschen sie nicht. Die Mod setzt unerlaubte Bewegungen und Teleports serverseitig zurück. Direkter Blockabbau und Blockinteraktionen außerhalb des Gebiets werden gesperrt. Maschinen, Projektile und die zahlreichen speziellen ATM10-Werkzeuge werden nicht als vollständiges Claim-Schutzsystem gefiltert.

Ein zufälliges Startfeld kann für bestimmte Ressourcen ungeeignet sein. Eine neue ATM10-Welt mit ihren Einführungs-/Checkmarkquests ist deshalb der vorgesehene Einstieg. Das Zufallsitem-System garantiert keine bestimmte Ressource oder lösbare Reihenfolge sämtlicher ATM10-Quests.

## Selbst bauen

Java 21 installieren und `JAVA_HOME` setzen, anschließend unter Windows `gradlew.bat build`, unter Linux/macOS `./gradlew build` ausführen. Das Ergebnis liegt in `build/libs`. Abhängigkeiten sind im Build fest auf Versionsnummern gesetzt.

## Prüfungen

`gradlew.bat test` prüft negative Koordinaten, Körperbreite an Grenzen, offene gemeinsame Kanten, konkave Ecken, gerichtete Freischaltung und die Speicherung von Punkten/Quest-IDs/Dimensionen.

Zusätzliche Spielprüfung in ATM10:

1. Neue Welt: Startfeld 3×3, alle vier Gitterwände sichtbar, kein Durchlaufen möglich.
2. Checkmarkquest abschließen: exakt +1 Punkt und ein zusätzliches Item.
3. G öffnen, Richtung prüfen, kaufen: genau neun neue Blöcke; gemeinsame Grenze verschwindet.
4. Weitere Quest und seitliche Erweiterung: L-Form korrekt, geschlossene Ecke bleibt gesperrt.
5. Welt neu öffnen, sterben, Dimension wechseln: Kontostand und Felder bleiben erhalten.
6. Quest zurücksetzen/erneut abschließen: keine doppelte Zusatzbelohnung.
7. Volles Inventar, Sprinten, Flug, Enderperle, Reittier, Server mit zwei Spielern und Shader separat prüfen.

Ein erfolgreicher Build ersetzt keinen vollständigen ATM10-Spieltest. Den tatsächlichen Prüfstand enthält `TESTING.md`.
