package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_Build_Carts'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_Build_Carts")
    name = "Build Carts"

    buildNumberPattern = "0.0%build.counter%"

    steps {
        script {
            name = "Download and build docker_image"
            scriptContent = "docker build https://github.com/TarasKindrat/Demo2.git#terraformInstances:Carts_Dockerfile -t carts_image"
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

    dependencies {
        snapshot(RelativeId("Demo2_BuildCarts")) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }
}))

