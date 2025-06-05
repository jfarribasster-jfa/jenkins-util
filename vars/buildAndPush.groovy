#!/usr/bin/env groovy
/*
    * Jenkins Pipeline Utility to build and push Docker images.
    * This script builds a Docker image from the current directory and pushes it to a specified Docker registry.
    *
    * Parameters:
    * - ECR: The name of the ecr in wich image will be pulled.
    * - DOCKERFILE: Dockerfile to build.
    * - NAME: Docker image name to build and push, in the format 'repository:version'.
    * - CACHE: Whatevewr to reuse cache or not.
*/
def call(String ECR, String DOCKERFILE, String NAME, String CACHE) {
    
    repository = NAME.split(':')[0]
    version = NAME.split(':')[1]
           
    // getting current las t version
    def currentVersion = sh(script: "aws ecr describe-images --repository-name tfm/${repository} --region us-east-1 --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]' --output text", returnStdout: true).trim()
    echo "Current version in ECR: ${currentVersion}"
    // If the current version is not the same as the one provided, we update the version    

    echo "Current version in ECR: ${currentVersion}"
    // Determinar nueva versión
    def y
    if (!currentVersion || currentVersion == "None") {
        y = 0
    } else {
        try {
            y = currentVersion.toInteger() + 1
        } catch (Exception e) {
            error "La versión actual (${currentVersion}) no es un número válido."
        }
    }

    if (${CACHE} == "no") {
        echo "Building image with cache..."
        sh """
            docker build --no-cache -t ${ECR}${NAME}.${y} -t ${ECR}${NAME}  -f ${DOCKERFILE} .
        """
    } else {
        echo "Building image without cache..."
        sh """
            docker build -t ${ECR}${NAME}.${y} -t ${ECR}${NAME}  -f ${DOCKERFILE} .
        """
    }
}