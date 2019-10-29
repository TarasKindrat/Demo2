package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_CreateOverlayNetwork'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_CreateOverlayNetwork")
    name = "Create overlay network"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        step {
            name = "Swarm for overlay on web"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                # Get web's inernal IP
                #self_ip=${'$'}(ifconfig | grep 'inet 10' | cut -d' ' -f10);
                #swarm_token=${'$'}(docker swarm init --advertise-addr=${'$'}self_ip | awk 'NR==3{print ${'$'}0}'| tr -d 'docker ');
                
                swarm_token=${'$'}(docker swarm init | awk 'NR==4{print ${'$'}0}');
                echo "Swarm token is ${'$'}swarm_token"
                # Set teamcity environment variable
                echo "##teamcity[setParameter name='env.SWARM_TOKEN' value='${'$'}swarm_token']"
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Swarm for overlay on mongo-db"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                # Join to swarm
                docker ${'$'}(echo %env.SWARM_TOKEN%);
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Create Overlay (custom-overlay) on web"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                docker network create --driver=overlay --attachable custom-overlay;
                docker network ls
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
    }
}))

