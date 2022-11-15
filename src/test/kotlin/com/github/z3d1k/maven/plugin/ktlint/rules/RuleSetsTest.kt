package com.github.z3d1k.maven.plugin.ktlint.rules

import com.pinterest.ktlint.core.Rule
import com.pinterest.ktlint.core.RuleProvider
import com.pinterest.ktlint.core.RuleSetProviderV2
import com.pinterest.ktlint.ruleset.experimental.ExperimentalRuleSetProvider
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider
import org.apache.commons.lang3.RandomStringUtils
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class RuleSetsTest {
    @Test
    fun `experimental ruleSet should be ignored if experimental rules are disabled`() {
        val ruleSetProviders = getRandomRuleSetProviders(10) + standardRuleSetProvider + experimentalRuleSetProvider
        val resolvedRuleSets = filterRuleSetProviders(enableExperimentalRules = false, providers = ruleSetProviders)

        assertTrue(resolvedRuleSets.none { ruleSet -> ruleSet.id == experimentalRuleSetProvider.id })
        assertEquals(ruleSetProviders.size - 1, resolvedRuleSets.size)
    }

    @Test
    fun `experimental ruleSet should be included if experimental rules are enabled`() {
        val ruleSetProviders = getRandomRuleSetProviders(10) + standardRuleSetProvider + experimentalRuleSetProvider
        val resolvedRuleSets = filterRuleSetProviders(enableExperimentalRules = true, providers = ruleSetProviders)

        assertTrue(resolvedRuleSets.any { ruleSet -> ruleSet.id == experimentalRuleSetProvider.id })
        assertEquals(ruleSetProviders.size, resolvedRuleSets.size)
    }

    companion object {
        const val lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz"

        val standardRuleSetProvider = StandardRuleSetProvider()
        val experimentalRuleSetProvider = ExperimentalRuleSetProvider()

        private fun getEmptyRuleSetProvider(): RuleSetProviderV2 =
            object : RuleSetProviderV2(RandomStringUtils.random(15, lowercaseAlphabet), NO_ABOUT) {
                override fun getRuleProviders(): Set<RuleProvider> = setOf(
                    RuleProvider { NoOpRule() }
                )
            }

        private fun getRandomRuleSetProviders(count: Int = 10): List<RuleSetProviderV2> = List(count) {
            getEmptyRuleSetProvider()
        }

        internal class NoOpRule(id: String = RandomStringUtils.random(15, lowercaseAlphabet)) : Rule(id) {
            override fun beforeVisitChildNodes(
                node: ASTNode,
                autoCorrect: Boolean,
                emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
            ) = Unit
        }
    }
}
