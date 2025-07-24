# 🎈 Selenium-Level-3 Automation Framework

This is a **Selenium-based automation framework** designed to support **parallel execution**, **dynamic URL handling**,
**Selenium Grid integration**, and **Allure reporting**. It provides a scalable and efficient solution for running
automated UI tests.

---

## 🗳️ Project Progress

### Outcomes

- [x] Selenide FW ([selenide.org](https://selenide.org/)): automation/test framework
- [x] Reports: HTML, Allure Report, Report Portal
- [x] Test retry: test failed ⇒ retry (1, 2)
- [x] Parallel/distributed testing
- [x] Cross browsers testing: Chrome, Edge
- [x] Selenium Grid/Shard
- [ ] Test cases: VJ, Agoda, TBD (7/8 - 3 Agoda tests, a Shadow DOM test, 2 VJ tests, and a content testing test)
- [x] CI: Schedule test, send email notification result with summary

### User Cases

- [x] Content testing
- [x] Multiple languages testing
- [x] Group tests by purposes: regression, smoke/sanity test
- [x] Source control practice: branch
- [x] Switch test environment: dev, stg (dev: agoda.com, stg: vj.com)
- [x] Wrap custom controls
- [ ] Data driven testing: test data is in excel file
- [x] Working with Shadow DOM
- [ ] Compare with another FW e.g. Playwright

---

## 🧶 Key Features

- **Selenium with Selenide** – Simplified and fluent API for UI automation.
- **Allure Reporting** – Beautiful test execution reports.
- **Retry On Failure** - Automatically retries failed tests immediately or after the suite completes.
- **Selenium Grid Support** – Run tests efficiently using Selenium Standalone.
- **CI Integration** – Easily integrates with Jenkins pipelines.

---

## 📦 Project Structure

```
.
├── .gitignore                         # Specifies untracked files to ignore in Git version control
├── Jenkinsfile                        # Jenkins CI/CD pipeline definition for automated build and test workflows
├── pom.xml                            # Maven build file that manages project dependencies and plugins
├── README.md                          # Project documentation with setup, run instructions and project overview
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── common                 # Common constants used across the project
│   │   │   ├── data                   # Enums and model classes for handling structured UI data shared across pages
│   │   │   ├── drivers                # WebDriver/Selenide utilities to initialize, manage, and quit the browser
│   │   │   ├── pages                  # Page Object Model (POM) classes representing UI elements and actions per page
│   │   │   ├── reports                # Integration classes for test reporting (e.g., Allure reports)
│   │   │   ├── testdata               # Data provider classes to supply test data parsed from YAML files
│   │   │   └── utils                  # Helper utilities like YAML file parsers
│   │   └── resources
│   │       └── log4j2.xml             # Configuration file for log4j2 logging system
│   └── test
│       ├── java
│       │   ├── listeners              # Custom TestNG listeners for hooks like onTestStart/onTestFailure for capture screenshot or retry
│       │   ├── retriers               # Retry analyzer logic to re-run failed tests based on configured rules
│       │   └── testcases              # End-to-end test classes, grouped by environment (e.g., Agoda, VJ)
│       └── resources
│           ├── suites                 # TestNG suite XML files organized by environment (e.g., AgodaRegression.xml)
│           ├── testdata               # YAML files containing test case-specific data grouped by method
│           └── selenide.properties    # Selenide-specific configuration for browser behavior (e.g., size, timeout)
```

---

## 🏗️ Project Setup

### 1️⃣ Install Java 21

Ensure Java 21 is installed.

#### Mac/Linux

```sh
brew install openjdk@21
```

Then, set JAVA_HOME:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

Add this to ~/.zshrc or ~/.bashrc then run `source ~/.zshrc` or `source ~/.bashrc` to reload the config.

#### Windows

1. Download Java 21 from [Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
2. Set JAVA_HOME in system environment variables:
   • Go to Control Panel → System → Advanced system settings → Environment Variables.
   • Add a new system variable:
   • Variable name: JAVA_HOME
   • Variable value: Path to your JDK installation (e.g., C:\Program Files\Java\jdk-21).
   • Add %JAVA_HOME%\bin to the Path variable.

#### Check your Java version:

```sh
java -version
```

### 2️⃣ Install Maven

#### Mac/Linux

```sh
brew install maven
```

#### Windows

1. Download from [Apache Maven](https://maven.apache.org/download.cgi).
2. Extract and set MAVEN_HOME in environment variables.
3. Add `%MAVEN_HOME%\bin` to the system Path. For more details, please refer
   to [Maven Installation](https://www.qamadness.com/knowledge-base/how-to-install-maven-and-configure-environment-variables/).

#### Verify installation:

```sh
mvn -version
```

### 3️⃣ Set Up Selenium Grid (Optional)

To run tests on Selenium Grid,
download [latest Selenium Server](https://github.com/SeleniumHQ/selenium/releases/download/selenium-4.29.0/selenium-server-4.29.0.jar)
and place it in ~/selenium-server by opening a terminal and executing the following commands:

```sh
# macOS/linux
mkdir -p ~/selenium-server
cd ~/selenium-server

# windows
mkdir %USERPROFILE%\selenium-server
cd %USERPROFILE%\selenium-server
```

##### Run Selenium Grid:

```sh
java -jar selenium-server-4.29.0.jar standalone \
--max-sessions 5 \
--driver-implementation "chrome" --driver-implementation "edge" \
--selenium-manager true
```
- `--max-sessions`: Sets the maximum number of concurrent WebDriver sessions the Grid can run. By default, this equals the number of available processor cores. To exceed that limit, add the `--override-max-sessions true` flag. Be cautious — increasing this value may impact session stability and resource availability on the host machine.
- `--driver-implementation`: Skip autoconfiguration by explicitly specifying which browser drivers (e.g., chrome, edge, firefox) to enable and display on the Grid UI. You can add multiple drivers by repeating the flag.
- `--selenium-manager`: Enables Selenium Manager to automatically download and configure browser drivers if they’re not already present on your system.

##### Stop Selenium Grid:

```sh
Ctrl + C
pkill -f selenium-server # macOS/linux
taskkill /F /FI "WINDOWTITLE eq selenium-server*" # windows
```

### 4️⃣ Install Allure for Test Reporting

#### Mac/Linux

```sh
brew install allure
```

#### Windows

- Follow the instruction in [Allure Official](https://allurereport.org/docs/install-for-windows/) site to install.

#### Verify installation:

```sh
allure --version
```

---

## 🏎️ Running Tests

### 1️⃣ Install Dependencies

- Get the project file then open your terminal from the project root folder and execute.

```sh
mvn clean install -DskipTests
```

### 2️⃣ Run Tests

```sh
mvn clean test \
  -Dselenide.browser=chrome \
  -Dselenide.headless=true \
  -Dselenide.timeout=20000 \
  -Dselenide.pageLoadStrategy=normal \
  -Dselenide.remote=http://localhost:4444 \ # only use this if you are running tests on Selenium Grid
  -Dselenide.baseUrl=https://www.agoda.com \
  -Dsurefire.suiteXmlFiles=src/test/resources/suites/AgodaRegression.xml \
  -Dgroups=smoke \
  -Dparallel=methods \
  -DthreadCount=5 \
  -DmaxRetry=3 \
  -DretryStrategy=post-suite
  ```

| Parameter                     | Description                                                                  |
|-------------------------------|------------------------------------------------------------------------------|
| `-Dselenide.browser`          | Specifies the browser to use (`chrome`, `firefox`, `edge`, `safari`).        |
| `-Dselenide.headless`         | Enables headless mode (`true` or `false`) for browser execution.             |
| `-Dselenide.timeout`          | Sets the default timeout (in milliseconds) for element waits.                |
| `-Dselenide.pageLoadStrategy` | Controls how the browser waits for page loading (`normal`, `eager`, `none`). |
| `-Dselenide.remote`           | URL of the remote Selenium Grid server (only needed for remote execution).   |
| `-Dselenide.baseUrl`          | Base URL of the application under test.                                      |
| `-Dsurefire.suiteXmlFiles`    | Path to the TestNG XML suite file to execute.                                |
| `-Dgroups`                    | Specifies which test group(s) to run (e.g., `smoke`, `regression`).          |
| `-Dparallel`                  | Specifies parallel execution mode (`classes`, `methods`, or `tests`).        |
| `-DthreadCount`               | Number of threads to use when running tests in parallel.                     |
| `-DmaxRetry`                  | Maximum number of retry attempts for failed tests.                           |
| `-DretryStrategy`             | Retry strategy to apply (`immediate` or `post-suite`).                       |

### 3️⃣ View Allure Report

```sh   
allure serve allure-results
```
