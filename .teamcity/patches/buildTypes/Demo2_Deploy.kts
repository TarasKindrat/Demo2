package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_Deploy'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_Deploy")
    name = "infrastructure"
    description = "deploy to test web"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        script {
            name = "terraform"
            enabled = false
            scriptContent = """
                sudo yum -y install git;
                sudo yum -y install unzip;
                sudo yum -y install unzip;
                # check if not exist
                #if test !-f /usr/bin/terraform;
                if [ ! -f /usr/bin/terraform ]; then
                    wget https://releases.hashicorp.com/terraform/0.12.9/terraform_0.12.9_linux_amd64.zip;
                    sudo unzip ./terraform_0.12.9_linux_amd64.zip -d /usr/bin/
                    sudo rm terraform_0.12.9_linux_amd64.zip;
                fi
                terraform -v
            """.trimIndent()
        }
        script {
            name = "Create instances"
            enabled = false
            scriptContent = """
                if [ -d Demo2 ]; then
                   sudo rm -R Demo2;
                fi
                git clone -b terraformInstances https://github.com/TarasKindrat/Demo2.git;
                cd Demo2;
                sudo terraform init;
                sudo terraform fmt;
                sudo terraform plan;
                sudo terraform apply -auto-approve;
            """.trimIndent()
        }
        script {
            name = "destroy"
            enabled = false
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            scriptContent = """
                cd Demo2
                sudo terraform destroy -auto-approve;
            """.trimIndent()
        }
        script {
            name = "Check Docker version (local)"
            enabled = false
            scriptContent = "docker --version"
        }
        step {
            name = "Check Docker version mongo-db"
            type = "ssh-exec-runner"
            enabled = false
            executionMode = BuildStep.ExecutionMode.ALWAYS
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker --version")
            param("jetbrains.buildServer.deployer.targetUrl", "mongo-db")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Check Docker version web"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "docker --version")
            param("jetbrains.buildServer.deployer.targetUrl", "web")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
    }
}))

