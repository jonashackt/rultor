/**
 * Copyright (c) 2009-2014, rultor.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the rultor.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.rultor.repo;

import com.google.common.collect.ImmutableMap;
import com.jcabi.aspects.Tv;
import com.rultor.spi.Arguments;
import com.rultor.spi.Coordinates;
import com.rultor.spi.Users;
import com.rultor.spi.Variable;
import com.rultor.spi.Wallet;
import java.util.Arrays;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link Dictionary}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCoupling (500 lines)
 */
public final class DictionaryTest {

    /**
     * Dictionary can make an instance.
     * @throws Exception If some problem inside
     */
    @Test
    public void makesInstance() throws Exception {
        final String key = "some key";
        final Variable<Map<String, Object>> var = new Dictionary(
            new ImmutableMap.Builder<Variable<String>, Variable<?>>()
                .put(new Text(key), new Constant<Integer>(Tv.TEN))
                .build()
        );
        MatcherAssert.assertThat(
            var.instantiate(
                Mockito.mock(Users.class),
                new Arguments(
                    Mockito.mock(Coordinates.class), new Wallet.Empty()
                )
            ),
            Matchers.<String, Object>hasEntry(key, Tv.TEN)
        );
    }

    /**
     * Dictionary can make a text.
     * @throws Exception If some problem inside
     */
    @Test
    public void makesText() throws Exception {
        final Variable<Map<String, Object>> var = new Dictionary(
            new ImmutableMap.Builder<Variable<String>, Variable<?>>()
                .put(new Text("one"), new Constant<Long>((long) Tv.TEN))
                .put(new Text("two"), new Text("some text\nline two"))
                .put(
                    new Alter("three"),
                    new Composite(
                        "com.rultor.SomeOtherClass",
                        Arrays.<Variable<?>>asList()
                    )
                )
                .build()
        );
        MatcherAssert.assertThat(
            var.asText(),
            Matchers.equalTo(
                // @checkstyle StringLiteralsConcatenation (5 lines)
                "{\n"
                + "  \"one\": 10L,\n"
                + "  \"two\": \"some text\\nline two\",\n"
                + "  @(\"three\"): com.rultor.SomeOtherClass()\n"
                + "}"
            )
        );
    }

}
