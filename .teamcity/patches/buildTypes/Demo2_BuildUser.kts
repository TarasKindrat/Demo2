package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_BuildUser'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_BuildUser")
    name = "Build user"

    buildNumberPattern = "0.0%build.counter%"

    vcs {
        root(RelativeId("Demo2_UserGit"))
    }

    steps {
        script {
            name = "Download and build docker_image"
            scriptContent = """
                if [ -d user ]; then
                   sudo rm -R user;
                fi
                git clone https://github.com/TarasKindrat/user.git;
                docker build --no-cache -f user/docker/user/Dockerfile-release user/ -t user_image
            """.trimIndent()
        }
        script {
            name = "Tag image"
            scriptContent = """
                docker tag user_image gcr.io/demo2-256511/user_image:%build.number%;
                docker tag user_image gcr.io/demo2-256511/user_image:latest;
            """.trimIndent()
        }
        script {
            name = "Push images to Container Registry"
            scriptContent = """
                docker push gcr.io/demo2-256511/user_image:%build.number%;
                docker push gcr.io/demo2-256511/user_image:latest;
            """.trimIndent()
        }
        script {
            name = "Delete images"
            scriptContent = """
                docker rmi user_image:latest;
                docker rmi gcr.io/demo2-256511/user_image:%build.number%;
                docker rmi gcr.io/demo2-256511/user_image:latest;
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
        finishBuildTrigger {
            buildType = "Demo2_BuildCatalogueDb"
            successfulOnly = true
        }
    }
}))

