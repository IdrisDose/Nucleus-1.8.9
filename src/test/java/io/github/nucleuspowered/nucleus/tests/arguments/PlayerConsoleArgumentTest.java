/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.tests.arguments;

import com.google.common.collect.Lists;
import io.github.nucleuspowered.nucleus.argumentparsers.PlayerConsoleArgument;
import io.github.nucleuspowered.nucleus.tests.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlayerConsoleArgumentTest extends TestBase {

    @Test
    public void testWhenTwoPlayersWithTheSameNameAreInTheUserDatabaseOnlyAnExactMatchIsReturned() throws ArgumentParseException {
        List<?> list = getParser().parseInternal("test" , new CommandArgs("", new ArrayList<>()));

        Assert.assertEquals(1, list.size());
        Assert.assertEquals("test", ((User)list.get(0)).getName());
    }

    @Test
    public void testWhenTwoPlayersWithTheSameNameAreInTheUserDatabaseWithNoExactMatchReturnsBothIfTheyOtherwiseMatch() throws ArgumentParseException {
        List<?> list = getParser().parseInternal("tes" , new CommandArgs("", new ArrayList<>()));
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testWhenTwoPlayersWithTheSameNameAreInTheUserDatabaseWithNoExactMatchReturnsOneReturnsIfOnlyOneMatches() throws ArgumentParseException {
        List<?> list = getParser().parseInternal("testt" , new CommandArgs("", new ArrayList<>()));
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("testtest", ((User)list.get(0)).getName());
    }

    @Test(expected = ArgumentParseException.class)
    public void testWhenTwoPlayersWithTheSameNameAreInTheUserDatabaseWithNoExactMatchReturnsNoneReturnIfNoneMatch() throws ArgumentParseException {
        getParser().parseInternal("blah" , new CommandArgs("", new ArrayList<>()));
    }

    private PlayerConsoleArgument getParser() {
        return new PlayerConsoleArgument(Text.of("test"), false, this::getOnlinePlayers);
    }

    private Collection<Player> getOnlinePlayers() {
        Player u1 = Mockito.mock(Player.class);
        Mockito.when(u1.getName()).thenReturn("test");
        Player u2 = Mockito.mock(Player.class);
        Mockito.when(u2.getName()).thenReturn("testtest");

        return Lists.newArrayList(u1, u2);
    }
}
