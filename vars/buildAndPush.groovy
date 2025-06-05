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
    if (currentVersion == "") {
        y = 0
    } else {
        y = currentVersion.toInteger() + 1
    }
    echo "New version to be pushed: ${y}"
}