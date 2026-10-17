
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
                sh 'mvn -version'
                sh 'git --version'
            }
        }

        stage('Build Microservices') {
            steps {
                script {
                    def services = [
                        'restaurant-service',
                        'menu-service',
                        'auth-service',
                        'order-service',
                        'payment-service',
                        'delivery-service',
                        'api-gateway'
                    ]

                    for (service in services) {
                        echo "Building ${service}..."

                        dir(service) {
                            sh 'mvn -B clean package -DskipTests'
                        }
                    }
                }
            }
        }

        stage('Test Restaurant Service') {
            steps {
                dir('restaurant-service') {
                    sh 'mvn -B test'
                }
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: '**/target/surefire-reports/*.xml'
        }

        success {
            echo 'FoodKart CI pipeline completed successfully!'
        }

        failure {
            echo 'FoodKart CI pipeline failed. Check the console output.'
        }
    }
}
