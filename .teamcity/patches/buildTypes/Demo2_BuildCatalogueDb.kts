package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_BuildCatalogueDb'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_BuildCatalogueDb")
    name = "Build catalogue-db"

    buildNumberPattern = "0.0%build.counter%"

    vcs {
        root(RelativeId("Demo2_Catalogue"))
    }

    steps {
        script {
            name = "Download and build docker_image"
            scriptContent = """
                if [ -d catalogue ]; then
                   sudo rm -R catalogue;
                fi
                git clone https://github.com/TarasKindrat/catalogue.git;
                docker build -f catalogue/docker/catalogue-db/Dockerfile catalogue/docker/catalogue-db/ -t catalogue-db_image;
            """.trimIndent()
        }
        script {
            name = "Tag image"
            scriptContent = """
                docker tag catalogue-db_image gcr.io/demo2-256511/catalogue-db_image:%build.number%;
                docker tag catalogue-db_image gcr.io/demo2-256511/catalogue-db_image:latest;
            """.trimIndent()
        }
        script {
            name = "Push images to Container Registry"
            scriptContent = """
                docker push gcr.io/demo2-256511/catalogue-db_image:%build.number%;
                docker push gcr.io/demo2-256511/catalogue-db_image:latest;
            """.trimIndent()
        }
        script {
            name = "Delete images from build server"
            scriptContent = """
                docker rmi catalogue-db_image:latest;
                docker rmi gcr.io/demo2-256511/catalogue-db_image:%build.number%;
                docker rmi gcr.io/demo2-256511/catalogue-db_image:latest;
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = ""
        }
    }
}))

