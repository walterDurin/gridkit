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
package com.griddynamics.coherence.integration.spring.config2;

import org.springframework.beans.factory.BeanNameAware;

import com.griddynamics.coherence.integration.spring.CoherenceCacheScheme;
import com.tangosol.run.xml.XmlElement;

/**
 * @author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
public class DistributedSchemeBean implements CoherenceCacheScheme, BeanNameAware {

	private String beanName;
	private DistributeCacheServiceDefinition service;
	private XmlElement scheme;
	
	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setService(DistributeCacheServiceDefinition service) {
		this.service = service;
	}
	
	@Override
	public String getSchemeName() {
		return beanName;
	}
	
	@Override
	public XmlElement getXmlConfig(String scope) {
		return null;
	}
}