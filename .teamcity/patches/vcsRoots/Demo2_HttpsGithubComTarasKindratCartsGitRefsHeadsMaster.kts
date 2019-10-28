package patches.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2018_2.*
import jetbrains.buildServer.configs.kotlin.v2018_2.ui.*
import jetbrains.buildServer.configs.kotlin.v2018_2.vcs.GitVcsRoot

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, create a vcsRoot with id = 'Demo2_HttpsGithubComTarasKindratCartsGitRefsHeadsMaster'
in the project with id = 'Demo2', and delete the patch script.
*/
create(RelativeId("Demo2"), GitVcsRoot({
    id("Demo2_HttpsGithubComTarasKindratCartsGitRefsHeadsMaster")
    name = "https://github.com/TarasKindrat/carts.git#refs/heads/master"
    url = "https://github.com/TarasKindrat/carts.git"
}))

