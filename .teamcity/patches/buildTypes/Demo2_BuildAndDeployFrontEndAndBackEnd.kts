package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_BuildAndDeployFrontEndAndBackEnd'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_BuildAndDeployFrontEndAndBackEnd")
    name = "Build and deploy front-end and back-end"

    vcs {
        root(RelativeId("Demo2_HttpsGithubComTarasKindratDemo2gitRefsHeadsMaster"))
    }

    steps {
        step {
            name = "Build and deploy  catalogue"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                git clone https://github.com/TarasKindrat/catalogue.git;
                docker build --no-cache -f catalogue/docker/catalogue/Dockerfile -t catalogue:latest .
                docker run -d --restart unless-stopped --name catalogue --network custom-overlay -p 8080:80 catalogue:latest
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
    }

    triggers {
        vcs {
        }
    }
}))

