package com.javiersc.semver.project.gradle.plugin

/**
 * @param message The short message of the commit
 * @param fullMessage The full message of the commit including the body
 * @param hash The hash of the commit
 * @param timestampEpochSecond The timestamp of the commit in epoch seconds
 * @param tags The tags associated with the commit
 */
public data class Commit(
    val message: String,
    val fullMessage: String,
    val hash: String,
    val timestampEpochSecond: Long,
    val tags: List<Tag>,
) {
    override fun toString(): String =
        """
            $fullMessage
            
            hash: $hash, tags: ${tags.joinToString()}
        """
            .trimIndent()
}

/**
 * @param name The name of the tag, example: `v1.0.0`
 * @param refName The name of the ref, example: `refs/tags/v1.0.0`
 */
public data class Tag(val name: String, val refName: String) {
    override fun toString(): String = name
}
