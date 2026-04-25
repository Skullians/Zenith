import net.skullian.zenith.configureRepository

plugins {
    `maven-publish`
}

publishing {
    repositories.configureRepository(project)
}
