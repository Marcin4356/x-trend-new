pipeline {
    agent {
        node {
            label 'maven'
        }
    }
envinronment {
    PATH = "/opt/apache-maven-3.9.10/bin:$PATH"
}

    stages {
        stage("build"){
            steps {
                sh 'mvn clen deploy'
            }
        }
    }
}
