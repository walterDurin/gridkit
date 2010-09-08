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

import java.util.Collection;

import com.tangosol.coherence.component.util.daemon.queueProcessor.service.peer.acceptor.TcpAcceptor;
import com.tangosol.io.Serializer;
import com.tangosol.util.Filter;

/**
 *  Coherence*Extend configuration fragment
 *	@author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class AcceptorConfig {

	@XmlConfigProperty("connection-limit")
	private Integer connectionLimit;
	
	@ReflectionInjectedProperty("__m_OutgoingMessageHandlerConfig")
	private OutgoingMessageHandlerConfig outgoingMessageHandlerConfig;
	
	@ReflectionInjectedProperty("__m_Serializer")
	private Serializer serializer;
	
	@ReflectionInjectedProperty("__m_TcpAcceptor")
	private TcpAcceptor tcpAcceptor;
	
	@XmlConfigProperty("filters")
	private Collection<Filter> filters;

	public Integer getConnectionLimit() {
		return connectionLimit;
	}

	public void setConnectionLimit(Integer connectionLimit) {
		this.connectionLimit = connectionLimit;
	}

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

	public TcpAcceptor getTcpAcceptor() {
		return tcpAcceptor;
	}

	public void setTcpAcceptor(TcpAcceptor tcpAcceptor) {
		this.tcpAcceptor = tcpAcceptor;
	}

	public Collection<Filter> getFilters() {
		return filters;
	}

	public void setFilters(Collection<Filter> filters) {
		this.filters = filters;
	}
	
}
