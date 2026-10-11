# Prüfstand – 11. Oktober 2026

## Erfolgreich

- Gradle 9.3.0 / Java 21: `build wrapper --gradle-version 9.3.0`, BUILD SUCCESSFUL.
- Kompiliert gegen Minecraft 1.21.1, NeoForge 21.1.219, FTB Quests 2101.1.36, FTB Library 2101.1.37, FTB Teams 2101.1.11 und Architectury 13.0.11.
- Vier JUnit-Tests: negative Koordinaten; Körperbreite und gemeinsame/konkave Kanten; erste geschlossene Zelle je Richtung; NBT-Rundlauf mit Punkten, eindeutigen Quest-IDs und Dimensionsdaten.
- Separate dedizierte Serverinstanz mit NeoForge 21.1.259 und den obigen FTB-/Architectury-Versionen: Gridbound 1.0.0 geladen, Welt erfolgreich gestartet (`Done`), sauber gestoppt, Exit-Code 0. Keine ERROR-Meldungen im Smoke-Log.
- Clientcode wird mitkompiliert. Die Mod lädt auf einem dedizierten Server ohne Zugriff auf Clientklassen beim Start.

## Noch nicht spielerisch geprüft

- Vollständige ATM10-8.2-Instanz mit allen Mods.
- Darstellung des Gitters, Menübedienung und Tastenkonflikte im laufenden Client.
- Tatsächlicher Questabschluss mit Spieler, Zufallsitemübergabe, Tod, Portale, Reittiere, modifizierte Bewegung und Multiplayer.
- Shader-/Rendering-Mod-Kombinationen und Performance in großen Gebieten.

Der Serverstart prüft die Laufzeit-Verknüpfung der Mod, ersetzt aber keinen Survival-Durchlauf. Die vier Tests prüfen die echte Raster- und Speicherimplementierung ohne grafischen Client.

## Dateien

- `build-log.txt`: letzter erfolgreicher Build.
- `build/test-results/test/`: JUnit-XML.
- `build/reports/tests/test/index.html`: Testbericht.
- `build/smoke-server/smoke.log`: Serverstart und Stopp.
- `smoke_server.py`: reproduzierbarer lokaler Smoke-Test mit dem vorhandenen benachbarten NeoForge-Testserver als Bibliotheksquelle.

Die verwaltete Windows-Testumgebung benötigt den JVM-Parameter `-Djdk.net.unixdomain.tmpdir=C:/nonexistent-gridbound-sockets`, damit Java für interne Pipes auf TCP zurückfällt. Er gehört nicht in die Mod-JAR und ist normalerweise nicht für Spieler erforderlich.
