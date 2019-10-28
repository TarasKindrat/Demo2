package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_DeployContainer'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_DeployContainer")
    name = "Deploy_container"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        step {
            name = "Run mongodb3.4"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker run -d mongo:3.4 -p 27017:27017")
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
    }
}))

