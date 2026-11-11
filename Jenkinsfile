
pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    environment {
        ENABLE_CD = 'true'
        HAS_ROLLBACK = 'false'
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
                sh 'bash --version'
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

                        for file in \
                            "$RESTAURANT_ENV" \
                            "$MENU_ENV" \
                            "$AUTH_ENV" \
                            "$ORDER_ENV" \
                            "$PAYMENT_ENV" \
                            "$DELIVERY_ENV"
                        do
                            if [ ! -s "$file" ]; then
                                echo "ERROR: Missing or empty credential file."
                                exit 1
                            fi
                        done

                        echo "All deployment credentials are accessible."
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

        stage('Validate Docker Compose') {
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
                        docker network inspect foodkart-network >/dev/null

                        echo "Docker Compose and network validated."
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
                        echo "ERROR: Cannot verify remote master."
                        exit 1
                    fi

                    if [ "$CURRENT_COMMIT" != "$REMOTE_MASTER" ]; then
                        echo "ERROR: Checkout differs from remote master."
                        exit 1
                    fi

                    echo "Deployment authorized."
                '''
            }
        }

        stage('Backup Existing Containers') {
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

                    def count = sh(
                        script: '''
                            set -eu
                            count=0

                            for service in \
                                restaurant-service \
                                menu-service \
                                auth-service \
                                order-service \
                                payment-service \
                                delivery-service \
                                api-gateway
                            do
                                if docker container inspect \
                                    "foodkart-$service" >/dev/null 2>&1; then
                                    count=$((count + 1))
                                fi
                            done

                            echo "$count"
                        ''',
                        returnStdout: true
                    ).trim()

                    if (count == '0') {
                        env.HAS_ROLLBACK = 'false'

                        echo 'FIRST DEPLOYMENT DETECTED.'
                        echo 'No existing microservice containers.'
                        echo 'Skipping backup stage.'
                    }

                    else if (count == '7') {
                        echo 'Existing deployment detected.'
                        echo 'Backing up existing Docker images (running or stopped)...'

                        for (service in services) {
                            withEnv([
                                "SERVICE_NAME=${service}",
                                "IMAGE_TAG=${env.BUILD_NUMBER}"
                            ]) {
                                sh '''
                                    set -eu

                                    container="foodkart-$SERVICE_NAME"

                                    image_id=$(docker inspect \
                                        --format '{{.Image}}' \
                                        "$container")

                                    if [ -z "$image_id" ]; then
                                        echo "ERROR: Could not find image for $container"
                                        exit 1
                                    fi

                                    docker tag "$image_id" \
                                        "foodkart-$SERVICE_NAME:rollback-$IMAGE_TAG"

                                    echo "Backup created for $SERVICE_NAME"
                                '''
                            }
                        }

                        env.HAS_ROLLBACK = 'true'

                        echo 'All seven rollback images preserved.'
                    }

                    else {
                        error(
                            "Partial deployment detected: ${count}/7 containers exist. " +
                            "Deployment stopped to prevent inconsistent updates."
                        )
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
                            withEnv([
                                "SERVICE_NAME=${service}",
                                "IMAGE_TAG=${env.BUILD_NUMBER}"
                            ]) {
                                sh '''
                                    set -eu

                                    docker build \
                                        -t "foodkart-$SERVICE_NAME:$IMAGE_TAG" \
                                        .
                                '''
                            }
                        }
                    }

                    echo 'All seven Docker images built successfully.'
                }
            }
        }

        stage('Deploy and Verify') {
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

                        # ---------------------------------------
                        # HTTP Health Check
                        # ---------------------------------------

                        check_health() {
                            service="$1"
                            url="$2"
                            attempt=1

                            while [ "$attempt" -le 12 ]; do
                                if curl \
                                    --fail \
                                    --silent \
                                    --show-error \
                                    --max-time 5 \
                                    --output /dev/null \
                                    "$url"; then

                                    echo "$service is healthy."
                                    return 0
                                fi

                                echo "Waiting for $service ($attempt/12)"
                                sleep 5
                                attempt=$((attempt + 1))
                            done

                            echo "ERROR: $service health check failed."
                            return 1
                        }

                        # ---------------------------------------
                        # Menu Service TCP Readiness Check
                        # ---------------------------------------

                        check_menu_tcp() {
                            attempt=1

                            while [ "$attempt" -le 12 ]; do
                                if bash -c \
                                    'echo > /dev/tcp/foodkart-menu-service/8082' \
                                    2>/dev/null; then

                                    echo "Menu Service port 8082 is accepting connections."
                                    return 0
                                fi

                                echo "Waiting for Menu Service ($attempt/12)"
                                sleep 5
                                attempt=$((attempt + 1))
                            done

                            echo "ERROR: Menu Service TCP readiness check failed."
                            return 1
                        }

                        # ---------------------------------------
                        # Verify All Seven Microservices
                        # ---------------------------------------

                        verify_all() {
                            check_health restaurant-service \
                                http://foodkart-restaurant-service:8081/actuator/health || return 1

                            check_menu_tcp || return 1

                            check_health auth-service \
                                http://foodkart-auth-service:8083/actuator/health || return 1

                            check_health order-service \
                                http://foodkart-order-service:8084/actuator/health || return 1

                            check_health payment-service \
                                http://foodkart-payment-service:8085/actuator/health || return 1

                            check_health delivery-service \
                                http://foodkart-delivery-service:8086/actuator/health || return 1

                            check_health api-gateway \
                                http://foodkart-api-gateway:8080/actuator/health || return 1
                        }

                        # ---------------------------------------
                        # Docker Image Retention
                        # Keep latest + current rollback only
                        # ---------------------------------------

                        cleanup_old_images() {
                            echo "Starting Docker image cleanup..."

                            for service in $SERVICES; do
                                repository="foodkart-$service"
                                keep_rollback=""

                                if [ "$HAS_ROLLBACK" = "true" ]; then
                                    keep_rollback="rollback-$BUILD_NUMBER"
                                fi

                                docker image ls "$repository" --format '{{.Tag}}' |
                                while IFS= read -r tag; do

                                    [ -n "$tag" ] || continue

                                    if [ "$tag" = "latest" ] ||
                                       { [ -n "$keep_rollback" ] && [ "$tag" = "$keep_rollback" ]; }; then

                                        echo "Keeping $repository:$tag"

                                    else
                                        echo "Removing $repository:$tag"

                                        docker image rm "$repository:$tag" >/dev/null 2>&1 ||
                                            echo "WARNING: Could not remove $repository:$tag"
                                    fi
                                done
                            done

                            echo "Docker image cleanup completed."
                        }

                        # ---------------------------------------
                        # Deploy New Docker Images
                        # ---------------------------------------

                        deploy_new() {
                            echo "Checking new Docker images..."

                            for service in $SERVICES; do
                                docker image inspect \
                                    "foodkart-$service:$BUILD_NUMBER" \
                                    >/dev/null 2>&1 || return 1
                            done

                            echo "Promoting new images to latest..."

                            for service in $SERVICES; do
                                docker tag \
                                    "foodkart-$service:$BUILD_NUMBER" \
                                    "foodkart-$service:latest" || return 1
                            done

                            echo "Creating or updating Docker containers..."

                            docker compose -f compose.yaml up \
                                -d \
                                --no-deps \
                                --no-build \
                                --pull never \
                                --force-recreate \
                                --wait \
                                --wait-timeout 180 \
                                $SERVICES || return 1

                            echo "Verifying deployed microservices..."

                            verify_all || return 1

                            echo "FOODKART DEPLOYMENT VERIFIED."

                            cleanup_old_images

                            return 0
                        }

                        # ---------------------------------------
                        # Restore Previous Docker Images
                        # ---------------------------------------

                        rollback() {
                            echo "Restoring previous application images..."

                            for service in $SERVICES; do
                                docker image inspect \
                                    "foodkart-$service:rollback-$BUILD_NUMBER" \
                                    >/dev/null 2>&1 || return 1
                            done

                            for service in $SERVICES; do
                                docker tag \
                                    "foodkart-$service:rollback-$BUILD_NUMBER" \
                                    "foodkart-$service:latest" || return 1
                            done

                            echo "Recreating containers using previous images..."

                            docker compose -f compose.yaml up \
                                -d \
                                --no-deps \
                                --no-build \
                                --pull never \
                                --force-recreate \
                                --wait \
                                --wait-timeout 180 \
                                $SERVICES || return 1

                            echo "Verifying rollback..."

                            verify_all || return 1

                            echo "Previous version restored successfully."

                            cleanup_old_images

                            return 0
                        }

                        # ---------------------------------------
                        # Deployment Execution
                        # ---------------------------------------

                        echo "Starting FoodKart deployment..."

                        if deploy_new; then
                            echo "FOODKART DEPLOYMENT SUCCESSFUL."
                        else
                            echo "FOODKART DEPLOYMENT FAILED."

                            if [ "$HAS_ROLLBACK" = "true" ]; then
                                echo "Starting automatic rollback..."

                                if rollback; then
                                    echo "Rollback successful."
                                else
                                    echo "CRITICAL: Rollback failed."
                                    echo "Preserving available Docker images for manual recovery."
                                fi
                            else
                                echo "First deployment: no previous version exists."
                                echo "Inspect containers and application logs."
                            fi

                            exit 1
                        fi
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'FoodKart CI/CD pipeline completed successfully.'
        }

        failure {
            echo 'FoodKart CI/CD pipeline failed. Check console output.'
        }
    }
}
