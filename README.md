# 🎈 Selenium-Level-3 Automation Framework

This is a **Selenium-based automation framework** designed to support **parallel execution**, **dynamic URL handling**, **Selenium Grid integration**, and **Allure reporting**. It provides a scalable and efficient solution for running automated UI tests.

---

## 🍺 Key Features
- **Selenium with Selenide** – Simplified and fluent API for UI automation.
- **Allure Reporting** – Beautiful test execution reports.
- **Retry On Failure** - Automatically retries failed tests immediately or after the suite completes.
- **Selenium Grid Support** – Run tests efficiently using Selenium Standalone.
- **CI Integration** – Easily integrates with Jenkins pipelines.

---

## 📌 Requirements
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
1.	Download Java 21 from [Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html).
2.	Set JAVA_HOME in system environment variables:
•	Go to Control Panel → System → Advanced system settings → Environment Variables.
•	Add a new system variable:
•	Variable name: JAVA_HOME
•	Variable value: Path to your JDK installation (e.g., C:\Program Files\Java\jdk-21).
•	Add %JAVA_HOME%\bin to the Path variable.

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
1.	Download from [Apache Maven](https://maven.apache.org/download.cgi).
2.	Extract and set MAVEN_HOME in environment variables.
3.	Add `%MAVEN_HOME%\bin` to the system Path. For more details, please refer to [Maven Installation](https://www.qamadness.com/knowledge-base/how-to-install-maven-and-configure-environment-variables/).


#### Verify installation:
```sh
mvn -version
```


### 3️⃣ Set Up Selenium Grid (Optional)
To run tests on Selenium Grid, download [latest Selenium Server](https://github.com/SeleniumHQ/selenium/releases/download/selenium-4.29.0/selenium-server-4.29.0.jar) and place it in ~/selenium-server by opening a terminal and executing the following commands:

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
java -jar <jar_file_name> standalone, for e.g.
java -jar selenium-server-4.29.0.jar standalone
```

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


## 🏎️ Running Tests

### 1️⃣ Install Dependencies
- Get the project file then open your terminal from the project root folder and execute.
```sh
mvn clean install -DskipTests
```

### 2️⃣ Run Tests
```sh
mvn test \
  -Dbrowser=chrome \
  -Dheadless=true \
  -Dtimeout=20000 \
  -Denvironment=agoda \
  -Dsurefire.suiteXmlFiles=src/test/resources/suites/AgodaSmoke.xml \
  -Dgroups=smoke \
  -Dparallel=methods \
  -Dthread-count=5 \
  -DmaxRetry=3 \
  -DretryStrategy=post-suite
  ```
| Parameter                     | Description                                                                |
|-------------------------------|----------------------------------------------------------------------------|
| `-Dbrowser`                   | Specifies the browser to use (`chrome`, `firefox`, or `safari`).           |
| `-Dheadless`                  | Enables headless mode (`true` or `false`) for browser execution.           |
| `-Dtimeout`                   | Sets the default timeout (in milliseconds) for element waits.              |
| `-Denvironment`               | Defines the target environment for testing (e.g., `agoda`, `vj`).          |
| `-Dsurefire.suiteXmlFiles`    | Path to the TestNG XML suite file to execute.                              |
| `-Dgroups`                    | Specifies which test group(s) to run (e.g., `smoke`, `regression`).        |
| `-Dparallel`                  | Specifies parallel execution mode (`classes`, `methods`, or `tests`).      |
| `-Dthread-count`              | Number of threads to use when running tests in parallel.                   |
| `-DmaxRetry`                  | Maximum number of retry attempts for failed tests.                         |
| `-DretryStrategy`             | Retry strategy to apply (`immediate` or `post-suite`).                     |

## 🎈 View Allure Report
```sh   
allure serve allure-results
```

