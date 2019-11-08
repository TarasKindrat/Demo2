package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_Build_Carts'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_Build_Carts")
    name = "Build Carts"

    artifactRules = "target => target"
    buildNumberPattern = "0.0%build.counter%"

    vcs {
        root(RelativeId("Demo2_HttpsGithubComTarasKindratCartsGitRefsHeadsMaster"))
    }

    steps {
        script {
            name = "Download carts repo by git"
            workingDir = "/home/taras/carts"
            scriptContent = """
                if [ -d carts ]; then
                   sudo rm -R carts;
                fi
                git clone https://github.com/TarasKindrat/carts.git;
            """.trimIndent()
        }
        maven {
            name = "Create carts.jar"
            goals = "clean test package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            jdkHome = "/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.232.b09-0.el7_7.x86_64/jre"
        }
        script {
            name = "Copy carts.jar"
            scriptContent = """
                if [ -f /home/taras/carts/carts/target/carts.jar ]; then
                   rm /home/taras/carts/carts/target/carts.jar
                fi
                cp target/carts.jar /home/taras/carts/carts/target
            """.trimIndent()
        }
        script {
            name = "Download and build docker_image"
            workingDir = "/home/taras/carts"
            scriptContent = """
                #if [ -d carts ]; then
                #   sudo rm -R carts;
                #fi
                #git clone https://github.com/TarasKindrat/carts.git;
                docker build -f carts/Dockerfile carts/ -t carts_image;
                
                #docker build https://github.com/TarasKindrat/Demo2.git#terraformInstances:Carts_Dockerfile -t carts_image
            """.trimIndent()
        }
        script {
            name = "Tag image"
            scriptContent = """
                docker tag carts_image gcr.io/demo2-256511/carts_image:%build.number%;
                docker tag carts_image gcr.io/demo2-256511/carts_image:latest;
            """.trimIndent()
        }
        script {
            name = "Push images to Container Registry"
            scriptContent = """
                docker push gcr.io/demo2-256511/carts_image:%build.number%;
                docker push gcr.io/demo2-256511/carts_image:latest;
            """.trimIndent()
        }
        script {
            name = "Delete images from build server"
            scriptContent = """
                docker rmi carts_image:latest;
                docker rmi gcr.io/demo2-256511/carts_image:%build.number%;
                docker rmi gcr.io/demo2-256511/carts_image:latest;
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }
}))

