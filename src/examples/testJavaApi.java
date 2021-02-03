import java.io.*;

import de.independit.scheduler.shell.*;
import de.independit.scheduler.server.output.*;

public class testJavaApi {

        public static void main(String[] args) {

                SDMSServerConnection serverConnection = new SDMSServerConnection("localhost", 2506, "SYSTEM", "G0H0ME");
                SDMSOutput output = null;

                try {
                        output = serverConnection.connect(null);
                } catch (IOException ioe) {
                        System.err.println("Error '" + ioe.toString() + "' establishing BICsuite server connection");
                        System.exit(1);
                }
                if (output.error != null) {
                        System.err.println("Error '" + output.error.code + ":" + output.error.message + "' connecting to BICsuite server");
                        System.exit(1);
                }

		String command = "SHOW SYSTEM";
                output = serverConnection.execute(command);
                if (output.error != null) {
                        System.err.println("Error '" + output.error.code + ":" + output.error.message + "' executing command: " + command);
                        System.exit(1);
                }

                System.out.println("Version: " + SDMSOutputUtil.getFromRecord(output,"VERSION"));
		int workers = SDMSOutputUtil.getTableLength(output,"WORKER");
                System.out.println("Workers: " + workers);
		for (int i = 0; i < workers; i ++) {
                	System.out.println("  Name: " +  SDMSOutputUtil.getFromTable(output, "WORKER", i, "NAME"));
		}

		command = "LIST SESSION";
                output = serverConnection.execute(command);
                if (output.error != null) {
                        System.err.println("Error '" + output.error.code + ":" + output.error.message + "' executing command: " + command);
                        System.exit(1);
                }
		int sessions = SDMSOutputUtil.getTableLength(output);
                System.out.println("Sessions: " + sessions);
		for (int i = 0; i < sessions; i ++) {
                	System.out.println("  User: " +  SDMSOutputUtil.getFromTable(output, i, "USER"));
		}

                try {
                        serverConnection.finish();
                } catch (IOException ioe) {
                        System.err.println("Error '" + ioe.toString() + "' closing BICsuite server connection");
                        System.exit(1);
                }
        }
}
