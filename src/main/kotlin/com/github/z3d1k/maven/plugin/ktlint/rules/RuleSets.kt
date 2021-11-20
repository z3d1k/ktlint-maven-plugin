package com.github.z3d1k.maven.plugin.ktlint.rules

import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import java.util.ServiceLoader

fun resolveRuleSets(
    enableExperimentalRules: Boolean = false,
    providers: Iterable<RuleSetProvider> = ServiceLoader.load(RuleSetProvider::class.java)
): List<RuleSet> {
    return providers
        .filter { provider -> provider !is ExperimentalRuleSetProvider || enableExperimentalRules }
        .sortedWith(compareBy { if (it is StandardRuleSetProvider) 0 else 1 })
        .map(RuleSetProvider::get)
}
