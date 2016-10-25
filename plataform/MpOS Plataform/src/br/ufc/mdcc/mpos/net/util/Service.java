/*******************************************************************************
 * Copyright (C) 2014 Philipp B. Costa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.ufc.mdcc.mpos.net.util;

/**
 * Represent a service in server
 * 
 * @author Philipp B. Costa
 */
public class Service {
	private String name;
	private int port;

	public Service(String name, int port) {
		this.name = name;
		this.port = port;
	}

	public final String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public final int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}