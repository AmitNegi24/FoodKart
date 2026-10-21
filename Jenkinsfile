
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                sh 'java -version'
                sh 'mvn -version'
                sh 'docker version'
                sh 'docker compose version'
            }
        }

stage('Verify Deployment Credentials') {
    steps {
        withCredentials([
            file(credentialsId: 'foodkart-restaurant-env', variable: 'RESTAURANT_ENV'),
            file(credentialsId: 'foodkart-menu-service', variable: 'MENU_ENV'),
            file(credentialsId: 'foodkart-auth-env', variable: 'AUTH_ENV'),
            file(credentialsId: 'foodkart-order-env', variable: 'ORDER_ENV'),
            file(credentialsId: 'foodkart-payment-env', variable: 'PAYMENT_ENV'),
            file(credentialsId: 'foodkart-delivery-env', variable: 'DELIVERY_ENV')
        ]) {
            sh '''
                set +x

                for credential_file in \
                    "$RESTAURANT_ENV" \
                    "$MENU_ENV" \
                    "$AUTH_ENV" \
                    "$ORDER_ENV" \
                    "$PAYMENT_ENV" \
                    "$DELIVERY_ENV"
                do
                    if [ ! -s "$credential_file" ]; then
                        echo "A Jenkins credential file is missing or empty."
                        exit 1
                    fi
                done

                echo "All six Jenkins Secret files are accessible."
            '''
        }
    }
}

        stage('Test Microservices') {
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
                        dir(service) {
                            sh 'mvn -B clean verify'
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
            echo 'FoodKart CI completed successfully.'
        }
        failure {
            echo 'FoodKart CI failed. Check the console output.'
        }
    }
}
