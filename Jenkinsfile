pipeline {
    agent any
    tools {
        maven 'Maven 3'
        jdk 'JDK 21'
    }
    // trigger the default suite at 4 AM
    triggers {
        cron('0 4 * * *')
    }
    // define jenkins parameters
    parameters {
        string(name: 'GIT_BRANCH',
                defaultValue: 'develop',
                description: 'Enter the git branch to build.')
        choice(name: 'BROWSER',
                choices: ['chrome', 'firefox'],
                description: 'Specify the browser to run the tests.')
        string(name: 'DEFAULT_TIMEOUT',
                defaultValue: '20000',
                description: 'Set the default time out for tests (in milliseconds).')
        choice(name: 'PAGE_LOAD_STRATEGY',
                choices: ['eager', 'none', 'normal'],
                description: 'Set the page load strategy for the driver.')
        choice(name: 'ENVIRONMENT',
                choices: ['https://www.agoda.com', 'https://www.vietjetair.com/en', 'https://www.vietjetair.com/ko', 'https://www.vietjetair.com/vi'],
                description: 'Specify an environment to run tests.')
        choice(name: 'SUITE_NAME',
                choices: ['AgodaRegression', 'VJRegression'],
                description: 'Select the test suite to run.')
        choice(name: 'TEST_GROUP',
                choices: ['smoke', 'regression'],
                description: 'Select which test group to run.')
        choice(name: 'PARALLEL_MODE',
                choices: ['methods', 'classes', 'tests'],
                description: 'Parallel execution mode for tests.')
        choice(name: 'THREAD_COUNT',
                choices: [5, 1, 2, 3, 4],
                description: 'Number of threads to run tests concurrently.')
        choice(name: 'MAX_RETRY',
                choices: [2, 0, 1, 3],
                description: 'Maximum number of retries for failed tests.')
        choice(name: 'RETRY_STRATEGY',
                choices: ['immediate', 'post-suite'],
                description: 'Retry strategy for failed tests.')
        string(name: 'EMAIL_RECIPIENTS',
                defaultValue: 'yletheqatest.io@gmail.com',
                description: 'Comma-separated list of emails to notify after build.')
    }
    stages {
        stage('Initialize') {
            steps {
                script {
                    def selectedBranch = params.GIT_BRANCH
                    def suiteName = params.SUITE_NAME
                    currentBuild.displayName = "#${env.BUILD_NUMBER} - ${selectedBranch} - ${suiteName}"
                }
            }
        }
        stage('Checkout') {
            steps {
                script {
                    def inputBranch = params.GIT_BRANCH
                    git branch: inputBranch, url: 'git@github.com:yenleagest/demo-selenium.git'
                }
            }
        }
        stage('Build & Compile') {
            steps {
                echo 'Compiling the project...'
                sh """
                    [ -d "allure-results" ] && rm -rf allure-results
                    mvn -q clean compile
                """
            }
        }
        stage('Run Tests') {
            steps {
                script {
                    // read the previous testng-results.xml to capture the last <suite started-at="..."> timestamp
                    def previousStartTime = null
                    def suiteXml = 'target/surefire-reports/testng-results.xml'
                    if (fileExists(suiteXml)) {
                        def content = readFile(suiteXml)
                        def matcher = content =~ /<suite[^>]+started-at="([^"]+)"/
                        if (matcher.find()) {
                            previousStartTime = matcher.group(1)
                            echo "Previous testng-results.xml started-at detected: ${previousStartTime}"
                        }
                    }
                    // also read the previous test-summary.txt generatedAt timestamp
                    def previousRetryMetadataTime = null
                    def retryMetadata = 'target/test-summary.txt'
                    if (fileExists(retryMetadata)) {
                        def retryContent = readFile(retryMetadata)
                        def retryMatcher = retryContent =~ /generatedAt=(\d+)/
                        if (retryMatcher.find()) {
                            previousRetryMetadataTime = retryMatcher.group(1)
                            echo "Previous test-summary.txt generatedAt: ${previousRetryMetadataTime}"
                        }
                    }
                    sh """
                        mvn -q test \
                        -Dselenide.browser=${params.BROWSER} \
                        -Dselenide.browserSize=1920x1080 \
                        -Dselenide.headless=true \
                        -Dselenide.timeout=${params.DEFAULT_TIMEOUT} \
                        -Dselenide.pageLoadStrategy=${params.PAGE_LOAD_STRATEGY} \
                        -Dselenide.baseUrl=${params.ENVIRONMENT} \
                        -Dsurefire.suiteXmlFiles=src/test/resources/suites/${params.SUITE_NAME}.xml \
                        -Dgroups=${params.TEST_GROUP} \
                        -Dparallel=${params.PARALLEL_MODE} \
                        -DthreadCount=${params.THREAD_COUNT} \
                        -DmaxRetry=${params.MAX_RETRY} \
                        -DretryStrategy=${params.RETRY_STRATEGY}
                    """
                    // wait for a new testng-results.xml to be generated by comparing the updated <suite started-at="..."> timestamp
                    if (previousStartTime != null) {
                        timeout(time: 10, unit: 'SECONDS') {
                            waitUntil {
                                def refreshed = readFile(suiteXml)
                                def matcher = refreshed =~ /<suite[^>]+started-at="([^"]+)"/
                                if (matcher.find()) {
                                    def newTime = matcher.group(1)
                                    return newTime != previousStartTime
                                }
                                return false
                            }
                            echo "Detected updated testng-results.xml with new started-at timestamp."
                        }
                    }
                    if (previousRetryMetadataTime != null) {
                        timeout(time: 10, unit: 'SECONDS') {
                            waitUntil {
                                def refreshed = readFile(retryMetadata)
                                def matcher = refreshed =~ /generatedAt=(\d+)/
                                if (matcher.find()) {
                                    def newTime = matcher.group(1)
                                    return newTime != previousRetryMetadataTime
                                }
                                return false
                            }
                            echo "Detected updated test-summary.txt with new generatedAt timestamp."
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
            script {
                if (params.EMAIL_RECIPIENTS) {
                    // obtain the test results from the testng-results.xml and test-summary.txt for email notification
                    def failed = 0;
                    def passed = 0
                    def retried = 0
                    def skipped = 0

                    if (fileExists('target/test-summary.txt')) {
                        def props = readFile('target/test-summary.txt').readLines().collectEntries {
                            def (k, v) = it.split('=')
                            [(k): v]
                        }
                        failed = props.get('failed', '0').toInteger()
                        passed = props.get('passed', '0').toInteger()
                        retried = props.get('retried', '0').toInteger()
                        skipped = props.get('skipped', '0').toInteger()
                    } else if (fileExists('target/surefire-reports/testng-results.xml')) {
                        def content = readFile('target/surefire-reports/testng-results.xml')
                        def matcher = content =~ /<testng-results[^>]*retried="(\d+)"[^>]*passed="(\d+)"[^>]*failed="(\d+)"[^>]*skipped="(\d+)"/
                        if (matcher.find()) {
                            retried = matcher.group(1).toInteger()
                            passed = matcher.group(2).toInteger()
                            failed = matcher.group(3).toInteger()
                            skipped = matcher.group(4).toInteger()
                        } else {
                            matcher = content =~ /<testng-results[^>]*passed="(\d+)"[^>]*failed="(\d+)"[^>]*skipped="(\d+)"/
                            if (matcher.find()) {
                                passed = matcher.group(1).toInteger()
                                failed = matcher.group(2).toInteger()
                                skipped = matcher.group(3).toInteger()
                            }
                        }
                    } else {
                        echo "⚠️ No test results found in testng-results.xml or test-summary.txt."
                        exit 1
                    }
                    def total = passed + failed + retried
                    // generate html report to attach to the email
                    sh """
                        allure generate --clean --single-file allure-results -o allure-report
                        mv allure-report/index.html allure-report/report.html
                    """
                    emailext(subject: "Build Notifications - #${env.BUILD_NUMBER} - ${params.SUITE_NAME} - ${currentBuild.currentResult}",
                            body: """
                        <html>
                        <head></head>
                        <body style="font-family: Arial, sans-serif;">
                            <h3>🧪 <b style="color:${currentBuild.currentResult == 'SUCCESS' ? 'green' : 'red'}">${currentBuild.currentResult}</b> Report Summary</h3>
                            <ul>
                                <li><b>Project:</b> Selenium3</li>
                                <li><b>Branch:</b> ${params.GIT_BRANCH}</li>
                                <li><b>Browser:</b> ${params.BROWSER}</li>
                                <li><b>Default Timeout:</b> ${params.DEFAULT_TIMEOUT}</li>
                                <li><b>Environment:</b> ${params.ENVIRONMENT}</li>
                                <li><b>Suite:</b> ${params.SUITE_NAME}</li>
                                <li><b>Test Group:</b> ${params.TEST_GROUP}</li>
                                <li><b>Parallel Mode:</b> ${params.PARALLEL_MODE}</li>
                                <li><b>Thread Count:</b> ${params.THREAD_COUNT}</li>
                                <li><b>Max Retry:</b> ${params.MAX_RETRY}</li>
                                <li><b>Retry Strategy:</b> ${params.RETRY_STRATEGY}</li>
                                <li><b>Triggered By:</b> ${currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')[0]?.userName ?: 'Auto Trigger or SCM'}</li>
                                <li><b>Build Duration:</b> ${currentBuild.durationString.replace(' and counting', '')}</li>
                                <li><b>Start Time:</b> ${new Date(currentBuild.getStartTimeInMillis()).format("yyyy-MM-dd hh:mm:ss a z", TimeZone.getTimeZone('ICT'))}</li>
                                <li><b>End Time:</b> ${new Date().format("yyyy-MM-dd hh:mm:ss a z", TimeZone.getTimeZone('ICT'))}</li>
                            </ul>

                            <p><b>🎲 Test Status Summary:</b></p>
                            <table border="2" cellpadding="7" cellspacing="0" style="border-collapse: collapse; width: 40%; text-align: left;">
                                <thead style="background-color: #d3eedf;">
                                    <tr>
                                        <th style="width: 30%;">Status</th>
                                        <th style="width: 70%;">Count</th>
                                    </tr>
                                </thead>
                                 <tbody style="background-color: #f6fbf9;">
                                    <tr><td><b>Total</b></td><td>${total}</td></tr>
                                    <tr><td><b>Passed</b></td><td>${passed}</td></tr>
                                    <tr><td><b>Failed</b></td><td>${failed}</td></tr>
                                    <tr><td><b>Retried</b></td><td>${retried}</td></tr>
                                    <tr><td><b>Skipped</b></td><td>${skipped}</td></tr>
                                </tbody>
                            </table>

                            <p style="margin-top: 20px;">📦 <a href="${env.BUILD_URL}">View Jenkins Build</a></p>
                            <p><b>⛑️ Note:</b> If you can’t access the Jenkins build, please download the attached <code>report.html</code> file, and open it in your browser to view the test report.</p>
                        </body>
                        </html>
                        """,
                            mimeType: 'text/html',
                            to: "${params.EMAIL_RECIPIENTS}",
                            attachmentsPattern: 'allure-report/report.html',)
                } else {
                    echo "No recipients specified. Skipping email notification."
                }
            }
        }
    }
}
