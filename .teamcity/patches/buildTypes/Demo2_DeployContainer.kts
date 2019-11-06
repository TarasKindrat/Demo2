package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_DeployContainer'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_DeployContainer")
    name = "Deploy databace's containers"

    buildNumberPattern = "0.0%build.counter%"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        step {
            name = "Run mongodb3.4 like carts-db"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker run -d --restart unless-stopped --name carts-db --network custom-overlay mongo:3.4")
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Run mongodb3.4 like orders-db"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker run -d --restart unless-stopped --name orders-db --network custom-overlay mongo:3.4")
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Run catalogue-db:0.3.0 like catalogue-db"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker run -d --restart unless-stopped --name catalogue-db --network custom-overlay catalogue-db:latest")
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Run user-db"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                git clone https://github.com/TarasKindrat/user.git;
                docker build -f user/docker/user-db/Dockerfile user/docker/user-db/ -t user-db_image:latest
                docker run -d --restart unless-stopped --name user-db --network custom-overlay user-db_image:latest
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        script {
            name = "Build catalogue-db"
            enabled = false
            workingDir = "/home/taras/"
            scriptContent = """
                git clone https://github.com/TarasKindrat/catalogue.git;
                docker build -f catalogue/docker/catalogue-db/Dockerfile catalogue/docker/catalogue-db/ -t catalogue-db:%build.number%
            """.trimIndent()
        }
        script {
            name = "build user-db"
            scriptContent = """
                git clone https://github.com/TarasKindrat/user.git;
                docker build -f user/docker/user-db/Dockerfile user/docker/user-db/ -t user-db_image;
                docker tag user-db_image gcr.io/demo2-256511/user-db_image::%build.number%;
                docker push gcr.io/demo2-256511/user-db_image:%build.number%;
                docker push gcr.io/demo2-256511/user-db_image:latest;
            """.trimIndent()
        }
    }
}))

