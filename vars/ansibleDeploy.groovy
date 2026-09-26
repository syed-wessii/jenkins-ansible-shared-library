def config

def readConfig(String configFile) {
    config = readProperties file: configFile

    echo "Configuration loaded"
    echo "Environment: ${config.ENVIRONMENT}"
    echo "Code Base Path: ${config.CODE_BASE_PATH}"
}

def cloneCode() {
    echo "Cloning project code"
    checkout scm
}

def userApproval() {
    def keepApprovalStage = config.KEEP_APPROVAL_STAGE.toBoolean()

    if (keepApprovalStage) {
        input message: "Deploy to ${config.ENVIRONMENT}?", ok: "Approve"
    } else {
        echo "Approval stage skipped"
    }
}

def executePlaybook() {
    dir(config.CODE_BASE_PATH) {
        sh 'ansible-playbook -i hosts assignment6.yml'
    }
}

def sendNotification() {
    slackSend(
        channel: config.SLACK_CHANNEL_NAME,
        message: config.ACTION_MESSAGE
    )

    emailext(
        to: config.EMAIL_RECIPIENT,
        subject: "Jenkins Deployment - ${config.ENVIRONMENT}",
        body: """Deployment completed successfully.

Environment: ${config.ENVIRONMENT}
Message: ${config.ACTION_MESSAGE}
Build: ${env.JOB_NAME} #${env.BUILD_NUMBER}
"""
    )
}
