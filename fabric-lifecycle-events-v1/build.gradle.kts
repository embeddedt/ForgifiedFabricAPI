val withClientSourceSet: () -> Unit by extra

withClientSourceSet()

mixin {
    config("fabric-lifecycle-events-v1.mixins.json")
    config("fabric-lifecycle-events-v1.client.mixins.json")
}

dependencies { 
    api(project(":fabric-api-base"))
}