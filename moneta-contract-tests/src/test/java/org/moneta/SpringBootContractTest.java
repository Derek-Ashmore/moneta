/*
 * This software is licensed under the Apache License, Version 2.0
 * (the "License") agreement; you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.moneta;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang3.SystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Tests the spring boot deployment.  All tests are inherited.
 * @author D. Ashmore
 *
 */
public class SpringBootContractTest extends ContractTestSuite {
	
	static Executor executor;
	static ExecuteWatchdog  watchdog;
	static DefaultExecuteResultHandler resultHandler;
	
	static Thread appThread;
	
	public SpringBootContractTest() {
		super("http://localhost:8080/moneta/");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		appThread = new Thread(new MyRunnable());
		appThread.setDaemon(true);
		appThread.start();
		Thread.sleep(8000);
		System.out.println("Test sequence starting....");
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		watchdog.destroyProcess();
		System.out.println("Test sequence ending.");
	}
	
	static class MyRunnable implements Runnable {

		public void run() {
			try {
			executor = new DaemonExecutor();
			resultHandler = new DefaultExecuteResultHandler();
			String javaHome = System.getProperty("java.home");
			String userDir = System.getProperty("user.dir");
			
			executor.setStreamHandler(new PumpStreamHandler(System.out));
			watchdog = new ExecuteWatchdog(15000);
			executor.setWatchdog(watchdog);
			executor.execute(new CommandLine(javaHome + SystemUtils.FILE_SEPARATOR 
					+ "bin"+ SystemUtils.FILE_SEPARATOR+"java.exe").addArgument("-version"));
			executor.execute(new CommandLine(javaHome + SystemUtils.FILE_SEPARATOR 
					+ "bin"+ SystemUtils.FILE_SEPARATOR+"java.exe")
				.addArgument("-jar")
				.addArgument(userDir + "/../moneta-springboot/target/moneta-springboot-0.0.1-SNAPSHOT.jar"));
			
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}


}
