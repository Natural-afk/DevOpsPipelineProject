pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6' 
        jdk 'JDK 17'        
    }

    environment {
        DEPLOY_DIR = 'C:\\ProgramData\\Jenkins\\my-app\\deployment'
        TRIVY_PATH = '"C:\\Program Files\\Trivy\\trivy.exe"' 
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
                dir('C:\\ProgramData\\Jenkins\\my-app') {
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
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
                    bat "${env.TRIVY_PATH} fs --exit-code 1 --severity HIGH,CRITICAL --format json --output trivy-report.json ."

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
                    withSonarQubeEnv('MySonarQubeServer') 
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
            when {
                branch 'main' 
            }
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


                // Uncomment the following lines to run the application
                /*
                bat """
                start "MyApp" java -jar "${env.DEPLOY_DIR}\\my-app.jar"
                """
                */
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
            // Send success notification
            mail to: 'cold2thev@gmail.com',
                 subject: "Build Successful: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "The build was successful."
        }
        failure {
            echo 'Pipeline failed.'
            // Send failure notification
            mail to: 'cold2thev@gmail.com',
                 subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: "The build failed. Please check the Jenkins console output."
        }
    }
}
