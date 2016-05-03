/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc and individual contributors
 * by the @authors tag. 
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.media.control.mgcp.transaction;

import static org.mockito.Mockito.*;

import org.junit.Test;

import static org.junit.Assert.*;
import org.mobicents.media.control.mgcp.command.MgcpCommand;
import org.mobicents.media.control.mgcp.command.MgcpCommandProvider;
import org.mobicents.media.control.mgcp.listener.MgcpTransactionListener;
import org.mobicents.media.control.mgcp.message.MessageDirection;
import org.mobicents.media.control.mgcp.message.MgcpRequest;
import org.mobicents.media.control.mgcp.message.MgcpResponse;
import org.mobicents.media.control.mgcp.network.MgcpChannel;

/**
 * @author Henrique Rosa (henrique.rosa@telestax.com)
 *
 */
public class MgcpTransactionTest {

    private static final String RESPONSE = "12345 200 OK";

    @Test
    public void testInboundRequest() {
        // given
        MgcpRequest request = mock(MgcpRequest.class);
        MgcpResponse response = mock(MgcpResponse.class);
        MgcpCommandProvider commands = mock(MgcpCommandProvider.class);
        MgcpCommand command = mock(MgcpCommand.class);
        MgcpChannel channel = mock(MgcpChannel.class);
        MgcpTransactionListener listener = mock(MgcpTransactionListener.class);

        MgcpTransaction transaction = new MgcpTransaction(commands, channel, listener);
        transaction.setId(12345);

        // when
        when(response.toString()).thenReturn(RESPONSE);
        when(commands.provide(request)).thenReturn(command);
        when(command.execute(request)).thenReturn(response);
        transaction.processRequest(request, MessageDirection.INBOUND);
        // FIXME have to manually invoke the callback
        transaction.onCommandComplete(response);

        // then
        assertEquals(12345, transaction.getId());
        assertEquals(Integer.toHexString(12345), transaction.getHexId());
        verify(command, times(1)).execute(request);
        verify(listener, times(1)).onTransactionComplete(transaction);
        verify(channel, times(1)).queue(RESPONSE.getBytes());
    }

}
