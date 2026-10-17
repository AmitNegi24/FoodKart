pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'FoodKart source code checked out successfully!'
            }
        }

        stage('Verify Environment') {
            steps {
                sh 'java -version'
                sh 'git --version'
            }
        }
    }

    post {
        success {
            echo 'FoodKart CI pipeline completed successfully!'
        }
        failure {
            echo 'FoodKart CI pipeline failed!'
        }
    }
}
