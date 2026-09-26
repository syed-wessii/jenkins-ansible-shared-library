@Library('ansible-shared-library') _

pipeline {

    agent any

    stages {

        stage('Read Config') {
            steps {
                script {
                    ansibleDeploy.readConfig('config/prod.conf')
                }
            }
        }

        stage('Clone') {
            steps {
                script {
                    ansibleDeploy.cloneCode()
                }
            }
        }

        stage('User Approval') {
            steps {
                script {
                    ansibleDeploy.userApproval()
                }
            }
        }

        stage('Playbook Execution') {
            steps {
                script {
                    ansibleDeploy.executePlaybook()
                }
            }
        }
    }

    post {
        always {
            script {
                ansibleDeploy.sendNotification()
            }
        }
    }
}
