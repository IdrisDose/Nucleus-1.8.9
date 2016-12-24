/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.modules.rules.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class RulesConfig {

    @Setting(value = "rules", comment = "loc:config.rules.ruleset")
    private List<String> ruleSet = Lists.newArrayList("Be respectful.", "Be ethical.", "Use common sense.");

    public List<String> getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(List<String> ruleSet) {
        this.ruleSet = ruleSet;
    }
}
