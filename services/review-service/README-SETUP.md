# Review Service — Local Setup (Windows PowerShell)

This file collects the exact steps to resolve the "Cannot resolve symbol 'springframework' / 'Page' / 'Pageable'" errors in your IDE by ensuring Maven and JDK are installed and the project is imported correctly.

## 1) Quick verification — do you have Maven on PATH?
Open PowerShell and run:

```powershell
mvn -v
```

If you see "The term 'mvn' is not recognized...", follow the installation steps below.

---

## 2) Install Maven on Windows using Chocolatey (recommended)
Run PowerShell as Administrator and execute the following commands.

1) Install Chocolatey (if not already installed):

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force;
[System.Net.ServicePointManager]::SecurityProtocol = [System.Net.SecurityProtocolType]::Tls12;
iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
```

2) Install Maven:

```powershell
choco install maven -y
```

3) Close and reopen PowerShell (or your IDE) and verify:

```powershell
mvn -v
```

You should see the Maven version and Java home information.

---

## 3) Alternative: Use IntelliJ's Maven import (no mvn on PATH required)
- Open the project in IntelliJ.
- Open the `pom.xml` file. Look for a popup that says "Import changes" and click it.
- Or open the Maven tool window (View → Tool Windows → Maven), then click the refresh/reload button (Reload All Maven Projects).
- IntelliJ will download dependencies for you. If dependency download fails, ensure IDE has network/proxy access.

---

## 4) Ensure JDK 17 is configured in IntelliJ
- File → Project Structure → Project → Project SDK: set to Java 17.
- File → Settings → Build, Execution, Deployment → Compiler → Java Compiler: set Target bytecode to 17 (if needed).

---

## 5) Enable Lombok annotation processing in IntelliJ
- File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors → check "Enable annotation processing".
- Install the Lombok plugin in IntelliJ (Settings → Plugins → Marketplace → search "Lombok" → Install), then restart the IDE.

---

## 6) Build the project locally (once mvn is available)
From PowerShell in the project root run:

```powershell
cd 'C:\Users\Kavindu\Documents\shopsphere\ShopSphere\services\review-service'
# Download dependencies and compile (skip tests for speed):
mvn -DskipTests package
```

If the build succeeds, your IDE should be able to resolve Spring symbols.

---

## 7) Optional: Update PSReadLine to avoid PowerShell terminal UI glitch
You may see a message about PSReadLine being outdated. To update (in a non-elevated PowerShell):

```powershell
Install-Module PSReadLine -MinimumVersion 2.0.3 -Scope CurrentUser -Force
```

Then restart the terminal.

---

## 8) If you'd like: add Maven Wrapper (mvnw)
- The Maven Wrapper allows anyone to build the project without installing Maven system-wide.
- Generating the wrapper requires `mvn` at least once: `mvn -N io.takari:maven:wrapper` or `mvn -N -DskipTests org.apache.maven.plugins:maven-wrapper-plugin:wrapper`.
- If you prefer, after you install Maven I can add the wrapper for you (or I can provide the exact command to run locally).

---

## 9) Troubleshooting
- If IntelliJ still shows unresolved symbols after a successful `mvn package`:
  - In IntelliJ choose: File → Invalidate Caches / Restart → Invalidate and Restart.
  - Re-import the Maven project (Maven tool window → Reload All Maven Projects).
  - Confirm Project SDK is 17 and Annotation Processing is enabled.

---

If you want, I can:
- Add `README-SETUP.md` (this file) to the repo (done).
- After you install Maven, run a build and tell me the output and I can continue implementing the next tasks (2.3 and beyond).

Tell me which option you want: (A) I should attempt to add a Maven Wrapper later after you install Maven, or (B) you want me to continue implementing code that doesn't require building right now. If you prefer, run the `mvn -v` command and paste the output here and I'll proceed accordingly.

