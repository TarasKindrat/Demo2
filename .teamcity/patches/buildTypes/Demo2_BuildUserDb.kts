package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_BuildUserDb'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_BuildUserDb")
    name = "Build user-db"

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
                docker build -f user/docker/user-db/Dockerfile user/docker/user-db/ -t user-db_image;
            """.trimIndent()
        }
        script {
            name = "Tag image"
            scriptContent = """
                docker tag user-db_image gcr.io/demo2-256511/user-db_image:%build.number%;
                docker tag user-db_image gcr.io/demo2-256511/user-db_image:latest;
            """.trimIndent()
        }
        script {
            name = "Push images to Container Registry"
            scriptContent = """
                docker push gcr.io/demo2-256511/user-db_image:%build.number%;
                docker push gcr.io/demo2-256511/user-db_image:latest;
            """.trimIndent()
        }
        script {
            name = "Delete images"
            scriptContent = """
                docker images;
                docker rmi user-db_image:latest;
                docker rmi gcr.io/demo2-256511/user-db_image:%build.number%;
                docker rmi gcr.io/demo2-256511/user-db_image:latest;
                docker images;
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }
}))

