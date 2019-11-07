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
    name = "Maven_create_carts.jar"

    artifactRules = "target => /home/taras/carts"
    buildNumberPattern = "1.0.%build.counter%"

    vcs {
        root(RelativeId("Demo2_HttpsGithubComTarasKindratCartsGitRefsHeadsMaster"))
    }

    steps {
        maven {
            goals = "clean test package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
            jdkHome = "/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.232.b09-0.el7_7.x86_64/jre"
        }
        step {
            name = "Stop carts container"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                if [ -n  ${'$'}(docker ps | awk '{print ${'$'}NF}' | grep -w carts) ]; then
                   docker stop carts
                fi
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
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
            enabled = false
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
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                #docker build uri#ref:dir
                # Get mongo-db IP
                #mongo-db-ip=${'$'}(ping -c 1 mongo-db | awk 'NR==1{print ${'$'}3}'| tr -d '('| tr -d ')')
                
                docker build https://github.com/TarasKindrat/Demo2.git#terraformInstances:Carts_Dockerfile -t carts_image:latest
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Run carts like container"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker run -d --restart unless-stopped -v /home/taras/carts:/opt --name carts --network custom-overlay -p 8081:80 carts_image:latest")
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Start carts container"
            type = "ssh-exec-runner"
            enabled = false
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", """
                if [ -z ${'$'}(docker ps | awk '{print ${'$'}NF}' | grep -w carts) ]; then
                   docker start carts
                fi
            """.trimIndent())
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Start carts service"
            type = "ssh-exec-runner"
            enabled = false
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

    features {
        feature {
            type = "JetBrains.AssemblyInfo"
            param("file-format", "%build.number%")
            param("assembly-format", "%build.number%")
        }
    }
}))

