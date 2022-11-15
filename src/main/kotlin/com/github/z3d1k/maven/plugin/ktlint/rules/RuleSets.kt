package com.github.z3d1k.maven.plugin.ktlint.rules

import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSetProviderV2
import com.pinterest.ktlint.ruleset.experimental.experimentalRulesetId
import java.util.ServiceLoader

fun resolveRuleProviders(
    enableExperimentalRules: Boolean = false,
    providers: Iterable<RuleSetProviderV2> = ServiceLoader.load(RuleSetProviderV2::class.java)
): Set<RuleProvider> {
    return filterRuleSetProviders(enableExperimentalRules, providers)
        .flatMap { provider -> provider.getRuleProviders() }
        .toSet()
}

fun filterRuleSetProviders(
    enableExperimentalRules: Boolean,
    providers: Iterable<RuleSetProviderV2>
) = providers.filter { provider -> enableExperimentalRules || provider.id != experimentalRulesetId }
