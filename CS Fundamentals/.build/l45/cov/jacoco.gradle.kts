plugins {
    application
    jacoco                                     // Gradle's built-in JaCoCo plugin
}

// … the rest of build.gradle.kts as before …

tasks.jacocoTestReport {
    dependsOn(tasks.test)                      // the report reads what the test run recorded
    reports {
        html.required = true                   // build/reports/jacoco/test/html/index.html
        csv.required = true                    // one row per class, easy to sum
    }
}
