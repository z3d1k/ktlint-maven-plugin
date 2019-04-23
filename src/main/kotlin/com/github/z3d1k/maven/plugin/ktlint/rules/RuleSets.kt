package com.github.z3d1k.maven.plugin.ktlint.rules

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider
import java.util.ServiceLoader

fun resolveRuleSets(
    enableExperimentalRules: Boolean = false,
    providers: Iterable<RuleSetProvider> = ServiceLoader.load(RuleSetProvider::class.java)
): List<RuleSet> {
    return providers
        .map(RuleSetProvider::get)
        .filter { ruleSet -> ruleSet.id != "experimental" || enableExperimentalRules }
        .sortedWith(compareBy { if (it.id == "standard") 0 else 1 })
}
