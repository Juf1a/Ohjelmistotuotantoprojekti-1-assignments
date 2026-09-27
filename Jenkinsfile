pipeline {
    agent any
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Juf1a/Ohjelmistotuotantoprojekti-1-assignments.git'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn clean install' 
            }
        }
        stage('Test') {git 'https://github.com/Juf1a/Ohjelmistotuotantoprojekti-1-assignments.git'
            steps {
                sh 'mvn test'
            }
        }
        stage('Code Coverage') {
            steps {
                sh 'mvn jacoco:report'
            }
        }
        stage('Publish Test Results') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }
        stage('Publish Coverage Report') {
            steps {
                jacoco()
            }
        }
        // Docker deploy stages come from the lecture demo 
}