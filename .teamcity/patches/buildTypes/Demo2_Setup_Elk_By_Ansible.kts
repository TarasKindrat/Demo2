package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_Setup_Elk_By_Ansible'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_Setup_Elk_By_Ansible")
    name = "Setup ELK by Ansible"

    vcs {
        root(RelativeId("Demo2_HttpsGithubComTarasKindratDemo2gitRefsHeadsMaster"))
    }

    steps {
        script {
            name = "Run Ansible role ELK"
            scriptContent = """
                if [ -d Demo2 ]; then
                   sudo rm -R Demo2;
                fi
                git clone -b ansible https://github.com/TarasKindrat/Demo2.git;
                cd Demo2;
                ansible-playbook install_ELK.yml
            """.trimIndent()
        }
    }

    triggers {
        finishBuildTrigger {
            buildType = "Demo2_RunContainersWithAnsible"
            successfulOnly = true
        }
    }
}))

