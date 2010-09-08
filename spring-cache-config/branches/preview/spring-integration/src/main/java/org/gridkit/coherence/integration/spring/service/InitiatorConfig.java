/**
 * Copyright 2010 Grid Dynamics Consulting Services, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gridkit.coherence.integration.spring.service;

import com.tangosol.coherence.component.util.daemon.queueProcessor.service.peer.initiator.TcpInitiator;
import com.tangosol.io.Serializer;

/**
 * Coherence*Extend configuration fragment
 * @author malexejev@gmail.com
 * 07.09.2010
 */
public class InitiatorConfig {
	
	@ReflectionInjectedProperty("__m_OutgoingMessageHandlerConfig")
	private OutgoingMessageHandlerConfig outgoingMessageHandlerConfig;
	
	@ReflectionInjectedProperty("__m_Serializer")
	private Serializer serializer;
	
	@ReflectionInjectedProperty("__m_TcpInitiator")
	private TcpInitiator tcpInitiator;

	public OutgoingMessageHandlerConfig getOutgoingMessageHandlerConfig() {
		return outgoingMessageHandlerConfig;
	}

	public void setOutgoingMessageHandlerConfig(
			OutgoingMessageHandlerConfig outgoingMessageHandlerConfig) {
		this.outgoingMessageHandlerConfig = outgoingMessageHandlerConfig;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public TcpInitiator getTcpInitiator() {
		return tcpInitiator;
	}

	public void setTcpInitiator(TcpInitiator tcpInitiator) {
		this.tcpInitiator = tcpInitiator;
	}
	
}
