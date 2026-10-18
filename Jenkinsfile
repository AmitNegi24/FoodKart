
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
                sh 'docker version'
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

        stage('Build Docker Images') {
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
                        echo "Building Docker image for ${service}..."

                        dir(service) {
                            sh "docker build -t foodkart-${service}:latest ."
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'FoodKart CI pipeline completed successfully!'
        }

        failure {
            echo 'FoodKart CI pipeline failed. Check the console output.'
        }
    }
}
