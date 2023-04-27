properties([[$class: 'JiraProjectProperty'], gitLabConnection(gitLabConnection: 'https://gitlab.sezinno.ru/', jobCredentialId: ''), [$class: 'GitlabLogoProperty', repositoryName: ''], [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], parameters([choice(choices: ['build', 'skip build'], description: 'Выберете необходима ли сборка образа', name: 'BUILD'), [$class: 'NodeParameterDefinition', allowedSlaves: [], defaultSlaves: ['prod1-node'], name: 'NODES', nodeEligibility: [$class: 'AllNodeEligibility'], triggerIfResult: 'multiSelectionDisallowed'], string(defaultValue: '000', description: 'Необходимо вписать в поле версию сборки jenkins', name: 'VERSION_B'), string(defaultValue: 'v0.0.1', description: '''Необходимо написать версию проекта gitlab
 ''', name: 'VERSION_P'), choice(choices: ['develop', 'feature', 'release'], description: 'Выберете необходимую ветку', name: 'BRANCH_N'), string(defaultValue: 'develop/v0.0.1', description: 'Выберете ветки из какой стоит запустить сборку', name: 'BRANCH_B')])])
pipeline {
  agent none
  environment {
    IMAGE_BASE = 'ru.innopolis/lam/lam_server/'
    BUILD_NUM = ".$BUILD_NUMBER"
    IMAGE_NAME = "${env.IMAGE_BASE}:${env.BUILD_NUM}"
    IMAGE_NAME_LATEST = "${env.IMAGE_BASE}:latest"
    DOCKERFILE_NAME = "Dockerfile"
    GIT_BRANCH = "v$GIT_BRANCH"
    GIT_BRANCH_LOCAL = "v$GIT_BRANCH_LOCAL"
    VERSION_B = "$BUILD_NUMBER"
  }
  stages {
    stage('Build/Push') {
      when {  expression { "${env.BUILD}" == 'build'} }
      agent {label "jenkins"}
      steps {
        cleanWs()
        script {
          def (value0, value1, value2) = "${env.GIT_BRANCH}".tokenize( '/' )
          PROJECT_B = value1
          PROJECT_V = "${env.GIT_BRANCH.replaceFirst(/^.*\//, '')}"
          MAIN_T = "${env.IMAGE_BASE}${PROJECT_B}:${PROJECT_V}${env.BUILD_NUM}"
          checkout scm
          def dockerImage = docker.build("${MAIN_T}", "-f ${env.DOCKERFILE_NAME} .")
          docker.withRegistry('http://nexus-docker1.sezinno.ru', 'nexus-docker1.sezinno.ru') {
            dockerImage.push()
            dockerImage.push("latest")
          cleanWs disableDeferredWipeout: true
          }
          echo "Pushed Docker Image : ${MAIN_T}"
        }
        sh 'docker image prune -a -f --filter "until=2h"'  
        sh 'docker container prune -f --filter "until=2h"'
      }
    }
    stage('Deploy') {
      agent { label "${NODES}"}
        steps{

          script { 
           checkout scm
           docker.withRegistry('http://nexus-docker1.sezinno.ru', 'nexus-docker1.sezinno.ru'){
           sh "docker compose pull"
           sh "docker compose up -d --force-recreate" 
          }
        }
      }
    } 
  }
}
