pipeline {
    agent any
    environment {
      APPLICATION_NAME = "springdata/app"
    }

    stages {
        stage('1 Build docker-compose.yml') {
            steps {
                echo "Build docker-compose.yml"
                sh "docker-compose build"
            }
        }
        stage('2 Build project') {
            steps {
                echo "Build project"
                sh "pwd"
                sh "ls -la"
                sh "gradle build -x test"

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