pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"
                password = "sk.eyJ1IjoiZ2Vvcmdld2hpdHRpbmd0b24yMCIsImEiOiJjbHBlZHZvNzQxNDgxMmpwcmh5Z2d0dWxpIn0.mYWeKpLXE9ykzjXODxTKOg"
            }
        }
    }
}

rootProject.name = "Fitness App"
include(":app")
