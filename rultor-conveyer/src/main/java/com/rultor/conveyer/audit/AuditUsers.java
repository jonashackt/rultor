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
package com.rultor.conveyer.audit;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.ScheduleWithFixedDelay;
import com.jcabi.urn.URN;
import com.rultor.spi.Pulses;
import com.rultor.spi.Stand;
import com.rultor.spi.User;
import com.rultor.spi.Users;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Users with finance audit functions.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.0
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "origin")
@Loggable(Loggable.DEBUG)
@SuppressWarnings("PMD.DoNotUseThreads")
@ScheduleWithFixedDelay(threads = 2, delay = 1, unit = TimeUnit.HOURS)
public final class AuditUsers implements Users, Runnable {

    /**
     * Original users.
     */
    private final transient Users origin;

    /**
     * Public ctor.
     * @param users Users
     */
    public AuditUsers(final Users users) {
        this.origin = users;
    }

    @Override
    public Iterator<User> iterator() {
        final Iterator<User> iter = this.origin.iterator();
        return new Iterator<User>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }
            @Override
            public User next() {
                return new AuditUser(iter.next());
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public User get(final URN name) {
        return new AuditUser(this.origin.get(name));
    }

    @Override
    public Stand stand(final String name) {
        return this.origin.stand(name);
    }

    @Override
    public void run() {
        final FreeTier tier = new FreeTier();
        for (final User user : this) {
            tier.fund(user.account());
        }
    }

    @Override
    public Pulses flow() {
        return this.origin.flow();
    }

}
