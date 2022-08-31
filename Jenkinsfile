pipeline {
    agent any
    environment {
      APPLICATION_NAME = "springdata/app"
    }

    stages {
        stage('1 Build project') {
            steps {
                echo "Build project"
                sh "pwd"
                sh "gradle build -x test"

            }
        }
        stage('2 Build docker-compose.yml') {
            steps {
                echo "Build docker-compose.yml"
                sh "docker-compose build"
            }
        }
        stage('3 Build Dockerfile') {
            steps {
                echo "Build Dockerfile"
                sh "docker build -t $APPLICATION_NAME ."
            }
        }
    }
}