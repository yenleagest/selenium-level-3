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
        string(
            name: 'GIT_BRANCH',
            defaultValue: 'develop',
            description: 'Enter the git branch to build.'
        )
        choice(
            name: 'SUITE_NAME',
            choices: ['AgodaRegression', 'AgodaSmoke', 'VJRegression', 'VJSmoke'],
            description: 'Select the test suite to run.'
        )
        choice(
            name: 'BROWSER',
            choices: ['Chrome', 'FireFox'],
            description: 'Specify the browser to run the tests.'
        )
        choice(
            name: 'PARALLEL',
            choices: ['methods', 'classes', 'tests'],
            description: 'Parallel execution mode for tests.'
        )

        choice(
            name: 'THREAD_COUNT',
            choices: [5, 1, 2, 3, 4],
            description: 'Number of threads to run tests concurrently.'
        )
        choice(
            name: 'MAX_RETRY',
            choices: [2, 0, 1, 3],
            description: 'Maximum number of retries for failed tests.'
        )
        choice(
            name: 'RETRY_STRATEGY',
            choices: ['Immediate', 'PostSuite'],
            description: 'Retry strategy for failed tests.'
        )
        string(
            name: 'EMAIL_RECIPIENTS',
            defaultValue: 'yletheqatest.io@gmail.com',
            description: 'Comma-separated list of emails to notify after build.'
        )
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

        stage('Update Configure') {
            steps {
                script {
                    echo 'Loading configurations'
                    def configFile = "config.properties"
                    def props = readProperties file: configFile

                    props['browser'] = params.BROWSER
                    props['threadCount'] = params.THREAD_COUNT
                    props['maxRetry'] = params.MAX_RETRY
                    props['retryStrategy'] = params.RETRY_STRATEGY
                    props['headless'] = "true"
                    if (params.SUITE_NAME.contains('Agoda')) {
                        props['environment'] = "agoda"
                    } else if (params.SUITE_NAME.contains('VJ')) {
                        props['environment'] = "vj"
                    }

                    def updatedContent = props.collect { k, v -> "${k}=${v}" }.join('\n')
                    writeFile(file: configFile, text: updatedContent)
                }
            }
        }

        stage('Build & Compile') {
            steps {
                echo 'Compiling the project...'
                sh '''
                    [ -d "allure-results" ] && rm -rf allure-results
                    mvn -q clean compile'''
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh "mvn -q test -Dbrowser=${params.BROWSER} -Dparallel=${params.PARALLEL} -Dthread-count=${params.THREAD_COUNT} -Dsurefire.suiteXmlFiles=src/test/resources/suites/${params.SUITE_NAME}.xml"
                }
            }
        }
    }

    post {
        always {
            allure includeProperties: false, jdk: '', results: [[path: 'allure-results']]
            script {
                if (params.EMAIL_RECIPIENTS) {
                    emailext(
                        subject: "Build Notifications <jenkins-yenle-sel3.org> #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                        body: "The suite ${params.SUITE_NAME} finished with status: ${currentBuild.currentResult}\n\nCheck it here: ${env.BUILD_URL}",
                        to: "${params.EMAIL_RECIPIENTS}"
                    )
                } else {
                    echo "No recipients specified. Skipping email notification."
                }
            }
        }
    }
}
