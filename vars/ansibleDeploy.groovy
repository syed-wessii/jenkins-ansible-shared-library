def call(String configFile) {

    node {

        stage('Clone') {
            checkout scm
        }

        def config = readProperties file: configFile

        def slackChannel = config.SLACK_CHANNEL_NAME
        def environment = config.ENVIRONMENT
        def codeBasePath = config.CODE_BASE_PATH
        def actionMessage = config.ACTION_MESSAGE
        def keepApprovalStage = config.KEEP_APPROVAL_STAGE.toBoolean()

        stage('User Approval') {
            if (keepApprovalStage) {
                input message: "Deploy to ${environment}?", ok: "Approve"
            } else {
                echo "Approval stage skipped"
            }
        }

        stage('Playbook Execution') {
            dir(codeBasePath) {
                sh 'ansible-playbook -i hosts site.yml'
            }
        }

        stage('Notification') {
            echo "Sending notification"
            echo "Channel: ${slackChannel}"
            echo "Message: ${actionMessage}"
        }

        echo "Starting Ansible deployment"
        echo "Environment: ${environment}"
        echo "Code Base Path: ${codeBasePath}"
        echo "Slack Channel: ${slackChannel}"
        echo "Action Message: ${actionMessage}"
        echo "Keep Approval Stage: ${keepApprovalStage}"
    }
}
