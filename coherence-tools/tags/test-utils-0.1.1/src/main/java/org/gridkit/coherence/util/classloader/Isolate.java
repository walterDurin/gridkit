/**
 * Copyright 2011 Grid Dynamics Consulting Services, Inc.
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
package org.gridkit.coherence.util.classloader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

import org.junit.Ignore;

/**
 *	@author Alexey Ragozin (alexey.ragozin@gmail.com)
 */
@Ignore
public class Isolate {
	
	private String name;
	private Thread isolatedThread;
	private IsolatedClassloader cl;
	
	private BlockingQueue<WorkUnit> queue = new SynchronousQueue<WorkUnit>();
	
	public Isolate(String name, String... packages) {		
		this.name = name;
		this.cl = new IsolatedClassloader(getClass().getClassLoader(), packages);
	}
	
	public synchronized void start() {
		isolatedThread = new Thread(new Runner());
		isolatedThread.setName("Isolate-" + name);
		isolatedThread.setDaemon(true);
		isolatedThread.start();		
	}
	
	public void exclude(Class<?>... excludes) {
		cl.exclude(excludes);
	}
	
	public void submit(Class<?> clazz, Object... constructorArgs) {
		try {
			queue.put(new WorkUnit(clazz.getName(), constructorArgs));
			queue.put(new WorkUnit(Nop.class.getName()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			queue.put(new WorkUnit(""));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cl = null;
	}
	
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return cl.loadClass(name);
	}
	
	public ClassLoader getClassLoader() {
		return cl;
	}
	
	private class WorkUnit {
		final String className;
		final Object[] constructorArgs;
		
		public WorkUnit(String className, Object... constructorArgs) {
			this.className = className;
			this.constructorArgs = constructorArgs;
		}
	}
	
	private class Runner implements Runnable {

		@Override
		public void run() {
			Thread.currentThread().setContextClassLoader(cl);
			while(true) {
				try {
					WorkUnit unit = queue.take();
					if (unit.className.length() == 0) {
						break;
					}
					else {
						Class<?> task = cl.loadClass(unit.className);						
						Constructor<?> c = task.getConstructors()[0];
						c.setAccessible(true);
						Runnable r = (Runnable) c.newInstance(unit.constructorArgs);
						r.run();
					}
				} catch (Exception e) {
					e.printStackTrace();
				};
			}
		};
		
	}
	
	public static class Nop implements Runnable {
		public Nop() {}
		
		@Override
		public void run() {}
	}
	
	private class IsolatedClassloader extends ClassLoader {
		
		private ClassLoader baseClassloader;
		private String[] packages;
		private Set<String> excludes;
		
		public IsolatedClassloader(ClassLoader base, String[] packages) {
			super(null);
			this.baseClassloader = base;
			this.packages = packages;
			this.excludes = new HashSet<String>();
		}
		
		public void exclude(Class<?>... excludedClasses) {
			for (Class<?> clazz : excludedClasses) {
				excludes.add(clazz.getCanonicalName());
			}
		}
		
		public void clearAssertionStatus() {
			baseClassloader.clearAssertionStatus();
		}

		public URL getResource(String name) {
			return baseClassloader.getResource(name);
		}

		public InputStream getResourceAsStream(String name) {
			return baseClassloader.getResourceAsStream(name);
		}

		public Enumeration<URL> getResources(String name) throws IOException {
			return baseClassloader.getResources(name);
		}

		public void setClassAssertionStatus(String className, boolean enabled) {
			baseClassloader.setClassAssertionStatus(className, enabled);
		}

		public void setDefaultAssertionStatus(boolean enabled) {
			baseClassloader.setDefaultAssertionStatus(enabled);
		}

		public void setPackageAssertionStatus(String packageName, boolean enabled) {
			baseClassloader.setPackageAssertionStatus(packageName, enabled);
		}

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (!excludes.contains(name)) {
				for(String prefix: packages) {
					if (name.startsWith(prefix)) {
						return this.loadClass(name, false);
					}
				}
			}
			Class<?> cc = baseClassloader.loadClass(name);
			return cc;
		}

		@Override
		protected Class<?> findClass(String classname) throws ClassNotFoundException {
			try {
				String path = classname.replace('.', '/').concat(".class");
				InputStream res = baseClassloader.getResourceAsStream(path);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				byte[] buf = new byte[4096];
				while(true) {
					int x = res.read(buf);
					if (x <= 0) {
						break;
					}
					else {
						bos.write(buf, 0, x);
					}
				}
				byte[] cd = bos.toByteArray();
				Class<?> baseC = baseClassloader.loadClass(classname);
				Class<?> c = defineClass(classname, cd, 0, cd.length, baseC.getProtectionDomain());
				//System.out.println("IS-" + name + " > " + classname);
				return c;
			}
			catch(Exception e) {
				throw new ClassNotFoundException(classname);
			}
		}
	}
}