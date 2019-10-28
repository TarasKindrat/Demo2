package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_BuildCarts'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_BuildCarts")
    name = "Build_Carts"

    artifactRules = "target => target"

    vcs {
        root(RelativeId("Demo2_HttpsGithubComTarasKindratCartsGitRefsHeadsMaster"))
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            jdkHome = "/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.232.b09-0.el7_7.x86_64/jre"
        }
        step {
            name = "Stop carts service"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "sudo systemctl stop carts")
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Upload carts to remote web host"
            type = "ssh-deploy-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.deployer.sourcePath", "target/carts.jar")
            param("jetbrains.buildServer.deployer.targetUrl", "web:/home/taras/carts")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.deployer.ssh.transport", "jetbrains.buildServer.deployer.ssh.transport.scp")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Create carts image"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                #docker build uri#ref:dir
                docker build https://github.com/TarasKindrat/Demo2.git#terraformInstances:Carts_Dockerfile -t carts_image:latest
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Run carts like container"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker run -d --rm -v /home/taras/carts:/opt — name carts -p 8081:8081 carts_image")
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Start carts service"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "sudo systemctl start carts")
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

