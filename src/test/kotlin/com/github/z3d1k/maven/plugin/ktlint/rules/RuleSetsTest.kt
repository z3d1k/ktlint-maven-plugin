package com.github.z3d1k.maven.plugin.ktlint.rules

import com.pinterest.ktlint.core.Rule
import com.pinterest.ktlint.core.RuleSet
import com.pinterest.ktlint.core.RuleSetProvider
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
        val resolvedRuleSets = resolveRuleSets(enableExperimentalRules = false, providers = ruleSetProviders)

        assertTrue(resolvedRuleSets.none { ruleSet -> ruleSet.id == experimentalRuleSetProvider.get().id })
        assertEquals(ruleSetProviders.size - 1, resolvedRuleSets.size)
    }

    @Test
    fun `experimental ruleSet should be included if experimental rules are enabled`() {
        val ruleSetProviders = getRandomRuleSetProviders(10) + standardRuleSetProvider + experimentalRuleSetProvider
        val resolvedRuleSets = resolveRuleSets(enableExperimentalRules = true, providers = ruleSetProviders)

        assertTrue(resolvedRuleSets.any { ruleSet -> ruleSet.id == experimentalRuleSetProvider.get().id })
        assertEquals(ruleSetProviders.size, resolvedRuleSets.size)
    }

    @Test
    fun `standard ruleSet should be first`() {
        val ruleSetProviders = getRandomRuleSetProviders() + standardRuleSetProvider + experimentalRuleSetProvider
        val resolvedRuleSets = resolveRuleSets(providers = ruleSetProviders)

        assertEquals(resolvedRuleSets.first().id, standardRuleSetProvider.get().id)
    }

    companion object {
        const val lowercaseAlphabet = "abcdefghijklmnopqrstuvwxyz"

        val standardRuleSetProvider = StandardRuleSetProvider()
        val experimentalRuleSetProvider = ExperimentalRuleSetProvider()

        private fun getEmptyRuleSetProvider(): RuleSetProvider = object : RuleSetProvider {
            override fun get(): RuleSet = RuleSet(RandomStringUtils.random(15, lowercaseAlphabet), NoOpRule())
        }

        private fun getRandomRuleSetProviders(count: Int = 10): List<RuleSetProvider> = List(count) {
            getEmptyRuleSetProvider()
        }

        internal class NoOpRule(id: String = RandomStringUtils.random(15, lowercaseAlphabet)) : Rule(id) {
            override fun visit(
                node: ASTNode,
                autoCorrect: Boolean,
                emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> Unit
            ) = Unit
        }
    }
}
