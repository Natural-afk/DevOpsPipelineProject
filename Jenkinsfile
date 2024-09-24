pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6'
        jdk 'JDK 17'
    }

    environment {
        DEPLOY_DIR = 'C:\\ProgramData\\Jenkins\\my-app\\deployment'
        TRIVY_PATH = 'C:\\Program Files\\Trivy\\trivy.exe'
        OCTO_CLI = 'C:\\ProgramData\\chocolatey\\lib\\OctopusTools\\tools\\octo.exe'
        BUILD_VERSION = "${env.BUILD_NUMBER ?: '1.0.0'}"  // Use Jenkins build number for release versioning
        TIMESTAMP = new Date().format("yyyyMMddHHmmss")  // Add timestamp to ensure unique version
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                git branch: 'main',
                    url: 'https://github.com/Natural-afk/DevOpsPipelineProject.git',
                    credentialsId: 'NewGitHub'
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application...'
                dir('C:\\ProgramData\\Jenkins\\my-app') {
                    bat 'mvn clean package'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests...'
                dir('C:\\ProgramData\\Jenkins\\my-app') {
                    bat 'mvn test'
                    junit 'target\\surefire-reports\\*.xml'
                }
            }
        }

        stage('Code Coverage') {
            steps {
                echo 'Publishing code coverage report...'
                dir('C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\6.2HD V1\\MyJavaApp\\target\\site\\jacoco') {
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'C:\\ProgramData\\Jenkins\\.jenkins\\workspace\\6.2HD V1\\MyJavaApp\\target\\site\\jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Code Coverage Report'
                    ])
                }
            }
        }

        stage('Trivy Vulnerability Scan') {
            steps {
                echo 'Running Trivy Vulnerability Scan...'
                dir('C:\\ProgramData\\Jenkins\\my-app') {
                    bat "\"${env.TRIVY_PATH}\" fs --exit-code 1 --severity HIGH,CRITICAL --format json --output trivy-report.json ."
                    archiveArtifacts artifacts: 'trivy-report.json', allowEmptyArchive: true

                    script {
                        def trivyReport = readJSON file: 'trivy-report.json'
                        def hasVulnerabilities = false

                        trivyReport.Results.each { result ->
                            if (result.Vulnerabilities && result.Vulnerabilities.size() > 0) {
                                hasVulnerabilities = true
                            }
                        }

                        if (hasVulnerabilities) {
                            error "Trivy found vulnerabilities with HIGH or CRITICAL severity."
                        } else {
                            echo "No HIGH or CRITICAL vulnerabilities found by Trivy."
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                echo 'Performing SonarQube analysis...'
                dir('C:\\ProgramData\\Jenkins\\my-app') {
                    withSonarQubeEnv('MySonarQubeServer') {
                        bat 'mvn sonar:sonar'
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                echo 'Waiting for SonarQube quality gate result...'
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application locally...'

                bat """
                if not exist "${env.DEPLOY_DIR}" (
                    mkdir "${env.DEPLOY_DIR}"
                )
                """

                bat """
                copy "C:\\ProgramData\\Jenkins\\my-app\\target\\my-app-1.0-SNAPSHOT.jar" "${env.DEPLOY_DIR}\\my-app.jar" /Y
                """
            }
        }

        stage('Release to Octopus') {
            steps {
                script {
                    echo 'Releasing to production using Octopus Deploy...'
                    bat """
                    ${env.OCTO_CLI} create-release --project MyWebApp --releaseNumber 1.0.${BUILD_VERSION}-${TIMESTAMP} --deployTo Production --server http://localhost:8082 --apiKey API-GYVWJTOVGSV7SLJE6BYFZVM8XKPLDDCN
                    """
                }
            }
        }

        stage('Datadog Monitoring') {
            steps {
                script {
                    echo 'Checking Datadog agent status...'
                    bat '"C:\\Program Files\\Datadog\\Datadog Agent\\bin\\agent.exe" status'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            mail to: 'cold2thev@gmail.com',
                 subject: "Build Successful: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "The build was successful."
        }
        failure {
            echo 'Pipeline failed.'
            mail to: 'cold2thev@gmail.com',
                 subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "The build failed. Please check the Jenkins console output."
        }
    }
}
