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

    vcsRoot(HttpsGithubComTarasKindratCartsGitRefsHeadsMaster)

    buildType(Build)
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
        script {
            name = "Stop carts service"
            scriptContent = "ssh -oStrictHostKeyChecking=no taras@${'$'}webIP 'sudo systemctl stop carts'"
        }
        script {
            name = "copy to remote"
            scriptContent = "scp -oStrictHostKeyChecking=no target/carts.jar taras@webIP:/opt"
        }
        script {
            name = "Reload systemctl"
            scriptContent = """ssh -oStrictHostKeyChecking=no taras${'$'}webIP \'sudo systemctl daemon-reload\'"""
        }
        script {
            name = "Run carts"
            scriptContent = """ssh -oStrictHostKeyChecking=no taras@"$webIP" \'sudo systemctl start carts\'"""
        }
    }

    triggers {
        vcs {
        }
        finishBuildTrigger {
            buildType = "${Build.id}"
            successfulOnly = true
        }
    }

    dependencies {
        artifacts(RelativeId("Build")) {
            buildRule = lastSuccessful()
            artifactRules = "target => target"
            enabled = false
        }
    }
})

object HttpsGithubComTarasKindratCartsGitRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/TarasKindrat/carts.git#refs/heads/master"
    url = "https://github.com/TarasKindrat/carts.git"
})
