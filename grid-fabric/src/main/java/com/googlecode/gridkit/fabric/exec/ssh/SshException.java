/**
 * Copyright 2008-2009 Grid Dynamics Consulting Services, Inc.
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
package com.googlecode.gridkit.fabric.exec.ssh;

import java.io.IOException;

import com.jcraft.jsch.JSchException;

public class SshException extends IOException {

	private static final long serialVersionUID = 20090415L;

	public SshException(JSchException e) {
		initCause(e);
	}

	public SshException(String message, JSchException e) {
		super(message);
		initCause(e);
	}
}
