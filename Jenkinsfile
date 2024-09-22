pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "natural-afk/todo-app:${env.BUILD_NUMBER}"
        SONARQUBE = "SonarQube"  // Ensure SonarQube is configured in Jenkins
        GIT_REPO_URL = 'https://github.com/Natural-afk/DevOpsPipelineProject.git'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from GitHub
                git credentialsId: 'github-pat', url: "${GIT_REPO_URL}", branch: 'main'
            }
        }

        stage('Install Dependencies') {
            steps {
                // Install npm dependencies
                sh 'npm install'
            }
        }

        stage('Build') {
            steps {
                // Build the application
                sh 'npm run build'
                // Build Docker image
                sh "docker build -t ${DOCKER_IMAGE} ."
            }
        }

        stage('Test') {
            steps {
                // Run automated tests
                sh 'npm test'
            }
        }

        stage('Code Quality Analysis') {
            steps {
                // Run SonarQube analysis
                withSonarQubeEnv('SonarQube') {
                    sh 'sonar-scanner'
                }
            }
        }

        stage('Quality Gate') {
            steps {
                // Wait for the SonarQube Quality Gate to be evaluated
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Deploy to Staging') {
            steps {
                // Deploy to staging environment using Docker Compose
                sh 'docker-compose -f docker-compose.staging.yml up -d'
            }
        }

        stage('Release to Production') {
            steps {
                // Manual approval before deploying to production
                input "Approve Deployment to Production?"

                // Deploy to production environment using Docker Compose
                sh 'docker-compose -f docker-compose.prod.yml up -d'
            }
        }

        stage('Monitoring') {
            steps {
                // Placeholder for monitoring steps (integrate monitoring tools here)
                echo 'Monitoring is set up separately via Datadog.'
            }
        }
    }

    post {
        always {
            // Clean up Docker images to save space
            sh 'docker system prune -f'
        }
        failure {
            // Notify team of build failure
            echo 'Build failed!'
        }
    }
}

