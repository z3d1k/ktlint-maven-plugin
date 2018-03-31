package com.github.z3d1k.maven.plugin.ktlint.rules

import com.github.shyiko.ktlint.core.RuleSet
import com.github.shyiko.ktlint.core.RuleSetProvider
import java.util.ServiceLoader

fun resolveRuleSets(
    providers: Iterable<RuleSetProvider> = ServiceLoader.load(RuleSetProvider::class.java)
): List<RuleSet> {
    return providers
            .map { it.get() }
            .sortedWith(compareBy { if (it.id == "standard") 0 else 1 })
}
