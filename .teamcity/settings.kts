import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2019.1"

project {

    vcsRoot(HttpsGithubComTarasKindratDemo1gitRefsHeadsMaster)
    vcsRoot(HttpsGithubComTarasKindratCartsGitRefsHeadsMaster)

    buildType(Build)
    buildType(Deploy)
    buildTypesOrder = arrayListOf(Deploy, Build)
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target => target"

    vcs {
        root(HttpsGithubComTarasKindratCartsGitRefsHeadsMaster)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
        maven {
            name = "jark"
            goals = "package"
        }
        step {
            name = "Stop carts service (1)"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "sudo systemctl stop carts")
            param("jetbrains.buildServer.deployer.targetUrl", "10.166.0.5")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "copy to remote"
            type = "ssh-deploy-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.deployer.sourcePath", "target/carts.jar")
            param("jetbrains.buildServer.deployer.targetUrl", "10.166.0.5:/home/taras")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.deployer.ssh.transport", "jetbrains.buildServer.deployer.ssh.transport.scp")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Reload systemctl"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "sudo systemctl daemon-reload")
            param("jetbrains.buildServer.deployer.targetUrl", "10.166.0.5")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
        step {
            name = "Run carts"
            type = "ssh-exec-runner"
            param("jetbrains.buildServer.deployer.username", "taras")
            param("jetbrains.buildServer.sshexec.command", "sudo systemctl start carts")
            param("jetbrains.buildServer.deployer.targetUrl", "10.166.0.5")
            param("jetbrains.buildServer.sshexec.authMethod", "CUSTOM_KEY")
            param("jetbrains.buildServer.sshexec.keyFile", "/home/taras/.ssh/id_rsa")
        }
    }

    triggers {
        vcs {
        }
        finishBuildTrigger {
            enabled = false
            buildType = "${Build.id}"
            successfulOnly = true
        }
    }

    dependencies {
        snapshot(Deploy) {
            reuseBuilds = ReuseBuilds.ANY
            onDependencyCancel = FailureAction.ADD_PROBLEM
        }
        artifacts(RelativeId("Build")) {
            buildRule = lastSuccessful()
            artifactRules = "target => target"
            enabled = false
        }
    }
})

object Deploy : BuildType({
    name = "infrastructure"
    description = "deploy to test web"

    vcs {
        root(HttpsGithubComTarasKindratDemo1gitRefsHeadsMaster)
    }

    steps {
        script {
            name = "terraform"
            workingDir = "/opt"
            scriptContent = """
                sudo yum -y install git;
                sudo yum -y install unzip;
                sudo yum -y install unzip;
                # check if not exist
                if test !-f /usr/bin/terraform;
                 then 
                    wget https://releases.hashicorp.com/terraform/0.12.9/terraform_0.12.9_linux_amd64.zip;
                    sudo unzip ./terraform_0.12.9_linux_amd64.zip -d /usr/bin/
                    sudo rm terraform_0.12.9_linux_amd64.zip;
                fi
                terraform -v
                sudo rm -R Demo1;
                git clone https://github.com/TarasKindrat/Demo1.git;
                cd Demo1;
                sudo terraform fmt;
                sudo terraform init;
                sudo terraform plan;
                sudo terraform apply -auto-approve;
            """.trimIndent()
        }
        script {
            name = "destroy"
            executionMode = BuildStep.ExecutionMode.RUN_ON_FAILURE
            workingDir = "/opt"
            scriptContent = "sudo terraform destroy -auto-approve;"
        }
    }
})

object HttpsGithubComTarasKindratCartsGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/TarasKindrat/carts.git#refs/heads/master"
    url = "https://github.com/TarasKindrat/carts.git"
})

object HttpsGithubComTarasKindratDemo1gitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/TarasKindrat/Demo1.git#refs/heads/master"
    url = "https://github.com/TarasKindrat/Demo1.git"
})
