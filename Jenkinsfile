@Library('piper-lib-os') _
pipeline {
  agent any
  options {
    buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '5', numToKeepStr: '10')
  }
  stages {
    stage("Init Build") {
      steps {
        deleteDir()
        checkout scm
        setupCommonPipelineEnvironment script: this
      }
    }
    stage('MTA Build') {
      steps {
        mtaBuild(
        script: this, buildTarget: 'CF', platform: 'CF')
      }
    }
    stage('Archiving artifacts') {
      steps {
        archiveArtifacts artifacts: '**/*.mtar',
        fingerprint: true
      }
    }
  }
}
