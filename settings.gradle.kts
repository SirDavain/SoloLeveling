pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

/*toolchainManagement {
    jvm {
        javaRepositories {
            gradlePluginPortal() // For foojay-resolver plugin
            mavenCentral()
            // You can also add specific Adoptium/Azul Zulu/etc. repositories if needed
            // Example for Adoptium:
            // maven { url = uri("https://api.adoptium.net/v3/maven-repository") }
        }
    }
}*/

rootProject.name = "TheSYSTEM"
include(":app")
 