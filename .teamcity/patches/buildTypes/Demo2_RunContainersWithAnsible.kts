package patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2018_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a buildType with id = 'Demo2_RunContainersWithAnsible'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), BuildType({
    id("Demo2_RunContainersWithAnsible")
    name = "Run containers with Ansible"

    steps {
        script {
            name = "Clone git repo"
            workingDir = "home/taras/ter_ansib"
            scriptContent = """
                cd home/taras/ter_ansib;
                if [ -d Demo2 ]; then
                   sudo rm -R Demo2;
                fi
                git clone -b terraformInstances https://github.com/TarasKindrat/Demo2.git;
            """.trimIndent()
        }
        script {
            name = "Run ansible playbook for deploy db containes"
            enabled = false
            workingDir = "home/taras/ter_ansib/Demo2"
            scriptContent = "ansible-playbook run_databases.yml -vvvv"
        }
        script {
            name = "Run ansible playbook for deploy db containes (1)"
            workingDir = "home/taras/ter_ansib/Demo2"
            scriptContent = "ansible-playbook run_web.yml -vvvv"
        }
    }
}))

