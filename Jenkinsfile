
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        ENABLE_CD = 'false'
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
                sh 'curl --version'
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
                        set -eu

                        for credential_file in \
                            "$RESTAURANT_ENV" \
                            "$MENU_ENV" \
                            "$AUTH_ENV" \
                            "$ORDER_ENV" \
                            "$PAYMENT_ENV" \
                            "$DELIVERY_ENV"
                        do
                            if [ ! -s "$credential_file" ]; then
                                echo "ERROR: A Jenkins credential file is missing or empty."
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

        stage('Validate Deployment Configuration') {
            steps {
                withCredentials([
                    file(credentialsId: 'foodkart-restaurant-env', variable: 'RESTAURANT_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-menu-service', variable: 'MENU_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-auth-env', variable: 'AUTH_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-order-env', variable: 'ORDER_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-payment-env', variable: 'PAYMENT_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-delivery-env', variable: 'DELIVERY_SERVICE_ENV_FILE')
                ]) {
                    sh '''
                        set +x
                        set -eu

                        docker compose -f compose.yaml config --quiet

                        echo "Docker Compose configuration is valid."
                    '''
                }
            }
        }

        stage('Authorize Deployment') {
            when {
                expression {
                    return env.ENABLE_CD == 'true'
                }
            }

            steps {
                sh '''
                    set -eu

                    CURRENT_COMMIT=$(git rev-parse HEAD)

                    REMOTE_MASTER=$(git ls-remote origin refs/heads/master |
                        awk 'NR == 1 {print $1}')

                    if [ -z "$REMOTE_MASTER" ]; then
                        echo "ERROR: Could not retrieve remote master commit."
                        exit 1
                    fi

                    if [ "$CURRENT_COMMIT" != "$REMOTE_MASTER" ]; then
                        echo "ERROR: Checkout does not match current origin/master."
                        exit 1
                    fi

                    echo "Deployment authorized for origin/master."
                '''
            }
        }

        stage('Backup Running Docker Images') {
            when {
                expression {
                    return env.ENABLE_CD == 'true'
                }
            }

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

                    sh 'docker network inspect foodkart-network >/dev/null'

                    // Capture the image ID from each running container
                    // before any new Docker images are built.
                    for (service in services) {
                        sh """
                            set -eu

                            CONTAINER="foodkart-${service}"
                            IMAGE="foodkart-${service}"

                            RUNNING=\$(docker inspect \
                                --format '{{.State.Running}}' \
                                "\$CONTAINER")

                            if [ "\$RUNNING" != "true" ]; then
                                echo "ERROR: \$CONTAINER is not running."
                                exit 1
                            fi

                            IMAGE_ID=\$(docker inspect \
                                --format '{{.Image}}' \
                                "\$CONTAINER")

                            docker tag "\$IMAGE_ID" \
                                "\$IMAGE:rollback-${env.BUILD_NUMBER}"

                            echo "Preserved running image for ${service}."
                        """
                    }

                    // Publish the stable rollback tags only after every
                    // service's versioned backup has been captured.
                    for (service in services) {
                        sh """
                            set -eu

                            docker tag \
                                foodkart-${service}:rollback-${env.BUILD_NUMBER} \
                                foodkart-${service}:rollback
                        """
                    }

                    echo 'All seven running image backups are ready.'
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
                            sh """
                                set -eu

                                docker build \
                                    -t foodkart-${service}:${env.BUILD_NUMBER} \
                                    .
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy and Verify with Rollback') {
            when {
                expression {
                    return env.ENABLE_CD == 'true'
                }
            }

            steps {
                withCredentials([
                    file(credentialsId: 'foodkart-restaurant-env', variable: 'RESTAURANT_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-menu-service', variable: 'MENU_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-auth-env', variable: 'AUTH_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-order-env', variable: 'ORDER_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-payment-env', variable: 'PAYMENT_SERVICE_ENV_FILE'),
                    file(credentialsId: 'foodkart-delivery-env', variable: 'DELIVERY_SERVICE_ENV_FILE')
                ]) {
                    sh '''
                        set +x
                        set -eu

                        SERVICES="restaurant-service menu-service auth-service order-service payment-service delivery-service api-gateway"

                        check_health() {
                            service="$1"
                            url="$2"
                            attempt=1

                            while [ "$attempt" -le 12 ]; do
                                if curl --fail --silent --show-error \
                                    --max-time 5 \
                                    --output /dev/null \
                                    "$url"; then
                                    echo "$service passed HTTP verification."
                                    return 0
                                fi

                                echo "$service not ready: attempt $attempt/12"
                                sleep 5
                                attempt=$((attempt + 1))
                            done

                            echo "ERROR: $service failed health verification."
                            return 1
                        }

                        verify_all() {
                            check_health restaurant-service http://foodkart-restaurant-service:8081/actuator/health || return 1
                            check_health menu-service http://foodkart-menu-service:8082/menu-items/food-items/3 || return 1
                            check_health auth-service http://foodkart-auth-service:8083/actuator/health || return 1
                            check_health order-service http://foodkart-order-service:8084/actuator/health || return 1
                            check_health payment-service http://foodkart-payment-service:8085/actuator/health || return 1
                            check_health delivery-service http://foodkart-delivery-service:8086/actuator/health || return 1
                            check_health api-gateway http://foodkart-api-gateway:8080/actuator/health || return 1
                        }

                        deploy_new_version() {
                            # Ensure all seven newly built images exist
                            # before changing any latest tags.
                            for service in $SERVICES; do
                                docker image inspect \
                                    "foodkart-$service:$BUILD_NUMBER" \
                                    >/dev/null 2>&1 || return 1
                            done

                            # Promote the new images to their Compose tags.
                            for service in $SERVICES; do
                                docker tag \
                                    "foodkart-$service:$BUILD_NUMBER" \
                                    "foodkart-$service:latest" || return 1
                            done

                            docker compose -f compose.yaml up \
                                -d \
                                --no-deps \
                                --no-build \
                                --pull never \
                                --force-recreate \
                                --wait \
                                --wait-timeout 180 \
                                $SERVICES || return 1

                            verify_all || return 1

                            return 0
                        }

                        rollback() {
                            echo "Restoring previous application images..."

                            rollback_status=0

                            for service in $SERVICES; do
                                docker image inspect \
                                    "foodkart-$service:rollback-$BUILD_NUMBER" \
                                    >/dev/null 2>&1 || {
                                        echo "ERROR: Missing rollback image for $service."
                                        rollback_status=1
                                        continue
                                    }

                                docker tag \
                                    "foodkart-$service:rollback-$BUILD_NUMBER" \
                                    "foodkart-$service:latest" || rollback_status=1
                            done

                            if [ "$rollback_status" -ne 0 ]; then
                                echo "CRITICAL: Could not restore every image tag."
                                return 1
                            fi

                            docker compose -f compose.yaml up \
                                -d \
                                --no-deps \
                                --no-build \
                                --pull never \
                                --force-recreate \
                                --wait \
                                --wait-timeout 180 \
                                $SERVICES || rollback_status=1

                            if [ "$rollback_status" -eq 0 ]; then
                                verify_all || rollback_status=1
                            fi

                            if [ "$rollback_status" -eq 0 ]; then
                                echo "Rollback completed and verified."
                                return 0
                            fi

                            echo "CRITICAL: Rollback failed or verification failed."
                            return 1
                        }

                        echo "Starting FoodKart deployment..."

                        if deploy_new_version; then
                            echo "FoodKart deployment verified successfully."
                        else
                            echo "Deployment failed. Starting rollback."

                            if rollback; then
                                echo "Previous application version restored."
                            else
                                echo "CRITICAL: Automatic rollback could not be verified."
                            fi

                            # Deliberately fail the Jenkins build even if
                            # rollback succeeds, so the release failure is visible.
                            exit 1
                        fi
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'FoodKart pipeline completed successfully.'
        }

        failure {
            echo 'FoodKart pipeline failed. Review the console output and deployment status.'
        }
    }
}
